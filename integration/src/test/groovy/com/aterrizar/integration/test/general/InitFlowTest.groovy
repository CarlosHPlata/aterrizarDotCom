package com.aterrizar.integration.test.general

import com.aterrizar.integration.framework.Checkin
import com.aterrizar.integration.framework.InitVerifier
import spock.lang.Specification

class InitFlowTest extends Specification {
    def "should init session successfully"() {
        setup:
        def checkin = Checkin.create()

        when:
        def result = checkin.initSession("MX")

        then:
        InitVerifier.verify(result)
    }
}
