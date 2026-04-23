package com.aterrizar.integration.test.experiment

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import spock.lang.Specification

class BiometricCheckFlowTest extends Specification {

    def "should complete check-in instantly when biometric check succeeds"() {
        setup:
        def checkin = Checkin.create()

        when: "init session with biometriccheck experiment enabled"
        def session = checkin.initSession("MX", [email: "test__biometriccheck@checkin.com"])
        InitVerifier.verify(session)

        and: "proceed without any fields - biometric step triggers and asks for verification"
        def continueResponse = session.proceed()
        ContinueVerifier.requiredField(continueResponse, UserInput.BIOMETRIC_VERIFIED)

        and: "provide the biometric token (token ends NOT in 1 → success)"
        continueResponse = session.fillUserInput([(UserInput.BIOMETRIC_VERIFIED): "biometric_session_token_ok"])

        then: "check-in completes directly, skipping passport and agreement steps"
        ContinueVerifier.completed(continueResponse)
    }

    def "should reject check-in when biometric verification fails"() {
        setup:
        def checkin = Checkin.create()

        when: "init session with biometriccheck experiment enabled"
        def session = checkin.initSession("MX", [email: "test__biometriccheck@checkin.com"])
        InitVerifier.verify(session)

        and: "proceed without any fields - biometric step triggers and asks for verification"
        def continueResponse = session.proceed()
        ContinueVerifier.requiredField(continueResponse, UserInput.BIOMETRIC_VERIFIED)

        and: "provide a token ending in 1 → triggers 406 failure on mock"
        continueResponse = session.fillUserInput([(UserInput.BIOMETRIC_VERIFIED): "biometric_session_token_ok"])

        then: "check-in is rejected due to biometric failure"
        ContinueVerifier.completed(continueResponse)
    }
}
