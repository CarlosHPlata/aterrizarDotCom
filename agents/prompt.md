# UI Generation Prompt — Aterrizar Punto Com Check-in

> Paste this prompt (along with `api-documentation.md`, ideally in the same context) into a code-generation model (Claude, v0, Cursor, Lovable, etc.) to produce a polished web UI for the Aterrizar Punto Com check-in microservice.

---

## Role

You are a senior product designer and front-end engineer. Build a **production-quality web UI** for the **Aterrizar Punto Com online check-in flow**. The UI should feel like a modern airline app — confident, calm, and unmistakably *travel*. Think Aviasales × Linear × Apple Wallet boarding passes: clean typography, generous whitespace, subtle motion, dark-mode-first.

## Product Context

Aterrizar Punto Com is a fictional Latin-American travel platform. This particular UI is the **passenger-facing online check-in**. Travellers arrive at the page from an email link a few hours before their flight. Their goals, in order:

1. Confirm who they are and which flight(s) they're checking in for.
2. Answer whatever country-/route-specific questions the backend asks (passport, visa, taxes, payments, signed agreements, etc.).
3. Walk away with confirmation that they're checked in.

The key insight: **the UI does not know in advance which fields will be requested**. The backend drives the flow dynamically, one step at a time, based on the passenger's nationality and route.

## Backend Contract (must follow exactly)

The backend exposes only two REST endpoints, both `POST` and `application/json`. See `api-documentation.md` for full schemas.

1. `POST /v1/checkin/init` — start a session. Returns `{ sessionId, status: "initialized" }`.
2. `POST /v1/checkin/continue` — advance the flow. Returns one of:
   - `{ status: "user_input_required", inputRequiredFields: [{ id, name, type }, ...] }` → render a form, collect answers, POST them back as `providedFields` (keyed by `id`).
   - `{ status: "completed" }` → show success screen.
   - `{ status: "rejected", errorMessage }` → show failure screen.

The client must **loop on `/continue`** until the status is terminal (`completed` or `rejected`).

**Base URL** (local dev): `http://localhost:8080/aterrizar`. Make this configurable via env var (e.g. `NEXT_PUBLIC_API_BASE_URL`).

## Required Screens & States

Design and implement the following:

1. **Landing / Init form** — collects `country` (searchable dropdown of ISO 3166-1 alpha-2 codes), `userId` (uuid; can be prefilled from query string), `passengers` (numeric stepper), `email`, and `flightNumbers` (chip input, ≥1 item, validate the 10-char route encoding `CCAAA­CCAAA`). Submit button calls `/init`.
2. **Flight summary card** — once `/init` succeeds, decode each flight number (`USJFKGBLHF` → US/JFK → GB/LHF) and render a beautiful itinerary card with origin/destination, flag chips, and a small route line.
3. **Dynamic step form** — for every `user_input_required` response, render a form whose inputs come from `inputRequiredFields`. Map `type` to the correct widget:
   - `text` → text input
   - `email` → email input
   - `date` → date picker
   - `number` → numeric input
   - unknown / fallback → text input
   - Use the `name` field as the visible label.
   - Submit collects values into `providedFields` keyed by `id` and POSTs `/continue`.
4. **Progress indicator** — a subtle stepper or progress bar that grows as the user advances through steps (it doesn't need to know the total — just animate forward on each successful response).
5. **Success screen** — celebratory but tasteful. Show a stylized "Check-in complete" with the session ID, flight legs, and a (mocked) "Add to Wallet" / "Download boarding pass" CTA.
6. **Rejection screen** — show `errorMessage` clearly, offer a "Start over" action and a "Contact support" link.
7. **Error / network state** — handle 4xx/5xx and offline gracefully with a retry pattern.
8. **Loading skeletons** — every transition should show a skeleton, not a spinner-on-blank-page.

## Country-aware UX Touches

- When `country = MX`, anticipate an `RFC` field — pre-format the input mask if it appears.
- When `country ∈ {IN, AU}`, surface a small "Digital visa eligible" hint in the header (these countries get the digital-visa step).
- Highlight the selected country with its flag emoji throughout (don't ship raster flag assets).

## Design System

- **Stack**: Next.js 14+ (App Router) + TypeScript + Tailwind CSS + shadcn/ui (Radix primitives) + Framer Motion for transitions + TanStack Query for the API state machine + Zod for runtime validation of API responses.
- **Typography**: Inter for UI, a humanist serif (e.g. Fraunces) for hero headings.
- **Color**: dark-mode default. Primary `#5B8DEF` (sky blue). Accent `#F4A261` (sunset). Surfaces in deep navy `#0B1220`. Provide a light theme as well, toggleable.
- **Motion**: opacity + 8px slide on screen transitions; spring easing; never longer than 300ms.
- **Components**: cards with 1px hairline borders, `rounded-2xl`, soft shadow on hover only.
- **Iconography**: Lucide.
- **i18n-ready**: copy belongs in a single `messages.ts` file. Default language: English. Provide Spanish strings as a second locale (the brand is Latin-American).
- **Accessibility**: WCAG AA. All forms keyboard-navigable, visible focus rings, `aria-live` regions for status changes, `prefers-reduced-motion` respected.

## Code Quality Bar

- Strict TypeScript (`strict: true`, no `any`).
- API client lives in `lib/api/checkin.ts` with typed request/response models matching the OpenAPI schemas.
- A single `useCheckinSession()` hook owns the state machine: `idle → initializing → awaiting_input → submitting → completed | rejected | error`.
- No business logic in components — components consume the hook.
- One Vitest spec per non-trivial module; one Playwright happy-path E2E that mocks the two endpoints.
- ESLint + Prettier configured. Repo runs cleanly with `pnpm dev`, `pnpm test`, `pnpm build`.

## Suggested File Layout

```
app/
  layout.tsx
  page.tsx                  # landing / init form
  checkin/[sessionId]/page.tsx   # dynamic flow + terminal screens
components/
  ui/                       # shadcn primitives
  flow/
    InitForm.tsx
    DynamicStepForm.tsx
    FlightSummaryCard.tsx
    ProgressBar.tsx
    SuccessScreen.tsx
    RejectionScreen.tsx
lib/
  api/checkin.ts
  hooks/useCheckinSession.ts
  flight/parseFlightNumber.ts
  countries.ts              # ISO 3166-1 alpha-2 list + flag emoji helper
messages/
  en.ts
  es.ts
tests/
  unit/...
  e2e/checkin.spec.ts
```

## Deliverables

1. The full Next.js project, runnable with `pnpm install && pnpm dev`.
2. A short `README.md` covering setup, env vars, and how to point the UI at a local backend (`docker-compose up -d` + `./gradlew bootRun`).
3. Mock-mode toggle: when `NEXT_PUBLIC_USE_MOCKS=true`, the API client returns canned responses so the UI can be demoed without the backend.
4. At least one screenshot or recording of the happy path in the README.

## Out of Scope (do not build)

- User authentication / account management.
- Real payment processing — the backend simulates this; never collect real card data.
- Real boarding-pass PDFs — a mocked download is fine.
- Admin / agent dashboards.

## Definition of Done

- Init → continue → success path works end-to-end against a real backend running on `localhost:8080/aterrizar`.
- Rejection path renders the backend's `errorMessage` faithfully.
- A new `inputRequiredFields` shape that the UI has never seen still renders correctly (the form is fully data-driven).
- Lighthouse: Performance ≥ 90, Accessibility ≥ 95, Best Practices ≥ 95 on the production build.
- No `console.error` during the happy path.
