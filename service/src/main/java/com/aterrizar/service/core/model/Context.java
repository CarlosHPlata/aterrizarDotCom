package com.aterrizar.service.core.model;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.aterrizar.service.core.model.request.CheckinRequest;
import com.aterrizar.service.core.model.request.CheckinResponse;
import com.aterrizar.service.core.model.session.ExperimentalData;
import com.aterrizar.service.core.model.session.Session;
import com.aterrizar.service.core.model.session.SessionData;
import com.aterrizar.service.core.model.session.Status;
import com.aterrizar.service.core.model.session.UserInformation;
import com.neovisionaries.i18n.CountryCode;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Represents the context of the check-in process, containing session, request, and response data.
 * Provides utility methods to modify and retrieve context-related information.
 *
 * <p><b>Design Patterns Used:</b>
 *
 * <p>- This class utilizes the Builder design pattern to construct immutable objects with optional
 * properties.
 *
 * <p>- It also employs the Fluent Interface pattern to provide a more readable and chainable API
 * for modifying the context.
 *
 * <p>- Additionally, this class serves as an adapter by providing a unified interface to interact
 * with various components like `Session`, `CheckinRequest`, and `CheckinResponse`. It simplifies
 * the interaction by exposing utility methods to modify and retrieve data in a consistent and
 * chainable way.
 */
@Getter
@SuperBuilder(toBuilder = true)
@lombok.experimental.Accessors(fluent = true)
public class Context {
    private final Session session;
    private final CheckinRequest checkinRequest;
    private final CheckinResponse checkinResponse;

    /**
     * Creates a new context with the specified session.
     *
     * @param session the session to set
     * @return a new context with the updated session
     */
    public Context withSession(Session session) {
        return this.toBuilder().session(session).build();
    }

    /**
     * Creates a new context with a customized session.
     *
     * @param customizer a consumer to customize the session
     * @return a new context with the updated session
     */
    public Context withSession(Consumer<Session.SessionBuilder> customizer) {
        var sessionBuilder = this.session != null ? this.session.toBuilder() : Session.builder();
        customizer.accept(sessionBuilder);
        return this.toBuilder().session(sessionBuilder.build()).build();
    }

    /**
     * Creates a new context with customized session data.
     *
     * @param customizer a consumer to customize the session data
     * @return a new context with the updated session data
     */
    public Context withSessionData(Consumer<SessionData.SessionDataBuilder> customizer) {
        var sessionDataBuilder =
                this.session != null && this.session.sessionData() != null
                        ? this.session.sessionData().toBuilder()
                        : SessionData.builder();
        customizer.accept(sessionDataBuilder);
        return this.withSession(
                sessionBuilder -> sessionBuilder.sessionData(sessionDataBuilder.build()));
    }

    /**
     * Creates a new context with a customized check-in request.
     *
     * @param customizer a consumer to customize the check-in request
     * @return a new context with the updated check-in request
     */
    public Context withCheckinRequest(Consumer<CheckinRequest.CheckinRequestBuilder> customizer) {
        var requestBuilder =
                this.checkinRequest != null
                        ? this.checkinRequest.toBuilder()
                        : CheckinRequest.builder();
        customizer.accept(requestBuilder);
        return this.toBuilder().checkinRequest(requestBuilder.build()).build();
    }

    /**
     * Creates a new context with a customized check-in response.
     *
     * @param customizer a consumer to customize the check-in response
     * @return a new context with the updated check-in response
     */
    public Context withCheckinResponse(
            Consumer<CheckinResponse.CheckinResponseBuilder> customizer) {
        var responseBuilder =
                this.checkinResponse != null
                        ? this.checkinResponse.toBuilder()
                        : CheckinResponse.builder();
        customizer.accept(responseBuilder);
        return this.toBuilder().checkinResponse(responseBuilder.build()).build();
    }

    /**
     * Creates a new context with the specified status.
     *
     * @param status the status to set
     * @return a new context with the updated status
     */
    public Context withStatus(Status status) {
        var session = this.session != null ? this.session : Session.builder().build();
        return this.toBuilder().session(session.withStatus(status)).build();
    }

    /**
     * Retrieves the current status of the session.
     *
     * @return the status of the session
     */
    public Status status() {
        return this.session.status();
    }

    /**
     * Creates a new context with a required field added to the check-in response.
     *
     * @param field the required field to add
     * @return a new context with the updated check-in response and status
     */
    public Context withRequiredField(RequiredField field) {
        CheckinResponse currentResponse =
                this.checkinResponse != null
                        ? this.checkinResponse
                        : CheckinResponse.builder().build();
        currentResponse.addProvidedField(field);

        return this.withStatus(Status.USER_INPUT_REQUIRED).toBuilder()
                .checkinResponse(currentResponse)
                .build();
    }

    /**
     * Retrieves the session ID from the context.
     *
     * @return the session ID
     */
    public UUID sessionId() {
        return this.session.sessionId();
    }

    /**
     * Retrieves the user ID from the context.
     *
     * @return the user ID
     * @throws IllegalStateException if user information is not available
     */
    public UUID userId() {
        var userInfo = Optional.of(this.session.userInformation());
        return userInfo.map(UserInformation::userId).orElseThrow(IllegalStateException::new);
    }

    public CountryCode countryCode() {
        var sessionData = Optional.ofNullable(this.session.sessionData());
        return sessionData.map(SessionData::countryCode).orElseThrow(IllegalStateException::new);
    }

    /**
     * Retrieves the user information from the context.
     *
     * @return the user information
     * @throws IllegalStateException if user information is not available
     */
    public UserInformation userInformation() {
        var userInfo = Optional.of(this.session.userInformation());
        return userInfo.orElseThrow(IllegalStateException::new);
    }

    /**
     * Creates a new context with customized user information.
     *
     * @param customizer a consumer to customize the user information
     * @return a new context with the updated user information
     */
    public Context withUserInformation(
            Consumer<UserInformation.UserInformationBuilder> customizer) {
        var userInfoBuilder =
                this.session.userInformation() != null
                        ? this.session.userInformation().toBuilder()
                        : UserInformation.builder();
        customizer.accept(userInfoBuilder);
        return this.withSession(
                sessionBuilder -> sessionBuilder.userInformation(userInfoBuilder.build()));
    }

    /**
     * Creates a new context with the specified experimental data.
     *
     * @param experimentalData the experimental data to set
     * @return a new context with the updated experimental data
     */
    public Context withExperimentalData(ExperimentalData experimentalData) {
        return this.withSession(
                sessionBuilder -> sessionBuilder.experimentalData(experimentalData));
    }

    /**
     * Retrieves the experimental data from the context, if available.
     *
     * @return an Optional containing the experimental data, or empty if not available
     */
    public Optional<ExperimentalData> experimentalData() {
        return Optional.ofNullable(this.session.experimentalData());
    }
}
