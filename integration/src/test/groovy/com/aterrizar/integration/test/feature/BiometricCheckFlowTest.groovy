package com.aterrizar.integration.test.feature

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import spock.lang.Specification


//Integration tests for the BiometricCheckFlow feature.
 
class BiometricCheckFlowTest extends Specification {

    def "should complete flow with successful biometric authentication"() {
        setup:
        def checkin = Checkin.create()

        when: "init session with biometric experiment email"
        def session = checkin.initSession("US", [email: "test__biometriccheck@checkin.com"])
        InitVerifier.verify(session)

        and: "proceed without fields - biometric enrollment starts"
        def continueResponse = session.proceed()

        then: "should request biometric verification token"
        ContinueVerifier.requiredField(continueResponse, UserInput.BIOMETRIC_VERIFIED)

        when: "submit valid biometric token"
        continueResponse = session.fillUserInput([(UserInput.BIOMETRIC_VERIFIED): "validToken"])

        then: "should skip passport and agreement, completing check-in"
        ContinueVerifier.completed(continueResponse)
    }

    
     //Test: Failed biometric authentication
     
    def "should continue with normal flow when biometric authentication fails"() {
        setup:
        def checkin = Checkin.create()

        when: "init session with biometric experiment email"
        def session = checkin.initSession("MX", [email: "test__biometriccheck@checkin.com"])
        InitVerifier.verify(session)

        and: "proceed - biometric enrollment starts"
        def continueResponse = session.proceed()

        then: "should request biometric verification token"
        ContinueVerifier.requiredField(continueResponse, UserInput.BIOMETRIC_VERIFIED)

        when: "submit invalid biometric token (fails)"
        continueResponse = session.fillUserInput([(UserInput.BIOMETRIC_VERIFIED): "invalidToken1"])

        then: "should continue with normal flow and request passport"
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER) 

        when: "submit passport"
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "should complete check-in"
        ContinueVerifier.completed(continueResponse)
    }

    
     // Test: Biometric feature flag disabled (email isn't the experimental flag)
     
    def "should use normal flow when biometric experiment is not enabled"() {
        setup:
        def checkin = Checkin.create()

        when: "init session with regular email (not experimental)"
        def session = checkin.initSession("MX", [email: "regular.user@example.com"])
        InitVerifier.verify(session)

        and: "proceed"
        def continueResponse = session.proceed()

        then: "should request passport directly (no biometric)"
        ContinueVerifier.requiredField(continueResponse, UserInput.PASSPORT_NUMBER)

        when: "submit passport"
        continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "should complete check-in directly (no agreement without experiment)"  
        ContinueVerifier.completed(continueResponse)                                  
    }
}
