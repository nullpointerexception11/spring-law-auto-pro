# spring-law-auto-pro

## Docker ile hýzlý baþlangýç
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

Not: Bu wrapper yerel Maven gerektirmez; Java ile Maven Wrapper indirerek çalýþýr.

## Frontend (React + Vite)
1. `cd frontend`
2. `npm install`
3. `npm run dev`

## Auth endpointleri
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/health`

## Notlar
- Flyway migration: `backend/src/main/resources/db/migration/V1__init.sql`
- JWT ayarlarý environment ile override edilebilir:
  - `APP_JWT_SECRET`
  - `APP_JWT_EXPIRATION_MINUTES`

## Operations API
Base path: /api/operations
- hearings, deadlines, calendar-events, petitions, evidences, client-notes, case-payments, case-fee-terms, file-objects, delete-requests
- Cogu endpoint JWT ister (Authorization: Bearer <token>)

## Postman
Collection: postman/spring-law-auto.postman_collection.json


