# Check-in System: Professional User Stories (Standard Convention)

---

### 1. Mexico (MX) Tax Compliance

**Description**:
As a Mexican passenger, I need to comply with local tax regulations by providing my RFC and digitally signing the calculated tax amount during check-in. This is a legal requirement for Aterrizar.com to operate in the Mexican market and ensures transparency regarding the government fees associated with the flight.

**Context**:
Our Mexican expansion requires a more rigorous check-in flow than the general one. We need to collect the RFC, calculate taxes via an external service, and ensure the user explicitly accepts the amount. This logic should be encapsulated in a specific strategy for Mexico.

**Technical details**:
- **Strategy**: Create a new `MxContinueFlow` and a new `MxCheckin` to Register a new strategy for `CountryCode.MX`.

- **Steps**:
  1. `RfcInputStep`: Use `Context.withRequiredField()` to ask for the RFC if not present.
  2. `TaxCalculationStep`: should be skipped if the tax amount is already present in the context. and executed after the `RfcInputStep` and if the RFC is present. It should retrieve information of the amount to pay from a new `TaxHttpClient` to fetch the tax amount. 
     - **Mock Target**: `GET /tax-service/v1/calculate?rfc={rfc}`
     - **CURL Example**: `curl -X GET "http://localhost:3001/tax-service/v1/calculate?rfc=ES12345678Z"`
     - **CURL Example (Error)**: `curl -X GET "http://localhost:3001/tax-service/v1/calculate?rfc=ES123456781"` (Failure 406) (all RFCs ending with 1)
     - **Mock Behavior**: RFCs ending with `1` will return a `406 Not Acceptable` error; all other values return `{ "taxAmount": 45.00 }`.
     - You can take as example the `ExperimentalGatewayAdapter` as inspiration.
  3. `TaxAgreementStep`: Display the amount in the response and require a boolean field `digitalSign` to be `true`. It should be skipped if the field `digitalSign` is already present and true in the context.

**Acceptance criteria**:
- [ ] Users from Mexico must be prompted for their RFC if missing.
- [ ] The system must call the tax service and update the context with the result.
- [ ] The flow must block until `digitalSign` is provided as `true`.
- [ ] Unit tests for all 3 steps.
- [ ] Integration test mimicking a full Mexican check-in flow.

---

### 2. 🤳 Biometric "FaceID" Auth & Skip Logic

**Description**:
As a Product Owner, I want to introduce a "Fast Track" biometric check-in pilot. For passengers who opt-in, we can use facial recognition to verify their identity instantly, allowing them to skip the manual entry of passport details and the signing of legal agreements, drastically reducing the check-in time from 2 minutes to 15 seconds.

**Context**:
This is an experimental feature managed via a feature flag. The process has two phases: getting a session token for the biometric kiosk/hardware and receiving the pass/fail signal. Once authenticated, we must use the `Context` to mark the user as verified.

**Technical details**:
- **Step**: Create a new `BiometricEnrollmentStep` (you can also think about two steps instead of one if you find it easier) that will execute the two phases and update the `Context` with the result.
- **API Phases**:
  - `GET /biometric-service/v1/start`: Should be implemented via `BiometricHttpClient` to return an `authSessionToken` and a required field asking for the user result of the biometric check defined by the same session token but ending with `_verified`.
  - `POST /biometric-service/v1/verify`: Receives the token signed and return a `verified` boolean field if the token is valid and the user biometric check is successful.
- **Mock Behavior**: `GET /biometric-service/v1/start` always succeeds. `POST /biometric-service/v1/verify` yields `200 OK` (success) unless the `authSessionToken` ends in `1`, which triggers a `406 Not Acceptable` (failure).
- **Skip Logic**: In `PassportInformationStep` and `AgreementSignStep` add at the when method a conditional check that looks for `context.session().isBiometricAuthenticated()`. If the condition is true, skip the step.
- **Framework**: Use `andExperimental(new BiometricEnrollmentStep(), ExperimentalStepKey.BIOMETRIC_CHECK)` in the `FlowExecutor`.

