### Mexico (MX) Tax Compliance
**Description**: As a Mexican passenger, I need to comply with local tax regulations by providing my RFC and digitally signing the calculated tax amount during check-in. This is a legal requirement for Aterrizar.com to operate in the Mexican market and ensures transparency regarding the government fees associated with the flight.

**Context**: Our Mexican expansion requires a more rigorous check-in flow than the general one. We need to collect the RFC, calculate taxes via an external service, and ensure the user explicitly accepts the amount. This logic should be encapsulated in a specific strategy for Mexico.

**Technical details**:

* **Strategy**: Create a new `MxContinueFlow` and a new `MxCheckin` to Register a new strategy for `CountryCode.MX`.
* **Steps**:
  
  1. `RfcInputStep`: Use `Context.withRequiredField()` to ask for the RFC if not present.
  2. `TaxCalculationStep`: should be skipped if the tax amount is already present in the context. and executed after the `RfcInputStep` and if the RFC is present. It should retrieve information of the amount to pay from a new `TaxHttpClient` to fetch the tax amount.
     
     * **Mock Target**: `GET /tax-service/v1/calculate?rfc={rfc}`
     * **CURL Example**: `curl -X GET "http://localhost:3001/tax-service/v1/calculate?rfc=ES12345678Z"`
     * **CURL Example (Error)**: `curl -X GET "http://localhost:3001/tax-service/v1/calculate?rfc=ES123456781"` (Failure 406) (all RFCs ending with 1)
     * **Mock Behavior**: RFCs ending with `1` will return a `406 Not Acceptable` error; all other values return `{ "taxAmount": 45.00 }`.
     * You can take as example the `ExperimentalGatewayAdapter` as inspiration.
  3. `TaxAgreementStep`: Display the amount in the response and require a boolean field `digitalSign` to be `true`. It should be skipped if the field `digitalSign` is already present and true in the context.

**Acceptance criteria**:

-  [ ] Users from Mexico must be prompted for their RFC if missing.

* [ ]  The system must call the tax service and update the context with the result.

* [ ]  The flow must block until `digitalSign` is provided as `true`.

* [ ]  Unit tests for all 3 steps.

* [ ]  Integration test mimicking a full Mexican check-in flow.