package com.aterrizar.integration.test.general

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import spock.lang.Specification

class DigitalVisaFlowTest extends Specification {
    
    def "should request visa number for flights to India"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKINDEL"]) // US to India flight
        InitVerifier.verify(session)

        and: // continue with passport but no visa
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: // should be asked for visa number
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)
    }

    def "should request visa number for flights to Australia"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKAUSYD"]) // US to Australia flight
        InitVerifier.verify(session)

        and: // continue with passport but no visa
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: // should be asked for visa number
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)
    }

    def "should not request visa number for flights to UK"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKGBLHR"]) // US to UK flight
        InitVerifier.verify(session)

        and: // continue with passport
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: // should skip visa and ask for agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)
    }

    def "should complete flow with valid visa number for India"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKINDEL"])
        InitVerifier.verify(session)

        and: // continue without filling anything
        def continueResponse = session.proceed()
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: // fill passport
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        and: // fill visa number
        continueResponse = session.fillUserInput([(UserInput.VISA_NUMBER): "INDVISA123456"])
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: // sign agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // should complete successfully
        ContinueVerifier.completed(continueResponse)
    }

    def "should complete flow with valid visa number for Australia"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKAUSYD"])
        InitVerifier.verify(session)

        and: // continue without filling anything
        def continueResponse = session.proceed()
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: // fill passport
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        and: // fill visa number
        continueResponse = session.fillUserInput([(UserInput.VISA_NUMBER): "AUSVISA789012"])
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: // sign agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // should complete successfully
        ContinueVerifier.completed(continueResponse)
    }

    def "should handle mixed destinations with visa requirement"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKGBLHR", "GBLHRINDEL"]) // US->UK, UK->India
        InitVerifier.verify(session)

        and: // fill passport first
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: // should be asked for visa number due to India destination
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        when: // fill visa and agreement
        continueResponse = session.fillUserInput([
            (UserInput.VISA_NUMBER): "MULTIVISA345",
            (UserInput.AGREEMENT_SIGNED): "true"
        ])

        then: // should complete successfully
        ContinueVerifier.completed(continueResponse)
    }

    def "should skip visa validation for default flights"() {
        setup:
        def checkin = Checkin.create()

        when: // using default flights (no visa required)
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: // continue with passport
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: // should skip visa and go to agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)
    }

    def "should complete entire flow step by step for visa-required destination"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", ["USJFKINDEL"])
        InitVerifier.verify(session)

        and: // start without any information
        def continueResponse = session.proceed()

        then: // first ask for passport
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        when: // provide passport
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: // then ask for visa
        ContinueVerifier.requiredField(continueResponse, UserInput.VISA_NUMBER)

        when: // provide visa
        continueResponse = session.fillUserInput([(UserInput.VISA_NUMBER): "VISA123456"])

        then: // then ask for agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        when: // sign agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // should complete
        ContinueVerifier.completed(continueResponse)
    }
}