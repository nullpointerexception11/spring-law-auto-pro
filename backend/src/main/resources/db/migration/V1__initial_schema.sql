-- ==========================================
-- PRESTIGE PLATINUM v8.2 DATABASE SCHEMA (REVISION 3)
-- Optimized for Spring Boot & TanStack Frontend
-- ==========================================

-- 1. DOMAIN-CRITICAL ENUMS
CREATE TYPE "UserStatus" AS ENUM ('PENDING', 'ACTIVE', 'INACTIVE', 'INVITED', 'ANONYMIZED');
CREATE TYPE "RoleKey" AS ENUM ('PLATFORM_ADMIN', 'ORG_ADMIN', 'LAWYER', 'STAFF', 'CLIENT');
CREATE TYPE "MatterStatus" AS ENUM ('ACTIVE', 'CLOSED', 'ARCHIVED', 'SUSPENDED');
CREATE TYPE "UniversalEventType" AS ENUM ('HEARING', 'DEADLINE', 'MEETING', 'TASK', 'REMINDER');
CREATE TYPE "UniversalEventStatus" AS ENUM ('PENDING', 'COMPLETED', 'CANCELED', 'MISSED');
CREATE TYPE "FeeType" AS ENUM ('BASE_ONLY', 'SUCCESS_ONLY', 'HYBRID');
CREATE TYPE "CorrDirection" AS ENUM ('INCOMING', 'OUTGOING');
CREATE TYPE "NotificationChannel" AS ENUM ('IN_APP', 'EMAIL', 'PUSH');
CREATE TYPE "NotificationStatus" AS ENUM ('PENDING', 'SENT', 'READ', 'FAILED');
CREATE TYPE "SignatureStatus" AS ENUM ('REQUESTED', 'SIGNED', 'EXPIRED', 'REJECTED');
CREATE TYPE "DeleteMode" AS ENUM ('SOFT', 'HARD');
CREATE TYPE "DeleteStatus" AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'EXECUTED');

-- 2. CORE TABLES
CREATE TABLE "Org" (
  "id" UUID PRIMARY KEY,
  "name" TEXT NOT NULL UNIQUE,
  "displayName" TEXT,
  "plan" TEXT DEFAULT 'FREE',
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "User" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID REFERENCES "Org"("id"),
  "email" TEXT NOT NULL,
  "fullName" TEXT NOT NULL,
  "passwordHash" TEXT NOT NULL,
  "status" "UserStatus" DEFAULT 'PENDING',
  "timezone" TEXT DEFAULT 'Europe/Istanbul',
  "locale" TEXT DEFAULT 'tr-TR',
  "notificationSettings" JSONB,
  "lastLoginAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE("orgId", "email")
);

CREATE TABLE "Role" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID REFERENCES "Org"("id"),
  "roleKey" "RoleKey" NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE("orgId", "roleKey")
);

CREATE TABLE "UserRole" (
  "userId" UUID NOT NULL REFERENCES "User"("id"),
  "roleId" UUID NOT NULL REFERENCES "Role"("id"),
  PRIMARY KEY ("userId", "roleId")
);

CREATE TABLE "FileObject" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "storageProvider" TEXT DEFAULT 'LOCAL',
  "storageKey" TEXT NOT NULL,
  "fileName" TEXT NOT NULL,
  "mimeType" TEXT,
  "sizeBytes" BIGINT,
  "sha256" TEXT,
  "ocrStatus" TEXT DEFAULT 'PENDING',
  "extractedText" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. BUSINESS-CONFIGURABLE TAXONOMY
CREATE TABLE "MatterPartyRole" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "name" TEXT NOT NULL,
  "category" TEXT,
  "createdAt" TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE("orgId", "name")
);

-- 4. MATTER & PARTIES
CREATE TABLE "Matter" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "title" TEXT NOT NULL,
  "referenceNumber" TEXT,
  "status" "MatterStatus" DEFAULT 'ACTIVE',
  "summary" TEXT,
  "description" TEXT,
  "tags" TEXT[],
  "openedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "closedAt" TIMESTAMPTZ,
  "createdByUserId" UUID REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "Party" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "fullName" TEXT NOT NULL,
  "email" TEXT,
  "phone" TEXT,
  "taxNumber" TEXT,
  "address" TEXT,
  "notes" TEXT,
  "createdByUserId" UUID REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "MatterParty" (
  "matterId" UUID NOT NULL REFERENCES "Matter"("id"),
  "partyId" UUID NOT NULL REFERENCES "Party"("id"),
  "roleId" UUID NOT NULL REFERENCES "MatterPartyRole"("id"),
  PRIMARY KEY ("matterId", "partyId", "roleId")
);

-- 5. LITIGATION DETAILS
CREATE TABLE "LitigationDetail" (
  "matterId" UUID PRIMARY KEY REFERENCES "Matter"("id") ON DELETE CASCADE,
  "courtName" TEXT,
  "caseNumber" TEXT,
  "judgeName" TEXT,
  "clerkName" TEXT,
  "decisionSummary" TEXT,
  "decisionDate" DATE,
  "appealDeadline" DATE,
  "appealFiled" BOOLEAN DEFAULT false,
  "finalJudgmentDate" DATE
);

-- 6. FEE AGREEMENTS
CREATE TABLE "FeeAgreement" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID NOT NULL UNIQUE REFERENCES "Matter"("id"),
  "feeType" "FeeType" NOT NULL,
  "baseAmount" DECIMAL(19,4),
  "successPercentage" DECIMAL(5,2),
  "currency" TEXT DEFAULT 'TRY',
  "signedAt" TIMESTAMPTZ,
  "signedFileId" UUID REFERENCES "FileObject"("id"),
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 7. UNIVERSAL EVENTS
CREATE TABLE "UniversalEvent" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID REFERENCES "Matter"("id"),
  "type" "UniversalEventType" NOT NULL,
  "title" TEXT NOT NULL,
  "descriptionHtml" TEXT,
  "startAt" TIMESTAMPTZ NOT NULL,
  "endAt" TIMESTAMPTZ,
  "status" "UniversalEventStatus" DEFAULT 'PENDING',
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 8. ACTIVITY LOG
CREATE TABLE "ActivityEvent" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "userId" UUID NOT NULL REFERENCES "User"("id"),
  "matterId" UUID REFERENCES "Matter"("id"),
  "action" TEXT NOT NULL,
  "entityType" TEXT,
  "entityId" UUID,
  "summary" TEXT,
  "metadata" JSONB,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 9. SEARCH ENGINE
CREATE TABLE "SearchDocument" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id") ON DELETE CASCADE,
  "entityType" TEXT NOT NULL,
  "entityId" UUID NOT NULL,
  "title" TEXT NOT NULL,
  "body" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- INDEXES
CREATE INDEX "idx_matter_org_status" ON "Matter"("orgId", "status");
CREATE INDEX "idx_event_org_start" ON "UniversalEvent"("orgId", "startAt");
CREATE INDEX "idx_search_org_entity" ON "SearchDocument"("orgId", "entityType", "entityId");
