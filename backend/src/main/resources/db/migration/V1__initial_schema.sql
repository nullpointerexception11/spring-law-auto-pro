-- ==========================================
-- PRESTIGE PLATINUM v8.2 DATABASE SCHEMA (REVISION 2)
-- Architect: User & Antigravity
-- Target: PostgreSQL 16+
-- ==========================================

-- 1. DOMAIN-CRITICAL ENUMS (State & Core Status)
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
  "storageProvider" TEXT DEFAULT 'S3', -- 'S3', 'LOCAL', 'GCS'
  "storageKey" TEXT NOT NULL,
  "fileName" TEXT NOT NULL,
  "mimeType" TEXT,
  "sizeBytes" BIGINT,
  "sha256" TEXT,
  "ocrStatus" TEXT DEFAULT 'PENDING', -- 'PENDING', 'COMPLETED', 'FAILED', 'NOT_APPLICABLE'
  "extractedText" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. BUSINESS-CONFIGURABLE TAXONOMY (Reference Tables)
CREATE TABLE "MatterPartyRole" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "name" TEXT NOT NULL, -- 'PLAINTIFF', 'DEFENDANT', 'WITNESS', 'JUDGE'
  "category" TEXT, -- 'OPPOSING', 'CLIENT', 'COURT'
  "createdAt" TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE("orgId", "name")
);

CREATE TABLE "ExpenseCategory" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "name" TEXT NOT NULL, -- 'COURT_FEE', 'NOTIFICATION', 'EXPERT'
  "createdAt" TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE("orgId", "name")
);

CREATE TABLE "CorrespondenceType" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "name" TEXT NOT NULL, -- 'EMAIL', 'LETTER', 'FAX', 'PORTAL_MESSAGE'
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
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Party is pure Identity now (Concept Drift Fixed)
CREATE TABLE "Party" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "fullName" TEXT NOT NULL,
  "email" TEXT,
  "phone" TEXT,
  "taxNumber" TEXT,
  "address" TEXT,
  "notes" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Contextual Role Mapping
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

-- 6. ADVANCED HEARINGS
CREATE TABLE "Hearing" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID NOT NULL REFERENCES "Matter"("id"),
  "hearingDate" TIMESTAMPTZ NOT NULL,
  "judge" TEXT,
  "outcome" TEXT, -- 'ADJOURNED', 'DECISION_GIVEN', 'SETTLED'
  "minutesLink" UUID REFERENCES "FileObject"("id"),
  "notes" TEXT,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 7. FEE AGREEMENTS
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

-- 8. EXPENSES
CREATE TABLE "Expense" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID REFERENCES "Matter"("id"),
  "partyId" UUID REFERENCES "Party"("id"),
  "categoryId" UUID NOT NULL REFERENCES "ExpenseCategory"("id"),
  "amount" DECIMAL(19,4) NOT NULL,
  "currency" TEXT DEFAULT 'TRY',
  "expenseDate" DATE NOT NULL,
  "description" TEXT,
  "receiptFileId" UUID REFERENCES "FileObject"("id"),
  "createdByUserId" UUID REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 9. CORRESPONDENCE
CREATE TABLE "Correspondence" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID NOT NULL REFERENCES "Matter"("id"),
  "direction" "CorrDirection" NOT NULL,
  "typeId" UUID NOT NULL REFERENCES "CorrespondenceType"("id"),
  "referenceNo" TEXT,
  "subject" TEXT,
  "sentAt" TIMESTAMPTZ,
  "receivedAt" TIMESTAMPTZ,
  "summary" TEXT,
  "fileId" UUID REFERENCES "FileObject"("id"),
  "createdByUserId" UUID REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 10. INVITATIONS
CREATE TABLE "Invitation" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "email" TEXT NOT NULL,
  "roleKey" "RoleKey" NOT NULL,
  "token" TEXT UNIQUE NOT NULL,
  "expiresAt" TIMESTAMPTZ NOT NULL,
  "invitedByUserId" UUID NOT NULL REFERENCES "User"("id"),
  "acceptedAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 11. NOTIFICATIONS (Prod-Ready)
