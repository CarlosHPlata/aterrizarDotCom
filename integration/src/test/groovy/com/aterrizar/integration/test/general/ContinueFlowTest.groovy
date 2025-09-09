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
        InitVerifier.verity(session)

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
        InitVerifier.verity(session)

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
        InitVerifier.verity(session)

        and: // continue but filling passport
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: // be asked to sign agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: // fill agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // be able to continue
        ContinueVerifier.completed(continueResponse)
    }

    def "should complete the entire flor"() {

        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verity(session)

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
