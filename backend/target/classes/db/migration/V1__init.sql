-- Init schema for hukuk otomasyon (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Enums
CREATE TYPE "RoleKey" AS ENUM ('ADMIN', 'LAWYER', 'SECRETARY');
CREATE TYPE "CaseStatus" AS ENUM ('OPEN', 'CLOSED', 'ARCHIVED');
CREATE TYPE "FeeModel" AS ENUM ('BASE_ONLY', 'SUCCESS_ONLY', 'BOTH');
CREATE TYPE "PaymentMethod" AS ENUM ('CASH', 'BANK_TRANSFER', 'CREDIT_CARD', 'OTHER');
CREATE TYPE "DeleteEntityType" AS ENUM (
  'CLIENT',
  'CASE',
  'PETITION',
  'EVIDENCE',
  'HEARING',
  'DEADLINE',
  'CASE_PAYMENT',
  'CALENDAR_EVENT',
  'CLIENT_NOTE',
  'FILE_OBJECT'
);
CREATE TYPE "DeleteRequestStatus" AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'EXECUTED');
CREATE TYPE "DeleteMode" AS ENUM ('SOFT', 'HARD');

-- Core tenant
CREATE TABLE "Org" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "name" TEXT NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "Org_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "OrgSettings" (
  "orgId" UUID NOT NULL,
  "requireSecretary" BOOLEAN NOT NULL DEFAULT false,
  "secretaryMode" TEXT NOT NULL DEFAULT 'BASIC',
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "OrgSettings_pkey" PRIMARY KEY ("orgId")
);

CREATE TABLE "User" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "email" TEXT NOT NULL,
  "fullName" TEXT NOT NULL,
  "passwordHash" TEXT NOT NULL,
  "status" TEXT NOT NULL DEFAULT 'ACTIVE',
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "Role" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "key" "RoleKey" NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "Role_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "UserRole" (
  "userId" UUID NOT NULL,
  "roleId" UUID NOT NULL,
  CONSTRAINT "UserRole_pkey" PRIMARY KEY ("userId","roleId")
);

CREATE TABLE "LawyerProfile" (
  "userId" UUID NOT NULL,
  "orgId" UUID NOT NULL,
  "adminUserId" UUID NOT NULL,
  "baroNo" TEXT,
  CONSTRAINT "LawyerProfile_pkey" PRIMARY KEY ("userId")
);

CREATE TABLE "SecretaryProfile" (
  "userId" UUID NOT NULL,
  "orgId" UUID NOT NULL,
  CONSTRAINT "SecretaryProfile_pkey" PRIMARY KEY ("userId")
);

CREATE TABLE "SecretaryLawyer" (
  "orgId" UUID NOT NULL,
  "secretaryUserId" UUID NOT NULL,
  "lawyerUserId" UUID NOT NULL,
  "assignedByUserId" UUID NOT NULL,
  "assignedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "endedAt" TIMESTAMP(3),
  CONSTRAINT "SecretaryLawyer_pkey" PRIMARY KEY ("secretaryUserId","lawyerUserId","assignedAt")
);

CREATE TABLE "Client" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "type" TEXT NOT NULL DEFAULT 'PERSON',
  "fullName" TEXT NOT NULL,
  "phone" TEXT,
  "email" TEXT,
  "address" TEXT,
  "createdByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  "deletedAt" TIMESTAMP(3),
  CONSTRAINT "Client_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "ClientLawyer" (
  "orgId" UUID NOT NULL,
  "clientId" UUID NOT NULL,
  "lawyerUserId" UUID NOT NULL,
  "assignedByUserId" UUID NOT NULL,
  "assignedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "endedAt" TIMESTAMP(3),
  CONSTRAINT "ClientLawyer_pkey" PRIMARY KEY ("clientId","lawyerUserId","assignedAt")
);