**Acceptance criteria**:
- [ ] Feature must be toggleable via `ExperimentalStepKey`.
- [ ] Successful biometric auth must automatically set the session status to allow skipping steps.
- [ ] `PassportInformationStep` and `AgreementSignStep` must not execute if the user is biometric-verified.
- [ ] Unit tests for the skip logic in `FlowExecutor`.
- [ ] Integration test for the experimental path.

---

### 3. 🛂 Home Office ETA Validation (UK, CH, SV)

**Description**:
As a Product Owner, I want to ensure we are compliant with immigration laws for the UK, Switzerland, and Sweden. Any passenger traveling to these countries must have a valid Electronic Travel Authorisation (ETA). Failure to validate this could result in heavy fines for the airline.

**Context**:
We need a unified way to validate ETAs against the Home Office. This required a properties feature, configuration-based for the countries that require an ETA in the form of `feature.homeoffice.eta.enabled-countries=UK,CH,SE`.
The step will evaluate eta, if the eta is rejected the flow will be rejected, if the eta is pending the flow will continue but mark the session as manual review required at airport in a field called `etaManualReviewRequired`, if the eta is accepted the flow will be accepted.


**Technical details**:
- **Client**: `HomeOfficeHttpClient` in `com.aterrizar.http.external.gateway.homeoffice`.
- **Step**: `EtaValidationStep`. Use `when(context)` to check if `context.countryCode()` is in the list of enabled countries based upon the property `feature.homeoffice.eta.enabled-countries` check for example how digital visa is implemented.
- **Model**: Pass `passportNumber` and `destinationCode` to the API.
- **WireMock**:
  ```bash
  curl -X POST http://localhost:3001/home-office/v1/eta-validation \
    -d '{"passportNumber": "...", "destination": "UK"}'
  ```
- **Mock Behavior**:
  - `passportNumber` ending in `1` returns a `406 Not Acceptable` (`status: Rejected`).
  - `passportNumber` ending in `2` returns a `200 OK` (`status: Pending`).
  - All other values return `200 OK` (`status: Accepted`).
- **Configuration**: Use `@Value` or a configuration bean to define the list of ETA-mandatory countries.

**Acceptance criteria**:
- [ ] Validation only triggers for UK, CH, and SV.
- [ ] The step must fail the flow (Terminal Result) if the API returns "Rejected".
- [ ] The step must mark the session as "Pending" if the API returns such status.
- [ ] Integration test verifying the different API response statuses.

---

### 4. 💰 The "Grand Payment" Orchestration (Baggage Fees)

**Description**:
As a Product Owner, I want to monetize excess baggage by allowing passengers to pay their fees directly within the check-in flow. We need to support local payment preferences (3DS, Wire Transfer, and Government-vouched CURP) to maximize conversion in different markets.

**Context**:
This is a complex feature where available methods depend on the passenger's country. We need an adapter to normalize the different payment types and a feature-flag system based on country codes.

**Technical details**:
- **Feature Flags**: Define in `application.properties`:
  - `feature.tax.payments.US=3DS,WIRE,GOV`
  - `feature.tax.payments.MX=3DS,WIRE`
  - `feature.tax.payments.CA=3DS,GOV`
- **Adapter**: Create `PaymentAdapter` to handle `3dsRequest`, `WireRequest`, and `GovRequest`.
- **Required Fields**:
  - `3DS`: `cardNumber`.
  - `WIRE`: `linkIdentifier`.
  - `GOV`: `curpNumber`.
