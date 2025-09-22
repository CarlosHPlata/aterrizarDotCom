package com.aterrizar.integration.test.general

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import spock.lang.Specification
import spock.lang.Unroll

class DigitalVisaFlowTest extends Specification {
    
    @Unroll
    def "should request visa number for flights to #countryName (#countryCode)"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with flight to visa-required country"
        def session = checkin.initSession("MX", [flightCode])
        InitVerifier.verify(session)

        and: "Continue with passport but no visa"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "Should be asked for visa number"
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        where:
        countryCode | countryName | flightCode
        "IN"        | "India"     | "USJFKINDEL"  // US to India
        "AU"        | "Australia" | "USJFKAUSYD"  // US to Australia
    }

    @Unroll
    def "should complete entire flow for #countryName visa-required destination"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with flight to visa-required country"
        def session = checkin.initSession("MX", [flightCode])
        InitVerifier.verify(session)

        and: "Continue without any fields"
        def continueResponse = session.proceed()
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: "Fill passport"
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        and: "Fill visa number"
        continueResponse = session.fillUserInput([(UserInput.VISA_NUMBER): visaNumber])
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: "Sign agreement"
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: "Should complete successfully"
        ContinueVerifier.completed(continueResponse)

        where:
        countryCode | countryName | flightCode   | visaNumber
        "IN"        | "India"     | "USJFKINDEL" | "INDIAVISA2024"
        "AU"        | "Australia" | "USJFKAUSYD" | "AUSSIEVISA2024"
    }

    @Unroll
    def "should complete flow with valid visa number for #countryName"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with flight to visa-required country"
        def session = checkin.initSession("MX", [flightCode])
        InitVerifier.verify(session)

        and: "Fill passport number"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        and: "Fill visa number"
        continueResponse = session.fillUserInput([(UserInput.VISA_NUMBER): visaNumber])
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: "Sign agreement"
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: "Should complete successfully"
        ContinueVerifier.completed(continueResponse)

        where:
        countryCode | countryName | flightCode   | visaNumber
        "IN"        | "India"     | "USJFKINDEL" | "VISA123456"
        "AU"        | "Australia" | "USJFKAUSYD" | "VISA789012"
    }

    @Unroll
    def "should request visa when connecting through #countryName"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with connection through visa-required country"
        def session = checkin.initSession("MX", [connectionFlight, finalFlight])
        InitVerifier.verify(session)

        and: "Fill passport number"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "Should be asked for visa number due to connection"
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        when: "Fill visa number and agreement"
        continueResponse = session.fillUserInput([
            (UserInput.VISA_NUMBER): "TRANSIT${countryCode}2024",
            (UserInput.AGREEMENT_SIGNED): "true"
        ])

        then: "Should complete successfully"
        ContinueVerifier.completed(continueResponse)

        where:
        countryCode | countryName | connectionFlight | finalFlight
        "IN"        | "India"     | "USJFKINDEL"     | "INDELGBLON"  // US->India->UK
        "AU"        | "Australia" | "USJFKAUSYD"     | "AUSYDNZNZL"  // US->Australia->NZ
    }

    def "should not request visa number for flights to UK"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with flight to UK"
        def session = checkin.initSession("MX", ["USJFKGBLHR"]) // US to UK flight
        InitVerifier.verify(session)

        and: "Continue with passport"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "Should skip visa and ask for agreement"
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)
    }

    def "should request visa number when any flight destination requires it"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with mixed flights (UK and India)"
        def session = checkin.initSession("MX", ["USJFKGBLHR", "GBLHRINDEL"]) // US->UK, UK->India
        InitVerifier.verify(session)

        and: "Continue with passport but no visa"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "Should be asked for visa number due to India destination"
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)
    }

    def "should complete flow for multiple visa-required destinations"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with flights to both India and Australia"
        def session = checkin.initSession("MX", ["USJFKINDEL", "INDELAUSYD"]) // US->India, India->Australia
        InitVerifier.verify(session)

        and: "Fill all required information"
        def continueResponse = session.fillUserInput([
            (UserInput.PASSPORT_NUMBER): "A12345678",
            (UserInput.VISA_NUMBER): "MULTIVISA123"
        ])
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: "Sign agreement"
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: "Should complete successfully"
        ContinueVerifier.completed(continueResponse)
    }

    @Unroll
    def "should handle multiple flight formats for #countryName destinations"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session with various flight formats to visa-required country"
        def session = checkin.initSession("MX", flightCodes)
        InitVerifier.verify(session)

        and: "Fill passport and visa information"
        def continueResponse = session.fillUserInput([
            (UserInput.PASSPORT_NUMBER): "A12345678",
            (UserInput.VISA_NUMBER): "VISA${countryCode}2024",
            (UserInput.AGREEMENT_SIGNED): "true"
        ])

        then: "Should complete successfully"
        ContinueVerifier.completed(continueResponse)

        where:
        countryCode | countryName | flightCodes
        "IN"        | "India"     | ["USJFKINDEL"]                    // Single flight to India
        "IN"        | "India"     | ["USJFKGBLHR", "GBLHRINDEL"]     // Via UK to India
        "AU"        | "Australia" | ["USJFKAUSYD"]                    // Single flight to Australia
        "AU"        | "Australia" | ["USJFKGBLHR", "GBLHRAUSYD"]     // Via UK to Australia
    }
}