CREATE TABLE "Case" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "clientId" UUID NOT NULL,
  "title" TEXT NOT NULL,
  "status" "CaseStatus" NOT NULL DEFAULT 'OPEN',
  "createdByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "openedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "closedAt" TIMESTAMP(3),
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  "deletedAt" TIMESTAMP(3),
  CONSTRAINT "Case_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "CaseLawyer" (
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "lawyerUserId" UUID NOT NULL,
  "assignedByUserId" UUID NOT NULL,
  "assignedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "endedAt" TIMESTAMP(3),
  CONSTRAINT "CaseLawyer_pkey" PRIMARY KEY ("caseId","lawyerUserId","assignedAt")
);

CREATE TABLE "FileObject" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "storageKey" TEXT NOT NULL,
  "fileName" TEXT NOT NULL,
  "mimeType" TEXT,
  "sizeBytes" INTEGER,
  "sha256" TEXT,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "FileObject_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "Petition" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "title" TEXT NOT NULL,
  "body" TEXT,
  "fileId" UUID,
  "createdByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  "deletedAt" TIMESTAMP(3),
  CONSTRAINT "Petition_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "Evidence" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "description" TEXT,
  "fileId" UUID NOT NULL,
  "createdByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deletedAt" TIMESTAMP(3),
  CONSTRAINT "Evidence_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "ClientNote" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "clientId" UUID NOT NULL,
  "body" TEXT NOT NULL,
  "visibility" TEXT NOT NULL DEFAULT 'LAWYERS',
  "createdByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deletedAt" TIMESTAMP(3),
  CONSTRAINT "ClientNote_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "Hearing" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "hearingAt" TIMESTAMP(3) NOT NULL,
  "court" TEXT,
  "notes" TEXT,
  "result" TEXT,
  "createdByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  "deletedAt" TIMESTAMP(3),
  CONSTRAINT "Hearing_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "Deadline" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "type" TEXT NOT NULL,
  "dueAt" TIMESTAMP(3) NOT NULL,
  "remindAt" TIMESTAMP(3),
  "status" TEXT NOT NULL DEFAULT 'OPEN',
  "notes" TEXT,
  "createdByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  "deletedAt" TIMESTAMP(3),
  CONSTRAINT "Deadline_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "CalendarEvent" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "ownerUserId" UUID NOT NULL,
  "startsAt" TIMESTAMP(3) NOT NULL,
  "endsAt" TIMESTAMP(3),
  "title" TEXT NOT NULL,
  "body" TEXT,
  "remindAt" TIMESTAMP(3),
  "relatedCaseId" UUID,
  "relatedClientId" UUID,
  "deletedByUserId" UUID,
  "deletedAt" TIMESTAMP(3),
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "CalendarEvent_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "CaseFeeTerms" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "model" "FeeModel" NOT NULL,
  "baseFeeAmount" DECIMAL(12,2),
  "successFeePercent" DECIMAL(5,2),
  "currency" TEXT NOT NULL DEFAULT 'TRY',
  "notes" TEXT,
  "createdByUserId" UUID NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "CaseFeeTerms_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "CasePayment" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "amount" DECIMAL(12,2) NOT NULL,
  "currency" TEXT NOT NULL DEFAULT 'TRY',
  "paidAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "method" "PaymentMethod" NOT NULL DEFAULT 'BANK_TRANSFER',
  "note" TEXT,
  "receiptFileId" UUID,
  "recordedByUserId" UUID NOT NULL,
  "deletedByUserId" UUID,
  "deletedAt" TIMESTAMP(3),
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "CasePayment_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "DeleteRequest" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "entityType" "DeleteEntityType" NOT NULL,
  "entityId" UUID NOT NULL,
  "mode" "DeleteMode" NOT NULL DEFAULT 'SOFT',
  "status" "DeleteRequestStatus" NOT NULL DEFAULT 'PENDING',
  "reason" TEXT,
  "requestedByUserId" UUID NOT NULL,
  "requestedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "reviewedByUserId" UUID,
  "reviewedAt" TIMESTAMP(3),
  "executedByUserId" UUID,
  "executedAt" TIMESTAMP(3),
  CONSTRAINT "DeleteRequest_pkey" PRIMARY KEY ("id")
);

