package com.aterrizar.integration.test.feature

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.ContinueVerifier
import com.aterrizar.integration.framework.InitVerifier
import com.aterrizar.integration.model.UserInput
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification
import spock.lang.Unroll

class IdScanFlowTest extends Specification {

    @Unroll
    def "should request scan token and document ID for #country (provider: #provider)"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession(country, [flightCode])
        InitVerifier.verify(session)

        and: "Continue with passport - no scan data yet"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        then: "Should be asked for scan token and document ID"
        ContinueVerifier.requiredField(continueResponse, UserInput.SCAN_TOKEN)
        ContinueVerifier.requiredField(continueResponse, UserInput.DOCUMENT_ID)

        where:
        country | flightCode      | provider
        "US"    | "USJFKGBLHR"   | "Onfido"
        "MX"    | "MXMIDGBLHR"   | "Jumio"
    }

    def "should complete flow when validation returns SUCCESS"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession("MX", ["MXMIDGBLHR"])
        InitVerifier.verify(session)

        and: "Fill passport"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.SCAN_TOKEN)

        and: "Provide scan token ending in 1 (SUCCESS) and valid Jumio document ID"
        continueResponse = session.fillUserInput([
                (UserInput.SCAN_TOKEN) : "JU-TOKEN001",
                (UserInput.DOCUMENT_ID): "JU-DOC123456"
        ])

        then: "Should complete"
        ContinueVerifier.completed(continueResponse)
    }

    def "should increment retry counter and return 200 when validation returns PENDING"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession("MX", ["MXMIDGBLHR"])
        InitVerifier.verify(session)

        and: "Fill passport"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.SCAN_TOKEN)

        and: "Provide scan token ending in 0 (PENDING)"
        continueResponse = session.fillUserInput([
                (UserInput.SCAN_TOKEN) : "JU-TOKEN000",
                (UserInput.DOCUMENT_ID): "JU-DOC123456"
        ])

        then: "Should return user input required again (retry)"
        continueResponse.status == "user_input_required"
    }

    def "should complete after pending then success"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession("MX", ["MXMIDGBLHR"])
        InitVerifier.verify(session)

        and: "Fill passport"
        session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: "First attempt - PENDING (token ends in 0)"
        session.fillUserInput([
                (UserInput.SCAN_TOKEN) : "JU-TOKEN000",
                (UserInput.DOCUMENT_ID): "JU-DOC123456"
        ])

        and: "Second attempt - SUCCESS (token ends in 1)"
        def continueResponse = session.fillUserInput([
                (UserInput.SCAN_TOKEN) : "JU-TOKEN001",
                (UserInput.DOCUMENT_ID): "JU-DOC123456"
        ])

        then: "Should complete"
        ContinueVerifier.completed(continueResponse)
    }

    def "should reject with 406 when validation returns REJECTED"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session"
        def session = checkin.initSession("MX", ["MXMIDGBLHR"])
        InitVerifier.verify(session)

        and: "Fill passport"
        session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])

        and: "Provide scan token ending in 2 (REJECTED)"
        def exception = null
        try {
            session.fillUserInput([
                    (UserInput.SCAN_TOKEN) : "JU-TOKEN002",
                    (UserInput.DOCUMENT_ID): "JU-DOC123456"
            ])
        } catch (HttpClientErrorException e) {
            exception = e
        }

        then: "Should return 406"
        exception != null
        exception.statusCode.value() == 406
    }

    def "should use Onfido provider for US and require ON- prefixed document ID"() {
        setup:
        def checkin = Checkin.create()

        when: "Initialize session for US passenger"
        def session = checkin.initSession("US", ["USJFKGBLHR"])
        InitVerifier.verify(session)

        and: "Fill passport"
        def continueResponse = session.fillUserInput([(UserInput.PASSPORT_NUMBER): "A12345678"])
        ContinueVerifier.requiredField(continueResponse, UserInput.SCAN_TOKEN)

        and: "Provide valid Onfido token and document"
        continueResponse = session.fillUserInput([
                (UserInput.SCAN_TOKEN) : "ON-TOKEN001",
                (UserInput.DOCUMENT_ID): "ON-DOC123456"
        ])

        then: "Should complete"
        ContinueVerifier.completed(continueResponse)
    }
}