# Frontend Architecture Design

**Goal:** Replace the current mixed frontend structure with a clean, backend-aligned application shell and domain-driven feature layout that is easier to scale, safer to maintain, and less likely to drift from API contracts.

**Architecture:** Keep the existing Vite + React + Tailwind stack, but reorganize it around a single API client, a single auth/session source of truth, and feature modules for matters, documents, clients, legal search, AI, calendar, billing, and admin. Route components stay thin; feature modules own data fetching, mapping, and domain UI. Backend DTOs are adapted through small mappers so UI components do not depend on backend shape directly.

**Tech Stack:** React 18, Vite, React Router, TanStack Query, Zustand, Axios, Tailwind CSS, Radix UI, framer-motion.

---

## Current State

The frontend already has the right ingredients:

- Lazy-loaded routes in `App.jsx`
- TanStack Query for server state
- Zustand for auth/session state
- A reusable layout shell with sidebar and topbar
- Separate pages for the main legal workflows

The problem is not the stack. The problem is boundary drift:

- Some screens point to routes that do not exist.
- Some screens use mock data while others use real backend endpoints.
- There are two axios clients with different token strategies.
- There are legacy AI endpoints in the frontend that do not match the backend.
- Some pages read directly from `localStorage` while others use the auth store.

## Primary Bottlenecks

1. **Route mismatch**
   - `DashboardPage` sends users to `/documents` and `/ai`, but these routes are not registered in `App.jsx`.
   - Result: broken navigation and dead UI actions.

2. **API contract drift**
   - The frontend still contains legacy AI calls to `/api/ai/chat`.
   - The backend AI surface is actually `/api/ai/v2/*`.
   - Result: one assistant path works conceptually but is not aligned with the server.

3. **Auth state fragmentation**
   - `useAuthStore` persists session data.
   - `frontend/src/api/client.js` reads the token from the store.
   - `frontend/src/lib/api.js` reads from `localStorage`.
   - `SuperAdminPage` and `DebugPage` also read `localStorage` directly.
   - Result: logout/login can drift and the app can show stale state.

4. **List scaling risk**
   - `MatterTable` performs client-side filtering and sorting over whatever `useMatters` returns.
   - Backend `GET /api/matters` returns a `Page<MatterListDto>`, so the frontend should support server-side pagination.
   - Result: this will become slow and inconsistent once matter counts grow.

5. **Mock-heavy screens**
   - Documents, calendar, billing, and super admin are mostly presentation-only right now.
   - Result: the product feels broader than the backend actually supports.

## Target Frontend Structure

### 1. App Shell Layer

Owns routing, global providers, and cross-cutting concerns.

Suggested responsibilities:

- `App.jsx`
  - Route declarations only
  - No business logic
  - Lazy-load page modules
- `main.jsx`
  - Query client, router bootstrap, global error surface
- `components/layout/*`
  - Sidebar, topbar, app frame
- `components/common/*`
  - Reusable app-level primitives like error boundaries, empty states, loading states

### 2. Shared Layer

Owns anything reused by multiple features.

Suggested responsibilities:

- `shared/api/client`
  - One axios instance
  - One auth token source
  - One error normalization path
- `shared/lib`
  - Route constants, formatting helpers, query keys, permission helpers
- `shared/ui`
  - Base design-system primitives and wrapper components

### 3. Feature Layer

Each domain gets its own module with its own hooks, API access, and UI pieces.

Suggested modules:

- `features/auth`
- `features/matters`
- `features/documents`
- `features/clients`
- `features/legal-search`
- `features/ai`
- `features/calendar`
- `features/billing`
- `features/admin`

Each feature should own:

- data-fetching hooks
- DTO-to-view-model mappers
- domain components
- local empty/loading/error states

### 4. Pages Layer

Pages should be thin route wrappers.

Responsibilities:

- compose feature components
- read route params
- pass feature-specific props
- avoid direct fetch logic unless the page is intentionally tiny

## Backend Alignment

The frontend should explicitly align itself to the backend contract below:

- Auth
  - `POST /api/auth/login`
- Matters
  - `GET /api/matters`
  - `GET /api/matters/{matterId}`
  - `POST /api/matters`
- Documents
  - `GET /api/documents/matter/{matterId}`
  - `POST /api/documents/upload`
- Clients
  - `GET /api/clients`
- Legal search
  - `POST /api/rag/search`
- AI
  - `POST /api/ai/v2/chat`
  - `POST /api/ai/v2/chat/stream`
  - `POST /api/ai/v2/search`

Any frontend route or UI affordance that depends on an endpoint not in this list should be treated as roadmap-only, not as a live feature.

## Route Map

Keep the visible shell, but make the navigation honest:

- `/login`
- `/dashboard`
- `/matters`
- `/matters/:matterId`
- `/calendar`
- `/legal-search`
- `/clients`
- `/billing`
- `/settings`
- `/super-admin`

If documents and AI are first-class entry points, they need explicit routes too:

- `/documents`
- `/ai`

Those routes should either:

- be added as real pages, or
- be removed from in-app navigation until they are ready

## Data Flow

### Authentication

1. Login form submits credentials to `POST /api/auth/login`
2. Response is normalized into a single auth shape
3. Zustand persists the token, user, role, and org id
4. Axios request interceptor injects the bearer token
5. 401 handling clears the store and redirects to `/login`

### Matters

1. Matter list page loads page 0 from `GET /api/matters`
2. Backend pagination metadata drives table paging controls
3. Matter detail page loads `GET /api/matters/{matterId}`
4. Create matter mutation invalidates the list query

### Documents

1. Matter detail page opens the document manager
2. Document list comes from `GET /api/documents/matter/{matterId}`
3. Upload uses `multipart/form-data`
4. Mutation invalidates the matter document query only

### AI and Search

1. AI assistant uses only the v2 service
2. RAG search screen uses `/api/rag/search`
3. Stream chat uses `fetch` or SSE-aware transport, not axios
4. Any legacy AI service wrapper stays out of route flow

## UX and Design Direction

The current visual language is already usable, but the app would benefit from a stronger editorial/legal-office tone:

- keep the shell restrained and professional
- use a denser information hierarchy in matter and document views
- preserve a premium tone with careful spacing and subtle motion
- avoid making every module feel like a separate product

The design should feel like a serious case-management tool, not a generic admin panel.

## Testing Strategy

The frontend should be tested at three levels:

- route-level smoke checks for navigation and protected access
- query/hook tests for backend-aligned data fetching
- feature-level interaction checks for matter creation, document upload, and AI search

Minimum confidence checks:

- login redirects to the right destination
- sidebar routes all resolve
- matter list can load, sort, and navigate
- document upload invalidates and refreshes correctly
- AI screens call the v2 backend only

## Acceptance Criteria

The frontend restructure is successful when:

- there is one canonical API client
- there is one canonical auth/session store
- every route in the shell maps to a real screen
- no live screen depends on the legacy `/api/ai/chat` path
- matter list and detail screens consume backend-aligned data
- mock-only pages are clearly separated from real backend-backed pages
- route modules are thin, feature modules own logic, and the codebase is easier to extend

## Recommended Implementation Order

1. Unify auth and API plumbing
2. Fix route map and navigation drift
3. Align matters and documents to backend contracts
4. Migrate AI screens to v2 only
5. Separate live screens from mock/demo surfaces
6. Add pagination and query-key discipline

