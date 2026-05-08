-- LawAuto Sovereign LMMS Master Schema (V1 - PRESTIGE PLATINUM v8.1)
-- Enterprise-Grade Legal Technology with FTS, RLS, and Audit Triggers
-- Implements Decision Tracking, Official Correspondence, Folder Hierarchy, KVKK, and Financials

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------------------------------------------------
-- 1. ENUMS (Production Grade Domain Language)
-- ---------------------------------------------------------
CREATE TYPE "RecordStatus" AS ENUM ('ACTIVE', 'ARCHIVED', 'LOCKED', 'DELETED', 'ANONYMIZED');
CREATE TYPE "UserStatus" AS ENUM ('ACTIVE', 'INVITED', 'SUSPENDED', 'DISABLED', 'PENDING_SETUP');
CREATE TYPE "RoleKey" AS ENUM ('PLATFORM_OWNER', 'PLATFORM_ADMIN', 'ORG_ADMIN', 'LAWYER', 'SECRETARY');
CREATE TYPE "MatterType" AS ENUM ('LITIGATION', 'CONSULTATION', 'EXECUTION', 'MEDIATION', 'ADVISORY');
CREATE TYPE "MatterStatus" AS ENUM ('OPEN', 'CLOSED', 'ON_HOLD', 'ARCHIVED');
CREATE TYPE "PartyType" AS ENUM ('PERSON', 'COMPANY', 'GOVERNMENT_BODY');
CREATE TYPE "PartyRole" AS ENUM ('CLIENT', 'OPPONENT', 'WITNESS', 'EXPERT', 'JUDGE', 'INSURANCE_COMPANY', 'OTHER');
CREATE TYPE "AssignmentRole" AS ENUM ('LEAD', 'ASSISTANT', 'SECRETARY');
CREATE TYPE "DecisionType" AS ENUM ('INTERIM', 'FINAL', 'APPEAL', 'SUPREME_COURT');
CREATE TYPE "CorrDirection" AS ENUM ('INCOMING', 'OUTGOING');
CREATE TYPE "CorrType" AS ENUM ('NOTIFICATION', 'EXPERT_REPORT', 'COURT_ORDER', 'PETITION_REPLY');
CREATE TYPE "JobStatus" AS ENUM ('QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED');
CREATE TYPE "UniversalEventType" AS ENUM ('HEARING', 'DEADLINE', 'MEETING', 'TASK', 'REMINDER');
CREATE TYPE "UniversalEventStatus" AS ENUM ('PENDING', 'COMPLETED', 'CANCELLED', 'MISSED');
CREATE TYPE "InvoiceStatus" AS ENUM ('DRAFT', 'ISSUED', 'PAID', 'OVERDUE', 'CANCELLED');
CREATE TYPE "PaymentMethod" AS ENUM ('CASH', 'BANK_TRANSFER', 'CREDIT_CARD');

-- ---------------------------------------------------------
-- 2. AUTOMATION: Trigger Function for updatedAt
-- ---------------------------------------------------------
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW."updatedAt" = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- ---------------------------------------------------------
-- 3. CORE INFRASTRUCTURE
-- ---------------------------------------------------------
CREATE TABLE "Org" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "name" TEXT NOT NULL,
  "subdomain" TEXT UNIQUE,
  "status" "RecordStatus" NOT NULL DEFAULT 'ACTIVE',
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "User" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID REFERENCES "Org"("id") ON DELETE RESTRICT,
  "email" TEXT NOT NULL UNIQUE,
  "fullName" TEXT NOT NULL,
  "passwordHash" TEXT,
  "status" "UserStatus" NOT NULL DEFAULT 'PENDING_SETUP',
  "consentGivenAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 4. AUTHORIZATION SYSTEM (AccessPolicy Foundation)
-- ---------------------------------------------------------
CREATE TABLE "Role" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID REFERENCES "Org"("id") ON DELETE CASCADE,
  "key" "RoleKey" NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "Permission" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "key" TEXT NOT NULL UNIQUE,
  "description" TEXT
);

CREATE TABLE "RolePermission" (
  "roleId" UUID NOT NULL REFERENCES "Role"("id") ON DELETE CASCADE,
  "permissionId" UUID NOT NULL REFERENCES "Permission"("id") ON DELETE CASCADE,
  PRIMARY KEY ("roleId", "permissionId")
);

