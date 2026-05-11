-- ==========================================
-- PRESTIGE PLATINUM v8.2 DATABASE SCHEMA (REVISION 3 - SNAKE_CASE)
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
CREATE TABLE "org" (
  "id" UUID PRIMARY KEY,
  "slug" TEXT NOT NULL UNIQUE,
  "display_name" TEXT,
  "plan" TEXT DEFAULT 'FREE',
  "status" TEXT DEFAULT 'ACTIVE',
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "user" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID REFERENCES "org"("id"),
  "email" TEXT NOT NULL,
  "email_canonical" TEXT NOT NULL UNIQUE,
  "full_name" TEXT NOT NULL,
  "password_hash" TEXT NOT NULL,
  "status" TEXT DEFAULT 'PENDING',
  "timezone" TEXT DEFAULT 'Europe/Istanbul',
  "locale" TEXT DEFAULT 'tr-TR',
  "notification_settings" JSONB,
  "last_login_at" TIMESTAMPTZ,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE("org_id", "email")
);

CREATE TABLE "role" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID REFERENCES "org"("id"),
  "role_key" TEXT NOT NULL,
  "display_name" TEXT NOT NULL,
  "system_role" BOOLEAN DEFAULT false,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE("org_id", "role_key")
);

CREATE TABLE "user_role" (
  "user_id" UUID NOT NULL REFERENCES "user"("id"),
  "role_id" UUID NOT NULL REFERENCES "role"("id"),
  PRIMARY KEY ("user_id", "role_id")
);

CREATE TABLE "file_object" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID NOT NULL REFERENCES "org"("id"),
  "storage_provider" TEXT DEFAULT 'S3',
  "storage_key" TEXT NOT NULL,
  "file_name" TEXT NOT NULL,
  "mime_type" TEXT,
  "size_bytes" BIGINT,
  "sha256" TEXT,
  "ocr_status" TEXT DEFAULT 'PENDING',
  "extracted_text" TEXT,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. CASE & MATTER MANAGEMENT
CREATE TABLE "matter_party_role" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID NOT NULL REFERENCES "org"("id"),
  "name" TEXT NOT NULL,
  "category" TEXT,
  "created_at" TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE("org_id", "name")
);

CREATE TABLE "matter" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID NOT NULL REFERENCES "org"("id"),
  "title" TEXT NOT NULL,
  "reference_number" TEXT,
  "status" TEXT DEFAULT 'ACTIVE',
  "summary" TEXT,
  "description" TEXT,
  "tags" TEXT[],
  "opened_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "closed_at" TIMESTAMPTZ,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "party" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID NOT NULL REFERENCES "org"("id"),
  "full_name" TEXT NOT NULL,
  "email" TEXT,
  "phone" TEXT,
  "tax_number" TEXT,
  "address" TEXT,
  "notes" TEXT,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "matter_party" (
  "matter_id" UUID NOT NULL REFERENCES "matter"("id"),
  "party_id" UUID NOT NULL REFERENCES "party"("id"),
  "role_id" UUID NOT NULL REFERENCES "matter_party_role"("id"),
  PRIMARY KEY ("matter_id", "party_id", "role_id")
);

CREATE TABLE "litigation_detail" (
  "matter_id" UUID PRIMARY KEY REFERENCES "matter"("id") ON DELETE CASCADE,
  "court_name" TEXT,
  "case_number" TEXT,
  "judge_name" TEXT,
  "clerk_name" TEXT,
  "decision_summary" TEXT,
  "decision_date" DATE,
  "appeal_deadline" DATE,
  "appeal_filed" BOOLEAN DEFAULT false,
  "final_judgment_date" DATE
);

CREATE TABLE "hearing" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID NOT NULL REFERENCES "org"("id"),
  "matter_id" UUID NOT NULL REFERENCES "matter"("id"),
  "hearing_date" TIMESTAMPTZ NOT NULL,
  "judge" TEXT,
  "outcome" TEXT,
  "minutes_link" UUID REFERENCES "file_object"("id"),
  "notes" TEXT,
  "created_at" TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE "fee_agreement" (
  "id" UUID PRIMARY KEY,
  "org_id" UUID NOT NULL REFERENCES "org"("id"),
  "matter_id" UUID NOT NULL UNIQUE REFERENCES "matter"("id"),
  "fee_type" "FeeType" NOT NULL,
  "base_amount" DECIMAL(19,4),
  "success_percentage" DECIMAL(5,2),
  "currency" TEXT DEFAULT 'TRY',
  "signed_at" TIMESTAMPTZ,
  "signed_file_id" UUID REFERENCES "file_object"("id"),
  "created_at" TIMESTAMPTZ DEFAULT NOW()
);

-- INDEXES FOR PERFORMANCE
CREATE INDEX "idx_matter_org_status" ON "matter"("org_id", "status");
CREATE INDEX "idx_user_email_canonical" ON "user"("email_canonical");
