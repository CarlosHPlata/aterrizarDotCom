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

    def "should work with a passport starting with G"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: // provide valid passport (starts with G)
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "G3567"])

        and: // sign agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // flow completes successfully
        ContinueVerifier.completed(continueResponse)
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

    def "should reject invalid passport starting with P"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: // provide invalid passport (starts with P)
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "P7399"])

        then: // system currently accepts it and asks for agreement (this is the bug)
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        when: // sign agreement with invalid passport
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // flow completes (but shouldn't with invalid passport)
        ContinueVerifier.completed(continueResponse)
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