-- ---------------------------------------------------------
-- 5. THE PARTY SYSTEM (KVKK Ready + Search)
-- ---------------------------------------------------------
CREATE TABLE "Party" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "type" "PartyType" NOT NULL DEFAULT 'PERSON',
  "fullName" TEXT NOT NULL,
  "phone" TEXT,
  "email" TEXT,
  "address" TEXT,
  "taxNo" TEXT, 
  "dataJson" JSONB,
  "status" "RecordStatus" NOT NULL DEFAULT 'ACTIVE',
  "consentGivenAt" TIMESTAMPTZ,
  "dataRetentionEndDate" TIMESTAMPTZ,
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "fts" tsvector GENERATED ALWAYS AS (to_tsvector('turkish', "fullName" || ' ' || coalesce("taxNo", ''))) STORED
);

-- ---------------------------------------------------------
-- 6. THE MATTER SYSTEM (Advanced Domain Logic)
-- ---------------------------------------------------------
CREATE TABLE "Matter" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "type" "MatterType" NOT NULL DEFAULT 'LITIGATION',
  "title" TEXT NOT NULL,
  "status" "MatterStatus" NOT NULL DEFAULT 'OPEN',
  "referenceNumber" TEXT,
  "descriptionHtml" TEXT,
  "openedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "closedAt" TIMESTAMPTZ,
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "recordStatus" "RecordStatus" NOT NULL DEFAULT 'ACTIVE',
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "fts" tsvector GENERATED ALWAYS AS (to_tsvector('turkish', "title" || ' ' || coalesce("referenceNumber", ''))) STORED
);

CREATE TABLE "MatterParty" (
  "matterId" UUID NOT NULL REFERENCES "Matter"("id") ON DELETE CASCADE,
  "partyId" UUID NOT NULL REFERENCES "Party"("id") ON DELETE RESTRICT,
  "role" "PartyRole" NOT NULL DEFAULT 'CLIENT',
  "isPrimary" BOOLEAN NOT NULL DEFAULT false,
  PRIMARY KEY ("matterId", "partyId")
);

CREATE TABLE "MatterDecision" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "matterId" UUID NOT NULL REFERENCES "Matter"("id") ON DELETE CASCADE,
  "decisionType" "DecisionType" NOT NULL,
  "decisionDate" DATE NOT NULL,
  "summary" TEXT,
  "fullTextHtml" TEXT,
  "appealDeadline" TIMESTAMPTZ,
  "isAppealFiled" BOOLEAN DEFAULT false,
  "fileId" UUID,
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "Correspondence" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "matterId" UUID NOT NULL REFERENCES "Matter"("id") ON DELETE CASCADE,
  "direction" "CorrDirection" NOT NULL,
  "type" "CorrType" NOT NULL,
  "date" DATE NOT NULL,
  "referenceNo" TEXT,
  "summary" TEXT,
  "registeredByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 7. STORAGE & DOCUMENT MANAGEMENT
-- ---------------------------------------------------------
CREATE TABLE "FileFolder" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "name" TEXT NOT NULL,
  "parentFolderId" UUID REFERENCES "FileFolder"("id") ON DELETE CASCADE,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE CASCADE,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "FileObject" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "folderId" UUID REFERENCES "FileFolder"("id") ON DELETE SET NULL,
  "storageProvider" TEXT NOT NULL DEFAULT 'LOCAL', -- S3, AZURE, GCP
  "bucket" TEXT,
  "storageKey" TEXT NOT NULL,
  "fileName" TEXT NOT NULL,
  "mimeType" TEXT,
  "sizeBytes" BIGINT,
  "sha256" TEXT,
  "ocrStatus" TEXT DEFAULT 'PENDING',
  "extractedText" TEXT,
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "Attachment" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "fileId" UUID NOT NULL REFERENCES "FileObject"("id") ON DELETE CASCADE,
  "entityType" TEXT NOT NULL,
  "entityId" UUID NOT NULL,
  "label" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 8. AI & ACTIVITY (Guardrail Audit)
-- ---------------------------------------------------------
CREATE TABLE "ActivityEvent" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "userId" UUID REFERENCES "User"("id") ON DELETE SET NULL,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE CASCADE,
  "action" TEXT NOT NULL,
  "entityType" TEXT NOT NULL,
  "entityId" UUID NOT NULL,
  "summary" TEXT,
  "changedColumns" TEXT[],
  "dataJson" JSONB,
  "ipAddress" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "AiInteraction" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "model" TEXT NOT NULL,
  "prompt" TEXT NOT NULL,
  "response" TEXT,
  "tokenCount" INTEGER,
  "containsSensitiveData" BOOLEAN NOT NULL DEFAULT false,
  "retentionExpiresAt" TIMESTAMPTZ,
  "relatedEntityType" TEXT,
  "relatedEntityId" UUID,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 9. EVENTS, TASKS & REMINDERS (Enum Centric)