-- Uniques / Indexes
CREATE UNIQUE INDEX "User_orgId_email_key" ON "User"("orgId", "email");
CREATE UNIQUE INDEX "Role_orgId_key_key" ON "Role"("orgId", "key");
CREATE UNIQUE INDEX "FileObject_orgId_storageKey_key" ON "FileObject"("orgId", "storageKey");
CREATE UNIQUE INDEX "CaseFeeTerms_caseId_key" ON "CaseFeeTerms"("caseId");
CREATE UNIQUE INDEX "DeleteRequest_org_entity_status_key" ON "DeleteRequest"("orgId","entityType","entityId","status");

CREATE INDEX "User_orgId_idx" ON "User"("orgId");
CREATE INDEX "Role_orgId_idx" ON "Role"("orgId");
CREATE INDEX "LawyerProfile_orgId_idx" ON "LawyerProfile"("orgId");
CREATE INDEX "LawyerProfile_adminUserId_idx" ON "LawyerProfile"("adminUserId");
CREATE INDEX "SecretaryProfile_orgId_idx" ON "SecretaryProfile"("orgId");
CREATE INDEX "SecretaryLawyer_orgId_idx" ON "SecretaryLawyer"("orgId");
CREATE INDEX "SecretaryLawyer_lawyerUserId_idx" ON "SecretaryLawyer"("lawyerUserId");
CREATE INDEX "Client_orgId_idx" ON "Client"("orgId");
CREATE INDEX "Client_orgId_deletedAt_idx" ON "Client"("orgId","deletedAt");
CREATE INDEX "Client_createdByUserId_idx" ON "Client"("createdByUserId");
CREATE INDEX "ClientLawyer_orgId_idx" ON "ClientLawyer"("orgId");
CREATE INDEX "ClientLawyer_lawyerUserId_idx" ON "ClientLawyer"("lawyerUserId");
CREATE INDEX "Case_orgId_idx" ON "Case"("orgId");
CREATE INDEX "Case_orgId_deletedAt_idx" ON "Case"("orgId","deletedAt");
CREATE INDEX "Case_clientId_idx" ON "Case"("clientId");
CREATE INDEX "Case_createdByUserId_idx" ON "Case"("createdByUserId");
CREATE INDEX "CaseLawyer_orgId_idx" ON "CaseLawyer"("orgId");
CREATE INDEX "CaseLawyer_lawyerUserId_idx" ON "CaseLawyer"("lawyerUserId");
CREATE INDEX "FileObject_orgId_idx" ON "FileObject"("orgId");
CREATE INDEX "Petition_orgId_idx" ON "Petition"("orgId");
CREATE INDEX "Petition_orgId_deletedAt_idx" ON "Petition"("orgId","deletedAt");
CREATE INDEX "Petition_caseId_idx" ON "Petition"("caseId");
CREATE INDEX "Petition_createdByUserId_idx" ON "Petition"("createdByUserId");
CREATE INDEX "Evidence_orgId_idx" ON "Evidence"("orgId");
CREATE INDEX "Evidence_orgId_deletedAt_idx" ON "Evidence"("orgId","deletedAt");
CREATE INDEX "Evidence_caseId_idx" ON "Evidence"("caseId");
CREATE INDEX "Evidence_createdByUserId_idx" ON "Evidence"("createdByUserId");
CREATE INDEX "ClientNote_orgId_idx" ON "ClientNote"("orgId");
CREATE INDEX "ClientNote_orgId_deletedAt_idx" ON "ClientNote"("orgId","deletedAt");
CREATE INDEX "ClientNote_clientId_idx" ON "ClientNote"("clientId");
CREATE INDEX "ClientNote_createdByUserId_idx" ON "ClientNote"("createdByUserId");
CREATE INDEX "Hearing_orgId_idx" ON "Hearing"("orgId");
CREATE INDEX "Hearing_orgId_deletedAt_idx" ON "Hearing"("orgId","deletedAt");
CREATE INDEX "Hearing_caseId_idx" ON "Hearing"("caseId");
CREATE INDEX "Hearing_hearingAt_idx" ON "Hearing"("hearingAt");
CREATE INDEX "Deadline_orgId_idx" ON "Deadline"("orgId");
CREATE INDEX "Deadline_orgId_deletedAt_idx" ON "Deadline"("orgId","deletedAt");
CREATE INDEX "Deadline_caseId_idx" ON "Deadline"("caseId");
CREATE INDEX "Deadline_dueAt_idx" ON "Deadline"("dueAt");
CREATE INDEX "Deadline_status_idx" ON "Deadline"("status");
CREATE INDEX "CalendarEvent_orgId_idx" ON "CalendarEvent"("orgId");
CREATE INDEX "CalendarEvent_orgId_deletedAt_idx" ON "CalendarEvent"("orgId","deletedAt");
CREATE INDEX "CalendarEvent_ownerUserId_idx" ON "CalendarEvent"("ownerUserId");
CREATE INDEX "CalendarEvent_startsAt_idx" ON "CalendarEvent"("startsAt");
CREATE INDEX "CaseFeeTerms_orgId_idx" ON "CaseFeeTerms"("orgId");
CREATE INDEX "CasePayment_orgId_idx" ON "CasePayment"("orgId");
CREATE INDEX "CasePayment_orgId_deletedAt_idx" ON "CasePayment"("orgId","deletedAt");
CREATE INDEX "CasePayment_caseId_idx" ON "CasePayment"("caseId");
CREATE INDEX "CasePayment_paidAt_idx" ON "CasePayment"("paidAt");
CREATE INDEX "CasePayment_recordedByUserId_idx" ON "CasePayment"("recordedByUserId");
CREATE INDEX "DeleteRequest_orgId_idx" ON "DeleteRequest"("orgId");
CREATE INDEX "DeleteRequest_entity_idx" ON "DeleteRequest"("entityType","entityId");
CREATE INDEX "DeleteRequest_status_idx" ON "DeleteRequest"("status");

