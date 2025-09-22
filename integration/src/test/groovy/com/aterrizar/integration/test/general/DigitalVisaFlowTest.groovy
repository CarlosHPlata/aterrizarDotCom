package com.aterrizar.integration.test.general

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import spock.lang.Specification
import spock.lang.Unroll

class DigitalVisaFlowTest extends Specification {
    
    @Unroll
    def "should request visa number for flights to #country"() {
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
        country     | flightCode
        "India"     | "USJFKINDEL"
        "Australia" | "USJFKAUSYD"
    }

    @Unroll
    def "should complete flow for #country"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession("MX", [flightCode])
        InitVerifier.verify(session)

        and: "Fill passport"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        and: "Fill visa"
        continueResponse = session.fillUserInput([(UserInput.VISA_NUMBER): "VISA123456"])
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: "Sign agreement"
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: "Should complete"
        ContinueVerifier.completed(continueResponse)

        where:
        country     | flightCode
        "India"     | "USJFKINDEL"
        "Australia" | "USJFKAUSYD"
    }

    def "should not request visa for UK flights"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKGBLHR"])
        InitVerifier.verify(session)

        and:
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then:
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)
    }

    def "should request visa when any flight requires it"() {
        setup:
        def checkin = Checkin.create()

        when: "Mixed flights: UK + India"
        def session = checkin.initSession("MX", ["USJFKGBLHR", "GBLHRINDEL"])
        InitVerifier.verify(session)

        and:
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "Visa required due to India"
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)
    }

    def "should complete flow for multiple visa destinations"() {
        setup:
        def checkin = Checkin.create()

        when: "Flights to both India and Australia"
        def session = checkin.initSession("MX", ["USJFKINDEL", "INDELAUSYD"])
        InitVerifier.verify(session)

        and: "Fill all required information at once"
        def continueResponse = session.fillUserInput([
            (UserInput.PASSPORT_NUMBER): "A12345678",
            (UserInput.VISA_NUMBER): "MULTIVISA123",
            (UserInput.AGREEMENT_SIGNED): "true"
        ])

        then: "Should complete"
        ContinueVerifier.completed(continueResponse)
    }
}