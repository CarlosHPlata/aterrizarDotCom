package mocks;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.request.CheckinRequest;
import com.aterrizar.service.core.model.request.CheckinResponse;
import com.aterrizar.service.core.model.session.*;
import com.neovisionaries.i18n.CountryCode;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MockContext extends Context {
    protected MockContext(ContextBuilder<?, ?> b) {
        super(b);
    }

    public static Context initializedMock(CountryCode countryCode) {
        return initializedMock(countryCode, builder -> {});
    }

    public static Context initializedMock(CountryCode countryCode, CheckinRequest checkinRequest) {
        return initializedMock(countryCode, builder -> builder.checkinRequest(checkinRequest));
    }

    public static Context initializedMock(CountryCode countryCode, Consumer<ContextBuilder<?, ?>> customizer) {
        var sessionId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var session = MockContext.mockSessionInformation(sessionId, userId, countryCode);
        var request = CheckinRequest.builder()
                .sessionId(sessionId)
                .userId(userId)
                .build();

        var builder = MockContext.builder()
                .session(session)
                .checkinRequest(request)
                .checkinResponse(CheckinResponse.builder().build());
        customizer.accept(builder);
        return builder.build();
    }

    private static Session mockSessionInformation(UUID sessionId, UUID userId, CountryCode countryCode) {
        var userInformation = UserInformation.builder()
                .userId(userId)
                .build();

        var flightData = FlightData.builder()
                .flightNumber("AR1234")
                .price(150L)
                .departure(Airport.builder()
                        .airportCode("JFK")
                        .countryCode(CountryCode.US)
                        .build())
                .destination(Airport.builder()
                        .airportCode("EZE")
                        .countryCode(CountryCode.AR)
                        .build())
                .build();

        var sessionData = SessionData.builder()
                .countryCode(countryCode)
                .passengers(1)
                .flights(List.of(flightData))
                .build();

        return Session.builder()
                .sessionId(sessionId)
                .userInformation(userInformation)
                .sessionData(sessionData)
                .build();
    }
}
