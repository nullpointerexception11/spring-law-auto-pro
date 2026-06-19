# Frontend Architecture Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reorganize the frontend into a backend-aligned, feature-driven structure with one API client, one auth source of truth, and a clean route map.

**Architecture:** Preserve the current React + Vite shell, but move domain logic into feature modules and make route components thin wrappers. Normalize all network calls through a single axios client, keep auth/session in one Zustand store, and align every live screen with actual backend endpoints. Mock-only screens should be isolated and clearly labeled so they do not masquerade as live product areas.

**Tech Stack:** React 18, Vite, React Router, TanStack Query, Zustand, Axios, Tailwind CSS, Radix UI, framer-motion.

---

### Task 1: Unify auth state and API client

**Files:**
- Modify: `frontend/src/store/useAuthStore.js`
- Modify: `frontend/src/api/client.js`
- Modify: `frontend/src/lib/api.js`
- Modify: `frontend/src/pages/LoginPage.jsx`
- Modify: `frontend/src/pages/SuperAdminPage.jsx`
- Modify: `frontend/src/pages/DebugPage.jsx`
- Modify: `frontend/src/api/aiService.js`
- Modify: `frontend/src/api/aiV2Service.js`
- Modify: `frontend/src/api/ragService.js`
- Modify: `frontend/src/pages/ClientsPage.jsx`
- Modify: `frontend/src/pages/LegalSearchPage.jsx`

- [ ] **Step 1: Move session access behind the auth store**

  Replace direct `localStorage.getItem(...)` reads with `useAuthStore` selectors or store helpers so token, role, and org state all come from one place.

- [ ] **Step 2: Remove the duplicate axios client**

  Make `frontend/src/lib/api.js` a compatibility shim or retire it entirely so every request uses `frontend/src/api/client.js`.

- [ ] **Step 3: Keep 401 handling centralized**

  Ensure the interceptor clears auth state once and redirects to `/login` without each page re-implementing logout behavior.

- [ ] **Step 4: Smoke test the auth flow**

  Run: `npm run build` from `frontend`

  Expected: build succeeds with no import or client-resolution errors.

---

### Task 2: Fix the route map and navigation drift

**Files:**
- Modify: `frontend/src/App.jsx`
- Modify: `frontend/src/lib/constants.js`
- Modify: `frontend/src/components/layout/Sidebar.jsx`
- Modify: `frontend/src/pages/DashboardPage.jsx`
- Modify: `frontend/src/pages/DocumentsPage.jsx`
- Modify: `frontend/src/pages/AiAssistantPage.jsx`

- [ ] **Step 1: Add every real screen to the router**

  Register `/documents` and `/ai` explicitly, or remove those navigation targets if they are not meant to be public routes yet.

- [ ] **Step 2: Make `/ai` resolve to the backend-aligned assistant**

  Point the route to the v2 AI page so the shell does not expose a legacy assistant entry point.

- [ ] **Step 3: Update shell navigation to only show live destinations**

  Keep sidebar items and dashboard shortcuts in sync with the router so users never click into a dead path.

- [ ] **Step 4: Smoke test route rendering**

  Run: `npm run build` from `frontend`

  Expected: route imports resolve and the build completes successfully.

---

### Task 3: Align matters to backend paging and detail models

**Files:**
- Modify: `frontend/src/hooks/useMatters.js`
- Modify: `frontend/src/components/matters/MatterTable.jsx`
- Modify: `frontend/src/components/matters/CreateMatterModal.jsx`
- Modify: `frontend/src/pages/matters/MatterList.jsx`
- Modify: `frontend/src/pages/matters/MatterDetail.jsx`
- Modify: `frontend/src/pages/DashboardPage.jsx`

- [ ] **Step 1: Make matter list fetching page-aware**

  Read the backend `Page<MatterListDto>` shape and keep page/size/sort state in the query key instead of flattening everything into one client-side array.

- [ ] **Step 2: Move search and sorting to the server where it matters**

  Keep client-side filtering only for tiny helper filters; the primary table should consume backend pagination metadata.

- [ ] **Step 3: Stabilize optimistic create behavior**

  Make sure new matters are inserted in a way that still works when the list is paginated.

- [ ] **Step 4: Smoke test matter screens**

  Run: `npm run build` from `frontend`

  Expected: the table, detail view, and dashboard import the updated hook contract without type or runtime breakage.

---

### Task 4: Consolidate documents and AI into real live flows

**Files:**
- Modify: `frontend/src/hooks/useDocuments.js`
- Modify: `frontend/src/components/matters/DocumentManager.jsx`
- Modify: `frontend/src/pages/DocumentsPage.jsx`
- Modify: `frontend/src/api/aiV2Service.js`
- Modify: `frontend/src/components/AiChat.jsx`
- Modify: `frontend/src/pages/AiAssistantPageV2.jsx`
- Modify: `frontend/src/pages/AiAssistantPage.jsx`
- Modify: `frontend/src/components/ai/RagSearchPanel.jsx`
- Modify: `frontend/src/pages/LegalSearchPage.jsx`

- [ ] **Step 1: Make document screens share the same live source**

  Ensure the matter-level document manager and the top-level documents screen both use the same backend-backed document data model.

- [ ] **Step 2: Retire legacy AI paths**

  Route every chat/search flow through `/api/ai/v2/*` and remove any dependency on `/api/ai/chat`.

- [ ] **Step 3: Reuse the RAG service everywhere**

  Keep legal search and AI-assisted search on the same service layer so results are consistent.

- [ ] **Step 4: Smoke test AI/document flows**

  Run: `npm run build` from `frontend`

  Expected: assistant and document imports resolve cleanly and all API clients are on the same backend contract.

---

### Task 5: Separate mock-only screens from live product surfaces

**Files:**
- Modify: `frontend/src/pages/CalendarPage.jsx`
- Modify: `frontend/src/pages/BillingPage.jsx`
- Modify: `frontend/src/pages/SuperAdminPage.jsx`
- Modify: `frontend/src/pages/SettingsPage.jsx`
- Modify: `frontend/src/components/layout/Sidebar.jsx`

- [ ] **Step 1: Label non-backend-backed screens honestly**

  Keep demo-only data in isolated pages or clearly mark these areas as roadmap surfaces so they do not appear production-complete.

- [ ] **Step 2: Remove fake actions from core navigation if needed**

  If a screen has no backend support yet, keep it out of the primary shell navigation until it is real.

- [ ] **Step 3: Keep settings and admin state store-driven**

  Use the shared auth store for role and session display so those pages remain consistent after logout/login.

- [ ] **Step 4: Smoke test the final shell**

  Run: `npm run build` from `frontend`

  Expected: the shell compiles and all live routes are reachable without dead links.

---

### Task 6: Final verification and handoff

**Files:**
- Modify: `frontend/src/App.jsx`
- Modify: `frontend/src/components/layout/AppLayout.jsx`
- Modify: `frontend/src/lib/constants.js`
- Modify: `README.md`

- [ ] **Step 1: Verify the route map against the design spec**

  Check that every user-facing route either maps to a real backend-backed feature or is deliberately marked as non-live.

- [ ] **Step 2: Update the project README**

  Rewrite the frontend section so it documents the real route map, the shared API client, and the supported backend endpoints.

- [ ] **Step 3: Run final build verification**

  Run: `npm run build` from `frontend`

  Expected: production build passes before the branch is considered ready.

