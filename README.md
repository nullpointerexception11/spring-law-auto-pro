# spring-law-auto-pro

## Docker ile hızlı başlangıç
1. `cd "C:\Users\Orhan's Comp\Desktop\spring-law-auto-pro"`
2. `docker compose up --build`

Servisler:
- Backend: `http://localhost:8080`
- Health: `http://localhost:8080/api/health`
- PostgreSQL: `localhost:5432` (`postgres/postgres`, db: `law_auto`)

Durdurma:
- `docker compose down`
- Volume dahil temizlemek için: `docker compose down -v`

## Backend test/build (Maven yoksa)
`backend` içinde:
- Test: `./mvnw test` (Windows: `mvnw.cmd test`)
- Compile: `./mvnw -DskipTests compile`
- Run: `./mvnw spring-boot:run`

Not: Bu wrapper yerel Maven gerektirmez; Java ile Maven Wrapper indirerek çalışır.

## Frontend (React + Vite)
1. `cd frontend`
2. `npm install`
3. `npm run dev`

### Frontend mimarisi
- Tek HTTP istemcisi: `frontend/src/api/client.js`
- Auth kaynağı: `frontend/src/store/useAuthStore.js`
- API uyumluluk katmanı: `frontend/src/lib/api.js` sadece eski importları taşır
- Canlı route'lar:
  - `/dashboard`
  - `/matters`
  - `/matters/:matterId`
  - `/documents`
  - `/ai`
  - `/legal-search`
  - `/clients`
  - `/settings`
- Önizleme route'lar:
  - `/calendar`
  - `/billing`
  - `/super-admin`

### Frontend'in kullandığı backend yüzeyleri
- Auth: `POST /api/auth/login`, `POST /api/auth/register`
- Davalar: `GET /api/matters`, `GET /api/matters/{id}`
- Belgeler: `GET /api/documents/matter/{matterId}`, `POST /api/documents/upload`
- AI: `POST /api/ai/v2/chat`, `POST /api/ai/v2/chat/stream`
- RAG: `POST /api/rag/search`, `POST /api/rag/hybrid-search`
- Müşteriler: `GET /api/clients`
- Global arama: `POST /api/search`

### Önizleme yüzeyleri
- Takvim, finans ve süper admin ekranları şu anda gerçek backend akışına bağlı değil.
- Bu alanlar shell içinde görünür ama "Önizleme" olarak işaretlenir.

## Auth endpointleri
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/health`

## Notlar
- Flyway migration: `backend/src/main/resources/db/migration/V1__init.sql`
- JWT ayarları environment ile override edilebilir:
  - `APP_JWT_SECRET`
  - `APP_JWT_EXPIRATION_MINUTES`

## Operations API
Base path: /api/operations
- hearings, deadlines, calendar-events, petitions, evidences, client-notes, case-payments, case-fee-terms, file-objects, delete-requests
- Cogu endpoint JWT ister (Authorization: Bearer <token>)

## Postman
Collection: postman/spring-law-auto.postman_collection.json



