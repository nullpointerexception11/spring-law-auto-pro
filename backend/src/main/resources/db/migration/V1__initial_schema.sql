-- LawAuto Sovereign LMMS Master Schema (V1 - PRESTIGE PLATINUM v8.2 FINAL)
-- Optimized for Weighted Search, RLS Isolation, Document Versioning, and Domain Events

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------------------------------------------------
-- 1. ENUMS (Platinum Domain Language)
-- ---------------------------------------------------------
CREATE TYPE "RecordStatus" AS ENUM ('ACTIVE', 'ARCHIVED', 'LOCKED', 'DELETED', 'ANONYMIZED');
CREATE TYPE "UserStatus" AS ENUM ('ACTIVE', 'INVITED', 'SUSPENDED', 'DISABLED', 'PENDING_SETUP');
CREATE TYPE "RoleKey" AS ENUM ('PLATFORM_OWNER', 'PLATFORM_ADMIN', 'ORG_ADMIN', 'LAWYER', 'SECRETARY');
CREATE TYPE "MatterType" AS ENUM ('LITIGATION', 'CONSULTATION', 'EXECUTION', 'MEDIATION', 'ADVISORY');
CREATE TYPE "MatterStatus" AS ENUM ('OPEN', 'CLOSED', 'ON_HOLD', 'ARCHIVED');
CREATE TYPE "PartyType" AS ENUM ('PERSON', 'COMPANY', 'GOVERNMENT_BODY');
CREATE TYPE "PartyRole" AS ENUM ('CLIENT', 'OPPONENT', 'WITNESS', 'EXPERT', 'JUDGE', 'INSURANCE_COMPANY', 'OTHER');
CREATE TYPE "DecisionType" AS ENUM ('INTERIM', 'FINAL', 'APPEAL', 'SUPREME_COURT');
CREATE TYPE "CorrDirection" AS ENUM ('INCOMING', 'OUTGOING');
CREATE TYPE "CorrType" AS ENUM ('NOTIFICATION', 'EXPERT_REPORT', 'COURT_ORDER', 'PETITION_REPLY');
CREATE TYPE "UniversalEventType" AS ENUM ('HEARING', 'DEADLINE', 'MEETING', 'TASK', 'REMINDER');
CREATE TYPE "UniversalEventStatus" AS ENUM ('PENDING', 'COMPLETED', 'CANCELLED', 'MISSED');
CREATE TYPE "InvoiceStatus" AS ENUM ('DRAFT', 'ISSUED', 'PAID', 'OVERDUE', 'CANCELLED');
CREATE TYPE "PaymentMethod" AS ENUM ('CASH', 'BANK_TRANSFER', 'CREDIT_CARD');
CREATE TYPE "TimeEntryType" AS ENUM ('BILLABLE', 'NON_BILLABLE', 'ADMIN', 'RESEARCH');
CREATE TYPE "JobStatus" AS ENUM ('QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED');

-- ---------------------------------------------------------
-- 2. AUTOMATION: Trigger Function
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
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 4. AUTHORIZATION (RBAC + UserRole Junction)
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

CREATE TABLE "UserRole" (
  "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE CASCADE,
  "roleId" UUID NOT NULL REFERENCES "Role"("id") ON DELETE CASCADE,
  PRIMARY KEY ("userId", "roleId")
);

-- ---------------------------------------------------------
-- 5. THE PARTY SYSTEM (Weighted Search)
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
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "fts" tsvector GENERATED ALWAYS AS (
    setweight(to_tsvector('turkish', coalesce("fullName", '')), 'A') ||
    setweight(to_tsvector('turkish', coalesce("taxNo", '')), 'B')
  ) STORED
);

-- ---------------------------------------------------------
-- 6. THE MATTER SYSTEM (Weighted Search + Lifecycle)
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
  "recordStatus" "RecordStatus" NOT NULL DEFAULT 'ACTIVE',
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "fts" tsvector GENERATED ALWAYS AS (
    setweight(to_tsvector('turkish', coalesce("title", '')), 'A') ||
    setweight(to_tsvector('turkish', coalesce("referenceNumber", '')), 'B')
  ) STORED
);

CREATE TABLE "MatterParty" (
  "matterId" UUID NOT NULL REFERENCES "Matter"("id") ON DELETE CASCADE,
  "partyId" UUID NOT NULL REFERENCES "Party"("id") ON DELETE RESTRICT,
  "role" "PartyRole" NOT NULL DEFAULT 'CLIENT',
  "isPrimary" BOOLEAN NOT NULL DEFAULT false,
  PRIMARY KEY ("matterId", "partyId")
);

