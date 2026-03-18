# AVANCE — Issue #17: Mexico (MX) Tax Compliance

> **Estado general:** 20 / 20 tareas completadas · 0 pendientes ✅

---

## Día 1 — Fundación y paralelos independientes

### Dev 1 — Fundación de Dominio ✅ LISTO

| # | Tarea | Archivo | Estado |
|---|---|---|---|
| 1 | `rfc`, `taxAmount`, `digitalSign` en `UserInformation` | `service/.../session/UserInformation.java` | ✅ Listo |
| 2 | `RFC` y `DIGITAL_SIGN` en `RequiredField` | `service/.../model/RequiredField.java` | ✅ Listo |
| 3 | Interfaz `TaxGateway` | `service/.../external/TaxGateway.java` | ✅ Listo |
| 4 | Record `TaxData` | `service/.../model/session/TaxData.java` | ✅ Listo |

---

### Dev 4 — DTO y Cliente HTTP ✅ LISTO

| # | Tarea | Archivo | Estado |
|---|---|---|---|
| 10 | Record `TaxDto` | `http/.../gateway/tax/model/v1/TaxDto.java` | ✅ Listo |
| 11 | Interfaz `TaxHttpClient` (`@HttpExchange`) | `http/.../gateway/tax/TaxHttpClient.java` | ✅ Listo |
| 13 | `http.client.tax.base.url` en `application.properties` | `http/src/main/resources/application.properties` | ✅ Listo |

---

### Dev 5 — WireMock y enum Groovy ✅ LISTO

| # | Tarea | Archivo | Estado |
|---|---|---|---|
| 14 | Stub éxito: RFC válido → 200 `taxAmount: 45.00` | `docker/wiremock/mappings/tax-service-success.json` | ✅ Listo |
| 15 | Stub error: RFC termina en `1` → 406 | `docker/wiremock/mappings/tax-service-error.json` | ✅ Listo |
| 16 | `RFC` y `DIGITAL_SIGN` en `UserInput.groovy` | `integration/.../model/UserInput.groovy` | ✅ Listo |

---

## Día 1–2 — Steps y flujo (desbloqueados por Dev 1)

### Dev 2 — Steps RFC y Cálculo de Impuestos ✅ LISTO

| # | Tarea | Archivo | Estado |
|---|---|---|---|
| 5 | `RfcInputStep` | `service/.../steps/RfcInputStep.java` | ✅ Listo |
| 6 | `TaxCalculationStep` (con manejo 406) | `service/.../steps/TaxCalculationStep.java` | ✅ Listo |
| 18 | `RfcInputStepTest` (unit tests) | `service/.../steps/RfcInputStepTest.java` | ✅ Listo |
| 19 | `TaxCalculationStepTest` (unit tests) | `service/.../steps/TaxCalculationStepTest.java` | ✅ Listo |

---

### Dev 3 — Step de Firma Digital + Flujo MX ✅ LISTO

| # | Tarea | Archivo | Estado |
|---|---|---|---|
| 7 | `TaxAgreementStep` | `service/.../steps/TaxAgreementStep.java` | ✅ Listo |
| 8 | `MxContinueFlow` (cadena completa de steps) | `service/.../flow/MxContinueFlow.java` | ✅ Listo |
| 9 | `MxCheckin` (estrategia `CountryCode.MX`) | `service/.../country/MxCheckin.java` | ✅ Listo |
| 20 | `TaxAgreementStepTest` (unit tests) | `service/.../steps/TaxAgreementStepTest.java` | ✅ Listo |

---

## Día 2–3 — Adapter HTTP (desbloqueado por Dev 1)

### Dev 4 — Adapter `TaxGateway` ✅ LISTO

| # | Tarea | Archivo | Estado |
|---|---|---|---|
| 12 | `TaxGatewayAdapter` (`@Service` que implementa `TaxGateway`, inyecta `TaxHttpClient`, mapea `TaxDto → TaxData`, captura WebClientResponseException 406) | `http/.../gateway/tax/TaxGatewayAdapter.java` | ✅ Listo |

> **Referencia:** seguir el patrón de `ExperimentalGatewayAdapter`. Sin este bean, el contexto de Spring no arranca.

---

## Día 3 — Test de integración end-to-end (depende de todos los anteriores)

### Dev 5 — MxContinueFlowTest ✅ LISTO

| # | Tarea | Archivo | Estado |
|---|---|---|---|
| 17 | `MxContinueFlowTest.groovy` — 3 escenarios Spock: happy path completo, RFC inválido (termina en `1`) → rejected, RFC ya en sesión (se salta `RfcInputStep`) | `integration/.../test/mx/MxContinueFlowTest.groovy` | ✅ Listo |

---

## Resumen de tareas pendientes

No hay tareas pendientes. Todas las 20 tareas han sido completadas. ✅

---

## Verificación final

Una vez completadas las 2 tareas pendientes:

```bash
# 1. Unit tests del módulo service
./gradlew :service:test

# 2. Compilación completa con checkstyle y spotless
./gradlew :http:build

# 3. Tests de integración end-to-end (requiere Docker)
docker-compose up -d
./gradlew :integration:integrationTest