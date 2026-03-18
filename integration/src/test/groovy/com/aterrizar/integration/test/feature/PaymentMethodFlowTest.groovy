package com.aterrizar.integration.test.feature

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import org.springframework.web.client.HttpClientErrorException.BadRequest
import spock.lang.Specification
import spock.lang.Unroll

class PaymentMethodFlowTest extends Specification {

    @Unroll
    def "should request #requiredField when #method is selected for #country"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession(country)
        InitVerifier.verify(session)

        and: "Send payment method ALONG WITH passport to reach the payment step"
        def response = session.fillUserInput([
                (UserInput.PASSPORT_NUMBER): "A12345678",
                (UserInput.PAYMENT_METHOD): method
        ])

        then: "Verify that the specific required field is requested"
        ContinueVerifier.requiredField(response, requiredField)

        where:
        country | method | requiredField
        "US"    | "3DS"  | UserInput.CARD_NUMBER
        "US"    | "WIRE" | UserInput.LINK_IDENTIFIER
        "US"    | "GOV"  | UserInput.CURP_NUMBER
        "CA"    | "GOV"  | UserInput.CURP_NUMBER
    }

    @Unroll
    def "should request #requiredField when #method is selected after filling fields #country"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession(country)
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

        and: "Send payment method ALONG WITH passport to reach the payment step"
        def response = session.fillUserInput([
                (UserInput.PASSPORT_NUMBER): "A12345678",
                (UserInput.PAYMENT_METHOD): method
        ])

        then: "Verify that the specific required field is requested"
        ContinueVerifier.requiredField(response, requiredField)

        where:
        country | method | requiredField
        "MX"    | "3DS"  | UserInput.CARD_NUMBER
    }

    @Unroll
    def "should reject #method for #country"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session and try a forbidden method"
        def session = checkin.initSession(country)
        InitVerifier.verify(session)

        session.fillUserInput([
                (UserInput.PASSPORT_NUMBER): "A12345678",
                (UserInput.PAYMENT_METHOD): method
        ])

        then: "The server returns a 400 BadRequest, we catch it and verify the message"
        BadRequest e = thrown(BadRequest)
        ContinueVerifier.rejected(e, errorMessage)

        where:
        country | method | errorMessage
        "CA"    | "WIRE" | "Payment method WIRE is not allowed for country CA"
    }

    @Unroll
    def "should reject #method for #country"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session and try a forbidden method"
        def session = checkin.initSession(country)
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

        session.fillUserInput([
                (UserInput.PASSPORT_NUMBER): "A12345678",
                (UserInput.PAYMENT_METHOD): method
        ])

        then: "The server returns a 400 BadRequest, we catch it and verify the message"
        BadRequest e = thrown(BadRequest)
        ContinueVerifier.rejected(e, errorMessage)

        where:
        country | method | errorMessage
        "MX"    | "GOV"  | "Payment method GOV is not allowed for country MX"
    }

    @Unroll
    def "should complete flow for #method"() {
        setup:
        def checkin = Checkin.create()

        when: "Complete flow with all required data"
        def session = checkin.initSession(country)
        def response = session.fillUserInput([
                (UserInput.PASSPORT_NUMBER): "A12345678",
                (UserInput.PAYMENT_METHOD): method,
                (requiredInput): "TEST-VALUE-123"
        ])

        then: "The flow should finish as COMPLETED"
        ContinueVerifier.completed(response)

        where:
        country | method | requiredInput
        "CA"    | "GOV"  | UserInput.CURP_NUMBER
        "US"    | "3DS"  | UserInput.CARD_NUMBER
    }

    @Unroll
    def "should complete flow for #method"() {
        setup:
        def checkin = Checkin.create()

        when: "Init session"
        def session = checkin.initSession(country)

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

        and: "Send payment method information"
        def response = session.fillUserInput([
                (UserInput.PASSPORT_NUMBER): "A12345678",
                (UserInput.PAYMENT_METHOD): method,
                (requiredInput): "TEST-VALUE-123"
        ])

        then: "The flow should finish as COMPLETED"
        ContinueVerifier.completed(response)

        where:
        country | method | requiredInput
        "MX"    | "WIRE" | UserInput.LINK_IDENTIFIER
    }
}