CREATE TABLE "Notification" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "userId" UUID NOT NULL REFERENCES "User"("id"),
  "channel" "NotificationChannel" NOT NULL,
  "title" TEXT NOT NULL,
  "body" TEXT,
  "entityType" TEXT,
  "entityId" UUID,
  "status" "NotificationStatus" DEFAULT 'PENDING',
  "retryCount" INTEGER DEFAULT 0,
  "lastError" TEXT,
  "nextRetryAt" TIMESTAMPTZ,
  "sentAt" TIMESTAMPTZ,
  "readAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 12. DOCUMENT TEMPLATES
CREATE TABLE "DocumentTemplate" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "name" TEXT NOT NULL,
  "version" INTEGER NOT NULL DEFAULT 1,
  "templateFileId" UUID NOT NULL REFERENCES "FileObject"("id"),
  "variablesJson" JSONB,
  "isActive" BOOLEAN DEFAULT true,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE "GeneratedDocument" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "templateId" UUID REFERENCES "DocumentTemplate"("id"),
  "matterId" UUID REFERENCES "Matter"("id"),
  "title" TEXT,
  "generatedFileId" UUID NOT NULL REFERENCES "FileObject"("id"),
  "createdByUserId" UUID REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 13. RETENTION
CREATE TABLE "RetentionPolicy" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "entityType" TEXT NOT NULL,
  "retentionDays" INTEGER NOT NULL,
  "action" TEXT NOT NULL,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 14. IMPERSONATION
CREATE TABLE "ImpersonationLog" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "adminUserId" UUID NOT NULL REFERENCES "User"("id"),
  "targetUserId" UUID NOT NULL REFERENCES "User"("id"),
  "startedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "endedAt" TIMESTAMPTZ,
  "reason" TEXT
);

-- 15. API KEYS
CREATE TABLE "ApiKey" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "name" TEXT NOT NULL,
  "keyHash" TEXT NOT NULL UNIQUE,
  "permissions" TEXT[],
  "lastUsedAt" TIMESTAMPTZ,
  "expiresAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- 16. SIGNATURE REQUESTS (E-Sign Workflow)
CREATE TABLE "SignatureRequest" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "entityType" TEXT NOT NULL,
  "entityId" UUID NOT NULL,
  "partyId" UUID NOT NULL REFERENCES "Party"("id"),
  "signOrder" INTEGER DEFAULT 1,
  "status" "SignatureStatus" NOT NULL DEFAULT 'REQUESTED',
  "requestedAt" TIMESTAMPTZ DEFAULT NOW(),
  "signedAt" TIMESTAMPTZ,
  "signedFileId" UUID REFERENCES "FileObject"("id"),
  "validUntil" TIMESTAMPTZ
);

-- 17. OPERATIONAL TOOLS
CREATE TABLE "UniversalEvent" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID REFERENCES "Matter"("id"),
  "type" "UniversalEventType" NOT NULL,
  "title" TEXT NOT NULL,
  "descriptionHtml" TEXT,
  "startAt" TIMESTAMPTZ NOT NULL,
  "endAt" TIMESTAMPTZ,
  "rrule" TEXT, -- iCal recurrence standard
  "status" "UniversalEventStatus" DEFAULT 'PENDING',
  "createdByUserId" UUID NOT NULL REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

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

-- 18. GENERIC DELETE QUEUE
CREATE TABLE "DeleteQueue" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "entityType" TEXT NOT NULL,
  "entityId" UUID NOT NULL,
  "mode" "DeleteMode" NOT NULL,
  "status" "DeleteStatus" DEFAULT 'PENDING',
  "reason" TEXT,
  "requestedByUserId" UUID NOT NULL REFERENCES "User"("id"),
  "reviewedByUserId" UUID REFERENCES "User"("id"),
  "executedByUserId" UUID REFERENCES "User"("id"),
  "requestedAt" TIMESTAMPTZ DEFAULT NOW(),
  "reviewedAt" TIMESTAMPTZ,
  "executedAt" TIMESTAMPTZ
);

-- 19. SEARCH ENGINE
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

-- INDEXES FOR PERFORMANCE & SEARCH
CREATE INDEX "idx_matter_org_status" ON "Matter"("orgId", "status");
CREATE INDEX "idx_event_org_start" ON "UniversalEvent"("orgId", "startAt");
CREATE INDEX "idx_notification_retry" ON "Notification"("status", "nextRetryAt");
CREATE INDEX "idx_deletequeue_status" ON "DeleteQueue"("orgId", "status");
