# Aterrizar Punto Com — Check-in API Reference

Generated from `http/src/main/resources/openapi/openapi.yaml` (OpenAPI 3.0.4, version `1.0.1`).

## Service Overview

Aterrizar Punto Com is a travel-management platform. This microservice handles the **check-in process**: it initializes a session for a passenger group and then iteratively asks the client to provide the fields required for the passengers' country / flight combination, until the check-in is `completed` or `rejected`.

The interaction is a **two-call loop**:

```
POST /v1/checkin/init       → returns { sessionId, status: "initialized" }
POST /v1/checkin/continue   → returns { status, inputRequiredFields?, errorMessage? }
                              (call repeatedly with the requested fields
                               until status is "completed" or "rejected")
```

## Connection Details

| Setting           | Value                                                    |
|-------------------|----------------------------------------------------------|
| Base URL (local)  | `http://localhost:8080/aterrizar`                        |
| Context path      | `/aterrizar`                                             |
| Swagger UI        | `http://localhost:8080/aterrizar/swagger-ui.html`        |
| OpenAPI JSON      | `http://localhost:8080/aterrizar/openapi`                |
| Content-Type      | `application/json`                                       |
| Auth              | None (this microservice does not require authentication) |

## Endpoints

### `POST /v1/checkin/init` — Initialize check-in

Creates a check-in session for a group of passengers traveling on one or more flight legs.

**Request body** — `InitRequestData`

| Field           | Type                | Required | Notes                                                       |
|-----------------|---------------------|----------|-------------------------------------------------------------|
| `country`       | `CountryCode`       | yes      | ISO 3166-1 alpha-2 (e.g. `US`, `MX`, `AR`).                 |
| `userId`        | `string` (uuid)     | yes      | Identifier of the user starting the check-in.               |
| `passengers`    | `integer`           | yes      | Number of passengers in the booking.                        |
| `email`         | `string` (email)    | yes      | Contact email for the check-in.                             |
| `flightNumbers` | `string[]` (≥1)     | yes      | Encoded flight legs (see "Flight number convention" below). |

**Sample request**

```json
{
  "country": "US",
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "passengers": 2,
  "email": "example@example.com",
  "flightNumbers": ["USJFKGBLHF", "GBLHRMXMID"]
}
```

**`200 OK` response** — `InitResponseData`

| Field       | Type            | Notes                                                     |
|-------------|-----------------|-----------------------------------------------------------|
| `status`    | `StatusCode`    | Always `initialized` on a successful init.                |
| `sessionId` | `string` (uuid) | Pass this as `sessionId` to every subsequent `/continue`. |

**Sample response**

```json
{
  "status": "initialized",
  "sessionId": "9c0d4d84-91a7-4e9c-b0a1-9a2db4d6f5d9"
}
```

---

### `POST /v1/checkin/continue` — Continue check-in

Advances the check-in flow. The server runs the next step in the country-specific chain and either:

- asks for additional fields (`status = user_input_required`),
- completes the check-in (`status = completed`),
- or rejects it (`status = rejected`).

The client should call this endpoint **in a loop**: read `inputRequiredFields` from the response, prompt the user for those values, then submit them back as `providedFields`.

**Request body** — `CheckinRequestData`

| Field            | Type                       | Required | Notes                                                          |
|------------------|----------------------------|----------|----------------------------------------------------------------|
| `sessionId`      | `string` (uuid)            | yes      | Returned by `/init`.                                           |
| `userId`         | `string` (uuid)            | yes      | Same user that started the session.                            |
| `country`        | `CountryCode`              | yes      | Same country as `/init`.                                       |
| `providedFields` | `Map<string, string>`      | no       | Keyed by the `id` of each `inputRequiredFields` from the prior response. Empty/omitted on the very first `/continue`. |

**Sample request (subsequent call, providing requested fields)**

```json
{
  "sessionId": "9c0d4d84-91a7-4e9c-b0a1-9a2db4d6f5d9",
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "country": "US",
  "providedFields": {
    "passportNumber": "X1234567",
    "passportExpiry": "2030-06-01"
  }
}
```

**`200 OK` response** — `CheckinResponseData`

| Field                 | Type                              | Notes                                                                |
|-----------------------|-----------------------------------|----------------------------------------------------------------------|
| `status`              | `StatusCode`                      | Drives the client state machine — see lifecycle below.               |
| `inputRequiredFields` | `RequiredField[]` (nullable)      | Present when `status = user_input_required`. Each item is a form field the UI must render and submit back. |
| `errorMessage`        | `string` (nullable)               | Present when `status = rejected` (and sometimes for soft warnings).  |

**`RequiredField` shape**