- **Steps**:
  1. `PaymentMethodStep`: Check flag, Request `paymentMethod` field which can be `3DS`, `WIRE`, or `GOV`.
  2. `PaymentValidationStep`: Check flag, call `PaymentFacade` to get a `paymentToken` from the correct payment adapter by a factory pattern based upon the payment method.
     - **Mock Target**: `POST /payment-service/v1/3ds/token`, `POST /payment-service/v1/wire/token`, `POST /payment-service/v1/gov/token`.
     - **Mock Behavior**:
       - `POST /payment-service/v1/3ds/token` returns `paymentToken` starting with `3DS-`.
       - `POST /payment-service/v1/wire/token` returns `paymentToken` starting with `WIRE-`.
       - `POST /payment-service/v1/gov/token` returns `paymentToken` starting with `GOV-`.
  3. `TransactionFinalizationStep`: Poll/Check status of the `paymentToken`.
     - **Mechanism**: Use the `paymentToken` stored in the `Session` to query an external `PaymentStatusClient`.
     - **Mock Target**: `GET /payment-service/v1/status/{token}`.
     - **Mock Behavior**:
       - `GET /payment-service/v1/status/{token}` always returns `{"status": "SUCCESS"}`.

**Acceptance criteria**:
- [ ] Users in MX cannot see/use GOV payment method.
- [ ] Flow returns `USER_INPUT_REQUIRED` with the specific field required for the chosen method (e.g., `curpNumber` for GOV).
- [ ] Check-in is blocked until `tokenStatus` is `SUCCESS`.
- [ ] Comprehensive unit tests for the `PaymentAdapter`.

---

### 5. 🧩 The "Macro-Step" Pattern (Composite Steps)

**Description**:
As a Technical Lead, I want to enhance the maintainability and readability of our business flows by introducing a "Macro-Step" pattern. Our flows are currently growing into long, flat lists of steps that can be difficult to manage. By grouping related operations—such as initial session retrieval and validation—into a single semantic unit like `InitSessionCompositeStep`, we can keep our high-level flow logic concise and easy for anyone to understand at a glance.

**Context**:
We need to implement the Composite Pattern within our `Step` framework. This allows a single `Step` to internally run its own sub-chain of logic. For this task, you will refactor the framework to support this pattern and specifically compound `GetSessionStep` and `ValidateSessionStep` into this new structure.

**Technical details**:
- **Framework Enhancement (Template Method Pattern)**:
  - Create an abstract class `CompositeStep` that implements the `Step` interface.
  - Define an abstract method (e.g., `registerSteps(FlowExecutor executor)`) that subclasses must implement to populate their internal chain.
  - The `onExecute` method in the base `CompositeStep` should initialize a local `FlowExecutor`, invoke the template method, and execute the resulting chain.
- **Concrete Implementation**:
  - Create `InitSessionCompositeStep` extending `CompositeStep`.
  - Implement the registration logic to include `GetSessionStep` and `ValidateSessionStep`.
- **Usage**:
  - Update existing flow strategies to use `baseExecutor.and(new InitSessionCompositeStep())` in place of the individual session management steps.

**Acceptance criteria**:
- [ ] `FlowExecutor` in the main flow remains clean (one line per major business process).
- [ ] Context and results are passed correctly through the sub-step chain.
- [ ] Any terminal rejection in a sub-step correctly terminates the entire composite and the main flow.
- [ ] Unit tests for the `CompositeStep` base class and the `InitSessionCompositeStep` concrete class.

---

### 6. ⚓ Observer-Based Step Interceptors

**Description**:
As a System Architect, I want to gain deep visibility into how our flows perform. We need to track exactly how long each step takes and whether it was execution-skipped or if it terminated the flow. This data is critical for identifying bottlenecks and debugging high-priority issues.

**Context**:
We should implement the Observer Pattern to allow "Interceptors" to subscribe to the flow lifecycle via the `Step` interface and `FlowExecutor`. This allows us to add cross-cutting concerns like logging or profiling without touching the business steps.

