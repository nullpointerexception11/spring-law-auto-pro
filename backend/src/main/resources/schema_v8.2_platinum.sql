-- ==========================================
-- PRESTIGE PLATINUM v8.2 DATABASE SCHEMA
-- Target: PostgreSQL 16+
-- ==========================================

-- ENUMS
CREATE TYPE "UserStatus" AS ENUM ('PENDING', 'ACTIVE', 'INACTIVE', 'INVITED', 'ANONYMIZED');
CREATE TYPE "RoleKey" AS ENUM ('PLATFORM_ADMIN', 'ORG_ADMIN', 'LAWYER', 'STAFF', 'CLIENT');
CREATE TYPE "MatterStatus" AS ENUM ('ACTIVE', 'CLOSED', 'ARCHIVED', 'SUSPENDED');
CREATE TYPE "PartyType" AS ENUM ('CLIENT', 'OPPONENT', 'JUDGE', 'WITNESS', 'EXPERT', 'COURT_STAFF');
CREATE TYPE "UniversalEventType" AS ENUM ('HEARING', 'DEADLINE', 'MEETING', 'TASK', 'REMINDER');
CREATE TYPE "UniversalEventStatus" AS ENUM ('PENDING', 'COMPLETED', 'CANCELED', 'MISSED');
CREATE TYPE "FeeType" AS ENUM ('BASE_ONLY', 'SUCCESS_ONLY', 'HYBRID');
CREATE TYPE "ExpenseType" AS ENUM ('COURT_FEE', 'NOTIFICATION', 'EXPERT', 'TRAVEL', 'OTHER');
CREATE TYPE "CorrDirection" AS ENUM ('INCOMING', 'OUTGOING');
CREATE TYPE "CorrType" AS ENUM ('EMAIL', 'LETTER', 'FAX', 'PORTAL_MESSAGE', 'INTERNAL');
CREATE TYPE "NotificationChannel" AS ENUM ('IN_APP', 'EMAIL', 'PUSH');
CREATE TYPE "NotificationStatus" AS ENUM ('PENDING', 'SENT', 'READ', 'FAILED');
CREATE TYPE "SignatureStatus" AS ENUM ('REQUESTED', 'SIGNED', 'EXPIRED', 'REJECTED');
CREATE TYPE "DeleteEntityType" AS ENUM ('PARTY', 'MATTER', 'PETITION', 'EVIDENCE', 'HEARING', 'DEADLINE', 'PAYMENT', 'CALENDAR_EVENT', 'NOTE', 'FILE_OBJECT');
CREATE TYPE "DeleteMode" AS ENUM ('SOFT', 'HARD');

-- CORE TABLES
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
  "storageKey" TEXT NOT NULL,
  "fileName" TEXT NOT NULL,
  "mimeType" TEXT,
  "sizeBytes" BIGINT,
  "sha256" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- MATTER & PARTIES
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

CREATE TABLE "Party" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "type" "PartyType" NOT NULL,
  "fullName" TEXT NOT NULL,
  "email" TEXT,
  "phone" TEXT,
  "taxNumber" TEXT,
  "address" TEXT,
  "notes" TEXT,
  "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "MatterParty" (
  "matterId" UUID NOT NULL REFERENCES "Matter"("id"),
  "partyId" UUID NOT NULL REFERENCES "Party"("id"),
  "roleInMatter" TEXT, -- e.g. 'PLAINTIFF', 'DEFENDANT'
  PRIMARY KEY ("matterId", "partyId")
);

-- LITIGATION DETAILS
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

-- ADVANCED HEARINGS
CREATE TABLE "Hearing" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID NOT NULL REFERENCES "Matter"("id"),
  "hearingDate" TIMESTAMPTZ NOT NULL,
  "judge" TEXT,
  "outcome" TEXT,
  "minutesLink" UUID REFERENCES "FileObject"("id"),
  "notes" TEXT,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- FEE AGREEMENTS
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

-- EXPENSES
CREATE TABLE "Expense" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID REFERENCES "Matter"("id"),
  "partyId" UUID REFERENCES "Party"("id"),
  "type" "ExpenseType" NOT NULL,
  "amount" DECIMAL(19,4) NOT NULL,
  "currency" TEXT DEFAULT 'TRY',
  "expenseDate" DATE NOT NULL,
  "description" TEXT,
  "receiptFileId" UUID REFERENCES "FileObject"("id"),
  "createdByUserId" UUID REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- CORRESPONDENCE
CREATE TABLE "Correspondence" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "matterId" UUID NOT NULL REFERENCES "Matter"("id"),
  "direction" "CorrDirection" NOT NULL,
  "type" "CorrType" NOT NULL,
  "referenceNo" TEXT,
  "subject" TEXT,
  "sentAt" TIMESTAMPTZ,
  "receivedAt" TIMESTAMPTZ,
  "summary" TEXT,
  "fileId" UUID REFERENCES "FileObject"("id"),
  "createdByUserId" UUID REFERENCES "User"("id"),
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- INVITATIONS
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

-- NOTIFICATIONS
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
  "sentAt" TIMESTAMPTZ,
  "readAt" TIMESTAMPTZ,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- DOCUMENT TEMPLATES
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

-- RETENTION
CREATE TABLE "RetentionPolicy" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "entityType" TEXT NOT NULL,
  "retentionDays" INTEGER NOT NULL,
  "action" TEXT NOT NULL,
  "createdAt" TIMESTAMPTZ DEFAULT NOW()
);

-- IMPERSONATION
CREATE TABLE "ImpersonationLog" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "adminUserId" UUID NOT NULL REFERENCES "User"("id"),
  "targetUserId" UUID NOT NULL REFERENCES "User"("id"),
  "startedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "endedAt" TIMESTAMPTZ,
  "reason" TEXT
);

-- API KEYS
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

-- SIGNATURE REQUESTS
CREATE TABLE "SignatureRequest" (
  "id" UUID PRIMARY KEY,
  "orgId" UUID NOT NULL REFERENCES "Org"("id"),
  "entityType" TEXT NOT NULL,
  "entityId" UUID NOT NULL,
  "partyId" UUID NOT NULL REFERENCES "Party"("id"),
  "status" "SignatureStatus" NOT NULL DEFAULT 'REQUESTED',
  "requestedAt" TIMESTAMPTZ DEFAULT NOW(),
  "signedAt" TIMESTAMPTZ,
  "signedFileId" UUID REFERENCES "FileObject"("id"),
  "validUntil" TIMESTAMPTZ
);

-- OPERATIONAL TOOLS
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

-- INDEXES FOR PERFORMANCE & SEARCH
CREATE INDEX "idx_matter_org_status" ON "Matter"("orgId", "status");
CREATE INDEX "idx_party_org_type" ON "Party"("orgId", "type");
CREATE INDEX "idx_event_org_start" ON "UniversalEvent"("orgId", "startAt");
CREATE INDEX "idx_notification_user_status" ON "Notification"("userId", "status");
