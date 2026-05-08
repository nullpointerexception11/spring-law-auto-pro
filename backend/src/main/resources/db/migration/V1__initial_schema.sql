-- LawAuto Sovereign LMMS Master Schema (V1 - PRESTIGE EDITION v8 GOLDEN)
-- The Absolute Final Baseline for Enterprise Legal Technology
-- Implements Decision Tracking, Official Correspondence, Folder Hierarchy, and KVKK

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------------------------------------------------
-- 1. ENUMS (Final Domain Language)
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

-- ---------------------------------------------------------
-- 2. CORE INFRASTRUCTURE
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
  "email" TEXT NOT NULL UNIQUE,
  "fullName" TEXT NOT NULL,
  "passwordHash" TEXT,
  "status" "UserStatus" NOT NULL DEFAULT 'PENDING_SETUP',
  "consentGivenAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "UserOrg" (
  "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE RESTRICT,
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "isOwner" BOOLEAN NOT NULL DEFAULT false,
  "joinedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("userId", "orgId")
);

-- ---------------------------------------------------------
-- 3. THE PARTY SYSTEM (KVKK Ready)
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
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 4. THE MATTER SYSTEM (Deep Decision Tracking)
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
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
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
  "fileId" UUID, -- Link to FileObject via Attachment
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
-- 5. STORAGE & HIERARCHY
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
-- 6. AI & ACTIVITY (Advanced Audit)
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
  "changedColumns" TEXT[], -- Precision Audit
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
  "relatedEntityType" TEXT,
  "relatedEntityId" UUID,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 7. EVENTS, TASKS & REMINDERS
-- ---------------------------------------------------------
CREATE TABLE "UniversalEvent" (
  "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE RESTRICT,
  "matterId" UUID REFERENCES "Matter"("id") ON DELETE CASCADE,
  "type" TEXT NOT NULL,
  "title" TEXT NOT NULL,
  "descriptionHtml" TEXT,
  "startAt" TIMESTAMPTZ NOT NULL,
  "endAt" TIMESTAMPTZ,
  "status" TEXT NOT NULL DEFAULT 'PENDING',
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
-- 8. INDEXES (ORACLE GOLDEN OPTIMIZATION)
-- ---------------------------------------------------------
CREATE INDEX "Matter_org_active_idx" ON "Matter"("orgId", "createdAt" DESC) WHERE "recordStatus" = 'ACTIVE';
CREATE INDEX "Activity_matter_created_idx" ON "ActivityEvent"("matterId", "createdAt" DESC);
CREATE INDEX "Attachment_entity_idx" ON "Attachment"("entityType", "entityId");
CREATE INDEX "FileObject_folder_idx" ON "FileObject"("folderId");
CREATE INDEX "Decision_matter_idx" ON "MatterDecision"("matterId", "decisionDate" DESC);
CREATE INDEX "Corr_matter_idx" ON "Correspondence"("matterId", "date" DESC);
CREATE INDEX "Party_org_tax_idx" ON "Party"("orgId", "taxNo");
