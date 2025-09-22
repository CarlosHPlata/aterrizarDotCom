package com.aterrizar.integration.test.ve

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification

class VeContinueFlowTest extends Specification {
    def "should reject if userid does not match"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("VE")
        InitVerifier.verify(session)

        and:
        session.setUserId(UUID.randomUUID().toString())
        session.proceed()

        then:
        def e = thrown(HttpClientErrorException.BadRequest)
        ContinueVerifier.rejected(e, "User ID does not match session")
    }

    def "should be asked to provide passport number  and the fund amount when sending no information"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("VE")
        InitVerifier.verify(session)

        and: // continue without filling anything
        def continueResponse = session.proceed()

        and: // be asked to provide funds amount
        ContinueVerifier.requiredField(continueResponse, UserInput.FUNDS_AMOUNT_US)

        and: // fill funds amount
        continueResponse = session.fillUserInput([(UserInput.FUNDS_AMOUNT_US): "100.0"])

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
        def session = checkin.initSession("VE")
        InitVerifier.verify(session)

        and: // start the flow
        def continueResponse = session.proceed()

        and: // be asked to provide funds amount
        ContinueVerifier.requiredField(continueResponse, UserInput.FUNDS_AMOUNT_US)

        and: // fill funds amount
        continueResponse = session.fillUserInput([(UserInput.FUNDS_AMOUNT_US): "100.0"])

        and: // be asked to fill passport
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: // fill passport
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: // be asked to sign agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: // fill agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // be able to continue
        ContinueVerifier.completed(continueResponse)
    }

    def "should complete the entire VE flow"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("VE")
        InitVerifier.verify(session)

        and: // continue without filling anything
        def continueResponse = session.proceed()
        ContinueVerifier.requiredField(continueResponse, UserInput.FUNDS_AMOUNT_US)

        and: // fill funds amount
        continueResponse = session.fillUserInput([(UserInput.FUNDS_AMOUNT_US): "100.0"])

        and: // be asked to fill passport
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: // fill passport
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: // be asked to sign agreement
        ContinueVerifier.requiredField(continueResponse, UserInput.AGREEMENT_SIGNED)

        and: // fill agreement
        continueResponse = session.fillUserInput([(UserInput.AGREEMENT_SIGNED): "true"])

        then: // be completed
        ContinueVerifier.completed(continueResponse)
    }
}
