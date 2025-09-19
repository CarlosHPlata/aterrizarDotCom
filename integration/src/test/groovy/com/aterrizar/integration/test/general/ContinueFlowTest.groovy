package com.aterrizar.integration.test.general

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification

class ContinueFlowTest extends Specification {
    def "should reject if userid does not match"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and:
        session.setUserId(UUID.randomUUID().toString())
        session.proceed()

        then:
        def e = thrown(HttpClientErrorException.BadRequest)
        ContinueVerifier.rejected(e, "User ID does not match session")
    }

    def "should be asked to provide passport number when sending no passport information"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: // continue without filling anything
        def continueResponse = session.proceed()

        and: // be asked to fill passport
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: // fill passport
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: //be able to continue
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)
    }

    def "should be asked to sign agreement"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: // continue but filling passport
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: // be asked to sign agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: // fill agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // be able to continue
        ContinueVerifier.completed(continueResponse)
    }

    def "should be asked to sign health clear acknowledgment if destination requires it"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX", List.of("USXXXCNYYY"))
        InitVerifier.verify(session)

        and: // continue but filling passport
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: // continue but signing agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        and: // be asked to sign health clear acknowledgment
        ContinueVerifier.requiredField(continueResponse, UserInput.HEALTH_CLEAR_ACKNOWLEDGEMENT)

        and: // fill health clear acknowledgment
        continueResponse = session.fillUserInput([(UserInput.HEALTH_CLEAR_ACKNOWLEDGEMENT): "true"])

        then: // be able to continue
        ContinueVerifier.completed(continueResponse)
    }

    def "should not continue if health clear acknowledgment is false and destination requires it"() {
        setup:
        def checkin = Checkin.create()

        when:
        // init session with a destination that requires health clearance
        def session = checkin.initSession("MX", List.of("USXXXCNYYY"))
        InitVerifier.verify(session)

        and: // continue but filling passport
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: // continue but signing agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        and: // be asked to sign health clear acknowledgment
        ContinueVerifier.requiredField(continueResponse, UserInput.HEALTH_CLEAR_ACKNOWLEDGEMENT)

        and: // fill health acknowledgment as false
        continueResponse = session.fillUserInput([(UserInput.HEALTH_CLEAR_ACKNOWLEDGEMENT): "false"])

        then: // be rejected
        def e = thrown(HttpClientErrorException.BadRequest)
        ContinueVerifier.rejected(e, "Health clear acknowledgement is required")
    }

    def "should complete the entire flow"() {

        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: // continue without filling anything
        def continueResponse = session.proceed()
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: // fill passport
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: // be asked to sign agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: //  fill agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // be completed
        ContinueVerifier.completed(continueResponse)
    }
}
