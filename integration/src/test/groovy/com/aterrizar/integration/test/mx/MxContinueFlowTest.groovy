package com.aterrizar.integration.test.mx

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification

class MxContinueFlowTest extends Specification {

    def "should complete the entire MX flow - happy path"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: "continue without filling anything"
        def continueResponse = session.proceed()

        and: "be asked to provide RFC"
        ContinueVerifier.requiredField(continueResponse, UserInput.RFC)

        and: "fill RFC (valid, does not end in 1)"
        continueResponse = session.fillUserInput([(UserInput.RFC): "XAXX010101000"])

        and: "be asked to sign digital tax agreement"
        ContinueVerifier.requiredField(continueResponse, UserInput.DIGITAL_SIGN)

        and: "fill digital sign"
        continueResponse = session.fillUserInput([(UserInput.DIGITAL_SIGN): "true"])

        and: "be asked to fill passport"
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: "fill passport"
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "can continue"
        ContinueVerifier.requiredField(continueResponse, UserInput.PAYMENT_METHOD)
    }

    def "should reject when RFC is invalid (ends in 1) - tax service returns 406"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: "continue without filling anything"
        def continueResponse = session.proceed()

        and: "be asked to provide RFC"
        ContinueVerifier.requiredField(continueResponse, UserInput.RFC)

        and: "fill RFC that ends in 1 (triggers 406 from tax service)"
        session.fillUserInput([(UserInput.RFC): "XAXX0101010001"])

        then: "be rejected with Invalid RFC"
        def e = thrown(HttpClientErrorException.BadRequest)
        ContinueVerifier.rejected(e, "Invalid RFC")
    }

    def "should skip RfcInputStep when RFC is already provided in session"() {
        setup:
        def checkin = Checkin.create()

        when:
        def session = checkin.initSession("MX")
        InitVerifier.verify(session)

        and: "provide RFC upfront without being asked"
        def continueResponse = session.fillUserInput([(UserInput.RFC): "XAXX010101000"])

        and: "be asked to sign digital tax agreement (RFC step was skipped)"
        ContinueVerifier.requiredField(continueResponse, UserInput.DIGITAL_SIGN)

        and: "fill digital sign"
        continueResponse = session.fillUserInput([(UserInput.DIGITAL_SIGN): "true"])

        and: "be asked to fill passport"
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        and: "fill passport"
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "can continue"
        ContinueVerifier.requiredField(continueResponse, UserInput.PAYMENT_METHOD)
    }
}