-- ---------------------------------------------------------
CREATE TABLE "UniversalEvent" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE CASCADE,
  "type" "UniversalEventType" NOT NULL DEFAULT 'TASK',
  "title" TEXT NOT NULL,
  "descriptionHtml" TEXT,
  "startAt" TIMESTAMPTZ NOT NULL,
  "endAt" TIMESTAMPTZ,
  "status" "UniversalEventStatus" NOT NULL DEFAULT 'PENDING',
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "recordStatus" "RecordStatus" NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE "EventReminder" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "eventId" UUID NOT NULL REFERENCES "UniversalEvent"("id") ON DELETE CASCADE,
  "remindAt" TIMESTAMPTZ NOT NULL,
  "isSent" BOOLEAN DEFAULT false,
  "sentAt" TIMESTAMPTZ
);

-- ---------------------------------------------------------
-- 10. FINANCE SYSTEM
-- ---------------------------------------------------------
CREATE TABLE "Invoice" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "partyId" UUID NOT NULL REFERENCES "Party"("id") ON DELETE RESTRICT,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE SET NULL,
  "invoiceNumber" TEXT NOT NULL UNIQUE,
  "amount" DECIMAL(19,4) NOT NULL,
  "taxRate" DECIMAL(5,2) NOT NULL DEFAULT 20.00,
  "currency" TEXT NOT NULL DEFAULT 'TRY',
  "status" "InvoiceStatus" NOT NULL DEFAULT 'DRAFT',
  "dueAt" TIMESTAMPTZ,
  "paidAt" TIMESTAMPTZ,
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "updatedByUserId" UUID REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "MatterPayment" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "matterId" UUID NOT NULL REFERENCES "Matter"("id") ON DELETE CASCADE,
  "invoiceId" UUID REFERENCES "Invoice"("id") ON DELETE SET NULL,
  "amount" DECIMAL(19,4) NOT NULL,
  "currency" TEXT NOT NULL DEFAULT 'TRY',
  "paidAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "method" "PaymentMethod" NOT NULL DEFAULT 'BANK_TRANSFER',
  "note" TEXT,
  "receiptFileId" UUID REFERENCES "FileObject"("id") ON DELETE SET NULL,
  "recordedByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deletedAt" TIMESTAMPTZ
);

CREATE TABLE "Expense" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "matterId" UUID NOT NULL REFERENCES "Matter"("id") ON DELETE CASCADE,
  "amount" DECIMAL(19,4) NOT NULL,
  "currency" TEXT NOT NULL DEFAULT 'TRY',
  "category" TEXT,
  "incurredAt" DATE NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "TimeEntry" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE CASCADE,
  "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "type" TEXT NOT NULL DEFAULT 'BILLABLE',
  "description" TEXT,
  "minutes" INTEGER NOT NULL,
  "hourlyRate" DECIMAL(19,4),
  "workedAt" DATE NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deletedAt" TIMESTAMPTZ
);

-- ---------------------------------------------------------
-- 11. INDEXES (Architect Optimized)
-- ---------------------------------------------------------
CREATE INDEX "Matter_fts_idx" ON "Matter" USING GIN ("fts");
CREATE INDEX "Party_fts_idx" ON "Party" USING GIN ("fts");
CREATE INDEX "Matter_org_active_idx" ON "Matter"("orgId", "createdAt" DESC) WHERE "recordStatus" = 'ACTIVE';
CREATE INDEX "Activity_matter_created_idx" ON "ActivityEvent"("matterId", "createdAt" DESC);
CREATE INDEX "Attachment_entity_idx" ON "Attachment"("entityType", "entityId");
CREATE INDEX "Invoice_number_idx" ON "Invoice"("invoiceNumber");

-- ---------------------------------------------------------
-- 12. TRIGGERS (Automated Persistence)
-- ---------------------------------------------------------
CREATE TRIGGER "update_org_updated_at" BEFORE UPDATE ON "Org" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_user_updated_at" BEFORE UPDATE ON "User" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_party_updated_at" BEFORE UPDATE ON "Party" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_matter_updated_at" BEFORE UPDATE ON "Matter" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_invoice_updated_at" BEFORE UPDATE ON "Invoice" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_payment_updated_at" BEFORE UPDATE ON "MatterPayment" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