| Field  | Type     | Notes                                                          |
|--------|----------|----------------------------------------------------------------|
| `id`   | `string` | Use as the key in `providedFields` on the next `/continue`.    |
| `name` | `string` | Human-readable label for the form input.                       |
| `type` | `string` | Hint for input rendering (e.g. `text`, `date`, `email`, etc.). |

**Sample response — needs more input**

```json
{
  "status": "user_input_required",
  "inputRequiredFields": [
    { "id": "passportNumber", "name": "Passport Number", "type": "text" },
    { "id": "passportExpiry", "name": "Passport Expiry",  "type": "date" }
  ],
  "errorMessage": null
}
```

**Sample response — completed**

```json
{
  "status": "completed",
  "inputRequiredFields": null,
  "errorMessage": null
}
```

**Sample response — rejected**

```json
{
  "status": "rejected",
  "inputRequiredFields": null,
  "errorMessage": "Passport validation failed: document expired."
}
```

## Status Lifecycle (`StatusCode`)

The `status` enum drives the entire client UX:

| Value                  | Meaning                                                        | What the UI should do                                                              |
|------------------------|----------------------------------------------------------------|------------------------------------------------------------------------------------|
| `initialized`          | Session has just been created.                                 | Immediately call `/continue` to start the flow.                                    |
| `user_input_required`  | The flow is paused, waiting on user-supplied fields.           | Render a form for `inputRequiredFields`, submit answers via `/continue`.           |
| `completed`            | Check-in finished successfully.                                | Show a success / boarding-pass-ready screen. Stop polling.                         |
| `rejected`             | The flow terminated unsuccessfully.                            | Show `errorMessage` and offer retry/contact-support actions. Stop polling.         |

## Country Code (`CountryCode`)

ISO 3166-1 alpha-2 — full list of officially assigned codes (250+ values). Use a typed dropdown in the UI. The most relevant codes for current flows are documented below.

### Country-specific behavior (from service config & flow steps)

| Country | Notable behavior                                                                  |
|---------|-----------------------------------------------------------------------------------|
| `MX`    | Uses a tailored Mexican flow (`MxCheckin`) — typically asks for an **RFC** field. |
| `VE`    | Uses a tailored Venezuelan flow (`VeCheckin`).                                    |
| `IN`, `AU` | Eligible for **digital visa** validation (per `feature.digital.visa.enabled-countries`). |
| `US`    | Tax payment methods enabled: `3DS`, `WIRE`, `GOV`.                                |
| `MX`    | Tax payment methods enabled: `3DS`, `WIRE`.                                       |
| `CA`    | Tax payment methods enabled: `3DS`, `GOV`.                                        |
| All others | Fall back to `GeneralCheckin`.                                                  |

The list of steps the engine may run (in `service/checkin/steps`) gives a sense of the fields the UI may be asked for at runtime: passport information, agreement signing, payment method selection & validation, funds check, tax agreement / calculation, RFC input, digital-visa validation, and final transaction confirmation.

## Flight Number Convention

Flight numbers in this service encode the route. Format:

```
[OriginCountry][OriginAirport][DestCountry][DestAirport]
   2 chars       3 chars         2 chars      3 chars      → 10 chars total
```

Example: `USJFKGBLHF` = **US/JFK → GB/LHF**. Different country pairs trigger different country-specific flows in the simulation.

## Recommended Client Flow (pseudo-code)

```ts
const init = await POST("/v1/checkin/init", initRequest);
let resp = await POST("/v1/checkin/continue", {
  sessionId: init.sessionId,
  userId: initRequest.userId,
  country: initRequest.country,
  providedFields: {},
});

while (resp.status === "user_input_required") {
  const answers = await renderFormAndCollect(resp.inputRequiredFields);
  resp = await POST("/v1/checkin/continue", {
    sessionId: init.sessionId,
    userId: initRequest.userId,
    country: initRequest.country,
    providedFields: answers,
  });
}

if (resp.status === "completed") showSuccess();
else                              showError(resp.errorMessage);
```

## OpenAPI Source

| Artifact          | Path                                                                          |
|-------------------|-------------------------------------------------------------------------------|
| Root spec         | `http/src/main/resources/openapi/openapi.yaml`                                |
| Init request      | `http/src/main/resources/openapi/components/InitRequestData.yaml`             |
| Init response     | `http/src/main/resources/openapi/components/InitResponseData.yaml`            |
| Continue request  | `http/src/main/resources/openapi/components/CheckinRequestData.yaml`          |
| Continue response | `http/src/main/resources/openapi/components/CheckinResponseData.yaml`         |
| Shared schemas    | `http/src/main/resources/openapi/components/shared.yaml`                      |

To regenerate clients/DTOs from the spec, run `./gradlew openApiGenerate` at the repo root.