-- Foreign keys
ALTER TABLE "OrgSettings" ADD CONSTRAINT "OrgSettings_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "User" ADD CONSTRAINT "User_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "Role" ADD CONSTRAINT "Role_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "UserRole" ADD CONSTRAINT "UserRole_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "UserRole" ADD CONSTRAINT "UserRole_roleId_fkey" FOREIGN KEY ("roleId") REFERENCES "Role"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "LawyerProfile" ADD CONSTRAINT "LawyerProfile_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "LawyerProfile" ADD CONSTRAINT "LawyerProfile_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "SecretaryProfile" ADD CONSTRAINT "SecretaryProfile_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "SecretaryProfile" ADD CONSTRAINT "SecretaryProfile_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "SecretaryLawyer" ADD CONSTRAINT "SecretaryLawyer_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "SecretaryLawyer" ADD CONSTRAINT "SecretaryLawyer_secretaryUserId_fkey" FOREIGN KEY ("secretaryUserId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "SecretaryLawyer" ADD CONSTRAINT "SecretaryLawyer_lawyerUserId_fkey" FOREIGN KEY ("lawyerUserId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "SecretaryLawyer" ADD CONSTRAINT "SecretaryLawyer_assignedByUserId_fkey" FOREIGN KEY ("assignedByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "Client" ADD CONSTRAINT "Client_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Client" ADD CONSTRAINT "Client_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Client" ADD CONSTRAINT "Client_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "ClientLawyer" ADD CONSTRAINT "ClientLawyer_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ClientLawyer" ADD CONSTRAINT "ClientLawyer_clientId_fkey" FOREIGN KEY ("clientId") REFERENCES "Client"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ClientLawyer" ADD CONSTRAINT "ClientLawyer_lawyerUserId_fkey" FOREIGN KEY ("lawyerUserId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ClientLawyer" ADD CONSTRAINT "ClientLawyer_assignedByUserId_fkey" FOREIGN KEY ("assignedByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "Case" ADD CONSTRAINT "Case_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Case" ADD CONSTRAINT "Case_clientId_fkey" FOREIGN KEY ("clientId") REFERENCES "Client"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Case" ADD CONSTRAINT "Case_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Case" ADD CONSTRAINT "Case_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "CaseLawyer" ADD CONSTRAINT "CaseLawyer_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CaseLawyer" ADD CONSTRAINT "CaseLawyer_caseId_fkey" FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CaseLawyer" ADD CONSTRAINT "CaseLawyer_lawyerUserId_fkey" FOREIGN KEY ("lawyerUserId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CaseLawyer" ADD CONSTRAINT "CaseLawyer_assignedByUserId_fkey" FOREIGN KEY ("assignedByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "FileObject" ADD CONSTRAINT "FileObject_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "Petition" ADD CONSTRAINT "Petition_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Petition" ADD CONSTRAINT "Petition_caseId_fkey" FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Petition" ADD CONSTRAINT "Petition_fileId_fkey" FOREIGN KEY ("fileId") REFERENCES "FileObject"("id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "Petition" ADD CONSTRAINT "Petition_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Petition" ADD CONSTRAINT "Petition_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "Evidence" ADD CONSTRAINT "Evidence_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Evidence" ADD CONSTRAINT "Evidence_caseId_fkey" FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Evidence" ADD CONSTRAINT "Evidence_fileId_fkey" FOREIGN KEY ("fileId") REFERENCES "FileObject"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Evidence" ADD CONSTRAINT "Evidence_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Evidence" ADD CONSTRAINT "Evidence_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "ClientNote" ADD CONSTRAINT "ClientNote_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ClientNote" ADD CONSTRAINT "ClientNote_clientId_fkey" FOREIGN KEY ("clientId") REFERENCES "Client"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ClientNote" ADD CONSTRAINT "ClientNote_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "ClientNote" ADD CONSTRAINT "ClientNote_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "Hearing" ADD CONSTRAINT "Hearing_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Hearing" ADD CONSTRAINT "Hearing_caseId_fkey" FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Hearing" ADD CONSTRAINT "Hearing_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Hearing" ADD CONSTRAINT "Hearing_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "Deadline" ADD CONSTRAINT "Deadline_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Deadline" ADD CONSTRAINT "Deadline_caseId_fkey" FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Deadline" ADD CONSTRAINT "Deadline_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Deadline" ADD CONSTRAINT "Deadline_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "CalendarEvent" ADD CONSTRAINT "CalendarEvent_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CalendarEvent" ADD CONSTRAINT "CalendarEvent_ownerUserId_fkey" FOREIGN KEY ("ownerUserId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CalendarEvent" ADD CONSTRAINT "CalendarEvent_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "CaseFeeTerms" ADD CONSTRAINT "CaseFeeTerms_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CaseFeeTerms" ADD CONSTRAINT "CaseFeeTerms_caseId_fkey" FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CaseFeeTerms" ADD CONSTRAINT "CaseFeeTerms_createdByUserId_fkey" FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "CasePayment" ADD CONSTRAINT "CasePayment_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CasePayment" ADD CONSTRAINT "CasePayment_caseId_fkey" FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "CasePayment" ADD CONSTRAINT "CasePayment_receiptFileId_fkey" FOREIGN KEY ("receiptFileId") REFERENCES "FileObject"("id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "CasePayment" ADD CONSTRAINT "CasePayment_recordedByUserId_fkey" FOREIGN KEY ("recordedByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "CasePayment" ADD CONSTRAINT "CasePayment_deletedByUserId_fkey" FOREIGN KEY ("deletedByUserId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "DeleteRequest" ADD CONSTRAINT "DeleteRequest_orgId_fkey" FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "DeleteRequest" ADD CONSTRAINT "DeleteRequest_requestedByUserId_fkey" FOREIGN KEY ("requestedByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "DeleteRequest" ADD CONSTRAINT "DeleteRequest_reviewedByUserId_fkey" FOREIGN KEY ("reviewedByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "DeleteRequest" ADD CONSTRAINT "DeleteRequest_executedByUserId_fkey" FOREIGN KEY ("executedByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
