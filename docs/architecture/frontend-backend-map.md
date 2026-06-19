# Frontend Backend Map

This note is a short reference for how the frontend shell should line up with the Spring backend.

## 1. Shell Layout

- `App.jsx` owns routing and lazy loading only.
- `components/layout/*` owns the application frame.
- `components/common/*` owns cross-cutting UI like error states and empty states.
- Feature logic should live in the domain area that uses it, not in the route wrapper.

## 2. Canonical Backend Surface

These are the backend endpoints the frontend can treat as live:

- Auth: `POST /api/auth/login`
- Matters: `GET /api/matters`, `GET /api/matters/{matterId}`, `POST /api/matters`
- Documents: `GET /api/documents/matter/{matterId}`, `POST /api/documents/upload`
- Clients: `GET /api/clients`
- Search: `GET /api/search`, `POST /api/rag/search`
- AI v2: `POST /api/ai/v2/chat`, `POST /api/ai/v2/chat/stream`, `POST /api/ai/v2/search`

Anything outside that list should be treated as roadmap-only unless the backend adds a controller for it.

## 3. Route Map

Live routes:

- `/login`
- `/dashboard`
- `/matters`
- `/matters/:matterId`
- `/documents`
- `/ai`
- `/legal-search`
- `/clients`
- `/settings`

Preview or roadmap routes:

- `/calendar`
- `/billing`
- `/super-admin`

The preview routes should be visually labeled so users do not mistake them for finished product areas.

## 4. Data Flow

### Auth

1. Login form posts to `POST /api/auth/login`
2. Response is normalized into the auth store
3. Axios interceptor reads the token from the store
4. `401` clears the session and returns the user to `/login`

### Matters

1. Matter list loads a backend `Page<MatterListDto>`
2. Table paging comes from server pagination, not a flattened client array
3. Matter detail reads `GET /api/matters/{matterId}`
4. Create mutation invalidates matter queries

### Documents

1. Matter detail and `/documents` both read matter-specific documents
2. Upload uses `multipart/form-data`
3. Document mutations invalidate only the matching matter query

### AI and Search

1. The assistant uses only `/api/ai/v2/*`
2. Legal search uses `/api/rag/search`
3. Streamed chat should use `fetch` or SSE-aware transport, not axios

## 5. Bottlenecks To Watch

- Do not read auth from multiple sources. The store must remain the single source of truth.
- Do not keep separate axios clients with different token injection rules.
- Do not render large matter lists with pure client-side filtering if the backend already returns pages.
- Do not present preview routes as finished backend-backed product areas.

## 6. Recommended Next Steps

1. Keep backend-aligned screens live and thin.
2. Keep roadmap surfaces clearly labeled.
3. Add backend controllers before upgrading preview routes into product routes.
4. Keep this map updated whenever a new route or controller is added.