**Technical details**:
- **Interface**: Create a new interface `StepInterceptor` with `before(Context)` and `after(Context, Result)`.
- **Refactoring**: 
 - Update `FlowExecutor` following **observer pattern** to have a constructor that accepts a list of `StepInterceptor` (that will serve as observers) then update the method `execute` to notify the observers before and after the execution of each step.
 - Update `CheckinStrategyFactory` to dynamically get the list of interceptors by using spring boot bean factory just as how it does with the checkin strategies, and then modify the `create` method to pass the list of interceptors to the strategy and finally to the `FlowExecutor`.
- **Implementation**: Create `TimingInterceptor` that logs:
  - `className`: The step name.
  - `whenPassed`: If the validation check passed.
  - `isTerminal`: If it stopped the flow.
  - you can format it like this: `Step: {className} - executed: {whenPassed} - terminal: {isTerminal}`.
- **Pattern**: Observer/Interception.

**Acceptance criteria**:
- [ ] Every step execution must trigger the interceptors.
- [ ] `TimingInterceptor` must output clean logs for every step in a flow.
- [ ] No performance degradation (interceptor overhead < 1ms).
- [ ] Unit test showing the interceptor captures the correct data.

---

### 7. 🛡️ Global ID-Scan (Onfido vs Jumio)

**Description**:
As a Security Officer, I want to implement a robust and cost-effective identity verification system. We need to leverage specialized providers: Onfido for high-security or strictly regulated regions and Jumio for standard regions. This strategy ensures we maintain the highest security standards where necessary while optimizing operational costs and providing a localized experience for our global passenger base.

**Context**:
The ID scanning process is orchestrated by the frontend, which interacts directly with the provider SDKs. Our backend acts as the secure validator and state manager. We need a dynamic factory to route validation requests to the correct provider based on the country. Since the verification can be slow, the backend will receive a token and a document ID to check status. We require a retry policy (up to 3 attempts) persisted in the session (Redis) to handle "Pending" responses gracefully.

**Technical details**:
- **Provider Settings & Rules**:
  - Implement a configuration property `feature.onfido.enabled.countries` to identify regions requiring Onfido (US).
  - **Onfido** (High-Security): Document IDs must start with `ON-`.
  - **Jumio** (Standard): All other regions. Document IDs must start with `JU-`.
  - Document IDs must be validated as exactly 12 characters.
- **Orchestration Flow**:
  1. `IdScanValidationStep`: 
     - **Execution Condition**: Evaluated when no valid document is in session, no scan token is present, and no retries are active.
     - **Logic**: Use `IdScanProviderFactory` to generate the correct provider token based on country.
     - **Response**: Return the token and required fields to the frontend asking for the scan token and the document ID.
  2. `IdScanStep`:
     - **Execution Condition**: Valid scan token exists in session and retry counter < 3.
     - **Logic**:
       - Reject with `400 Bad Request` if token or document ID is missing in the request.
       - Invoke `DocumentValidator` to verify the provider-specific prefix and status.
       - **Status Handling**:
         - `SUCCESS`: Finalize verification, clear retries, and proceed.
         - `PENDING`: Increment the retry counter in the session (Redis) and return `200 OK`.
         - `REJECTED`: Terminate with `406 Not Acceptable`.
- **Mock Behavior**:
  - `GET /scanner-service/v1/onfido/token` returns `token` starting with `ON-`.
  - `GET /scanner-service/v1/jumio/token` returns `token` starting with `JU-`.
  - `POST /scanner-service/v1/validate`:
    - `token` ending in `0` returns `200 OK` with `status: PENDING`.
    - `token` ending in `2` returns `406 Not Acceptable` with `status: REJECTED`.
    - `token` ending in `1` (or others) returns `200 OK` with `status: SUCCESS`.
- **Patterns**:
  - **Factory Pattern**: `IdScanProviderFactory` determines the client client based on the token generated.
- **Integration**: Target classes in `com.aterrizar.http.external.gateway.scanner`.