-- ---------------------------------------------------------
-- 7. DOCUMENT SYSTEM (Versioning + RLS Ready)
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
  "parentFileId" UUID REFERENCES "FileObject"("id"), -- Versioning link
  "versionNo" INTEGER NOT NULL DEFAULT 1,
  "isLatest" BOOLEAN NOT NULL DEFAULT true,
  "storageProvider" TEXT NOT NULL DEFAULT 'LOCAL',
  "bucket" TEXT,
  "storageKey" TEXT NOT NULL,
  "fileName" TEXT NOT NULL,
  "mimeType" TEXT,
  "sizeBytes" BIGINT,
  "sha256" TEXT,
  "ocrStatus" "JobStatus" NOT NULL DEFAULT 'QUEUED',
  "extractedText" TEXT,
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "Attachment" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT, -- Critical for RLS
  "fileId" UUID NOT NULL REFERENCES "FileObject"("id") ON DELETE CASCADE,
  "entityType" TEXT NOT NULL,
  "entityId" UUID NOT NULL,
  "label" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 8. INTELLIGENCE & AUDIT (Guardrails + Redaction)
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
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP -- Append-only
);

CREATE TABLE "AiInteraction" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "model" TEXT NOT NULL,
  "prompt" TEXT NOT NULL,
  "response" TEXT,
  "isRedacted" BOOLEAN NOT NULL DEFAULT false,
  "redactedAt" TIMESTAMPTZ,
  "containsSensitiveData" BOOLEAN NOT NULL DEFAULT false,
  "retentionExpiresAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 9. WORKFLOW: EVENTS & ASSIGNEES
-- ---------------------------------------------------------
CREATE TABLE "UniversalEvent" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE CASCADE,
  "type" "UniversalEventType" NOT NULL DEFAULT 'TASK',
  "title" TEXT NOT NULL,
  "startAt" TIMESTAMPTZ NOT NULL,
  "endAt" TIMESTAMPTZ,
  "status" "UniversalEventStatus" NOT NULL DEFAULT 'PENDING',
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "EventAssignee" (
  "eventId" UUID NOT NULL REFERENCES "UniversalEvent"("id") ON DELETE CASCADE,
  "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE CASCADE,
  PRIMARY KEY ("eventId", "userId")
);

-- ---------------------------------------------------------
-- 10. FINANCIAL SYSTEM (Ledger Ready)
-- ---------------------------------------------------------
CREATE TABLE "Invoice" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "partyId" UUID NOT NULL REFERENCES "Party"("id") ON DELETE RESTRICT,
  "invoiceNumber" TEXT NOT NULL UNIQUE,
  "amount" DECIMAL(19,4) NOT NULL,
  "status" "InvoiceStatus" NOT NULL DEFAULT 'DRAFT',
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "TimeEntry" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE CASCADE,
  "type" "TimeEntryType" NOT NULL DEFAULT 'BILLABLE',
  "minutes" INTEGER NOT NULL,
  "workedAt" DATE NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 11. INTEGRATION & JOBS
-- ---------------------------------------------------------
CREATE TABLE "DomainEvent" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "eventType" TEXT NOT NULL, -- e.g., 'MatterCreated'
  "payload" JSONB,
  "occurredAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "JobQueue" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "jobType" TEXT NOT NULL, -- 'OCR', 'AI_SUMMARY'
  "payload" JSONB,
  "status" "JobStatus" NOT NULL DEFAULT 'QUEUED',
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 12. INDEXES
-- ---------------------------------------------------------
CREATE INDEX "Matter_fts_idx" ON "Matter" USING GIN ("fts");
CREATE INDEX "Party_fts_idx" ON "Party" USING GIN ("fts");
CREATE INDEX "Activity_org_matter_idx" ON "ActivityEvent"("orgId", "matterId", "createdAt" DESC);
CREATE INDEX "Attachment_org_entity_idx" ON "Attachment"("orgId", "entityType", "entityId");

-- ---------------------------------------------------------
-- 13. TRIGGERS
-- ---------------------------------------------------------
CREATE TRIGGER "update_org_updated_at" BEFORE UPDATE ON "Org" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_user_updated_at" BEFORE UPDATE ON "User" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_matter_updated_at" BEFORE UPDATE ON "Matter" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER "update_invoice_updated_at" BEFORE UPDATE ON "Invoice" FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