**Acceptance criteria**:
- [ ] High-security countries are correctly routed to Onfido based on property configuration.
- [ ] System rejects verification if the document ID prefix does not match the assigned provider.
- [ ] Implements a 3-attempt retry policy for "Pending" statuses, persisted in Redis.
- [ ] Returns `400 Bad Request` if mandatory scan data is missing.
- [ ] Unit tests for `IdScanProviderFactory` and the state transitions in `IdScanStep`.
- [ ] Integration tests covering Token Generation -> Pending (Retry loop) -> Success/Failure.

---

### 8. ⚡ High-Performance Flight Cache (Redis Proxy)

**Description**:
As an Operations Lead, I've noticed that the `Aviator` flight system can be sluggish during peak hours, causing check-in timeouts. We need a way to cache flight data locally so that we don't have to hit the slow external API every time a passenger starts their check-in for the same flight.

**Context**:
We have a Redis instance available. We should implement a Proxy for the `AviatorHttpClient` that transparently handles caching of the `FlightDto`.

**Technical details**:
- **Pattern**: Proxy Pattern.
- **Implementation**: `CachingAviatorClientProxy` implements `AviatorHttpClient`.
- **Logic**:
  - Key: `flight:{flightNumber}`.
  - Check Redis -> Hit: Return object. Miss: Call real client -> Store in Redis (TTL 10m).
- **Dependency**: Use `StringRedisTemplate` or a similar Redis client.

**Acceptance criteria**:
- [ ] First call for a flight takes > 500ms (to simulate API latency).
- [ ] Subsequent calls for the same flight take < 20ms.
- [ ] Objects are correctly serialized/deserialized from Redis.
- [ ] Unit test for the Proxy logic with a mocked Real Client and Redis.

---

### 9. 📝 Automated Audit Logging (History Step)

**Description**:
As a Compliance Manager, I am required to maintain a full history of all session modifications for regulatory audits. I need to know exactly which fields were changed during each step of the check-in process to reconstruct the user's journey if a dispute arises.

**Context**:
We can leverage the existing **Step Interceptors** (Story 6) to solve this. An interceptor can diff the `Context` before and after each step and send the changes to an audit service.

**Technical details**:
- **Interceptor**: `AuditLogInterceptor`.
- **Logic**:
  - Capture `Context` state in `before()`.
  - Compare with `Context` state in `after()`.
  - Send JSON diff to `AuditHttpClient`.
- **Mock Target**: `POST /audit-service/v1/logs`
- **Pattern**: Observer.

**Acceptance criteria**:
- [ ] Every change in the session status or data must be logged.
- [ ] The log must include the `sessionId`, `stepName`, and the `diffData`.
- [ ] If no changes occurred in a step, no log should be sent.
- [ ] Integration test verifying logs are sent when the flow progresses.

---

### 10. 🔄 Session Snapshot & Rollback

**Description**:
As a Product Owner, I want a "Self-Healing" checkout process. If a passenger encounters a technical error or provides invalid data during a complex phase (like Payment), I don't want them to lose their entire progress. The system should be able to "undo" and return them to the last known stable state.

**Context**:
This requires implementing a state-point mechanism (Memento Pattern). We need to explicitly mark "Stable Points" where a snapshot of the `Context` is taken.

**Technical details**:
- **Pattern**: Memento Pattern.
- **Step**: `SnapshotStep`. When executed, it saves a copy of the current `Context` into the `ExperimentalData` of the `Session`.
- **Flow Logic**:
  - Add `SnapshotStep` before the `Payment` phase.
  - Update `FlowExecutor` to handle errors: if a step fails, check if a snapshot exists in the context and "revert" the session to that state instead of erroring out.
- **Storage**: The snapshot should be serialized into a `String` or `Map` inside the session database.

**Acceptance criteria**:
- [ ] A failure in `PaymentValidationStep` must revert the session to the state captured by `SnapshotStep`.
- [ ] The user session status must remain active (e.g., `USER_INPUT_REQUIRED`) instead of `REJECTED` if a rollback is possible.
- [ ] Unit tests for serialize/deserialize of the Context memento.
- [ ] Integration test for the failure/rollback scenario.
