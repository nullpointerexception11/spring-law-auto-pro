-- ==========================================
-- LAW AUTO DATABASE SCHEMA (v1.0)
-- Optimized for Production SaaS
-- ==========================================

-- 1. ENUMS
CREATE TYPE org_plan AS ENUM ('FREE', 'PRO', 'ENTERPRISE');
CREATE TYPE org_status AS ENUM ('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION');
CREATE TYPE user_status AS ENUM ('INVITED', 'ACTIVE', 'INACTIVE', 'ANONYMIZED');
CREATE TYPE matter_status AS ENUM ('ACTIVE', 'CLOSED', 'ARCHIVED', 'SUSPENDED');
CREATE TYPE universal_event_type AS ENUM ('HEARING', 'DEADLINE', 'MEETING', 'TASK', 'REMINDER');
CREATE TYPE universal_event_status AS ENUM ('DRAFT', 'SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELED', 'RESCHEDULED', 'POSTPONED', 'MISSED');
CREATE TYPE party_type AS ENUM ('CLIENT', 'OPPONENT', 'LAWYER', 'EXPERT', 'WITNESS', 'OTHER');
CREATE TYPE party_category AS ENUM ('CLIENT', 'OPPONENT', 'INTERNAL', 'EXTERNAL', 'OTHER');
CREATE TYPE storage_provider AS ENUM ('LOCAL', 'S3', 'MINIO', 'AZURE_BLOB', 'GOOGLE_CLOUD_STORAGE');
CREATE TYPE ocr_status AS ENUM ('NONE', 'PENDING', 'PROCESSING', 'COMPLETED', 'FAILED');
CREATE TYPE currency AS ENUM ('TRY', 'USD', 'EUR', 'GBP');
CREATE TYPE fee_type AS ENUM ('BASE_ONLY', 'SUCCESS_ONLY', 'HYBRID');
CREATE TYPE fee_agreement_status AS ENUM ('DRAFT', 'PENDING_SIGNATURE', 'ACTIVE', 'COMPLETED', 'CANCELED');
CREATE TYPE activity_action AS ENUM ('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'UPLOAD_FILE', 'DOWNLOAD_FILE', 'ASSIGN_USER', 'CHANGE_STATUS');
CREATE TYPE entity_type AS ENUM ('ORG', 'USER', 'ROLE', 'MATTER', 'PARTY', 'FEE_AGREEMENT', 'FILE', 'EVENT');

-- 2. CORE TABLES

CREATE TABLE orgs (
  id UUID PRIMARY KEY,
  slug TEXT NOT NULL UNIQUE,
  display_name TEXT NOT NULL,
  plan org_plan NOT NULL DEFAULT 'FREE',
  status org_status NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  email TEXT NOT NULL,
  email_canonical TEXT NOT NULL,
  full_name TEXT NOT NULL,
  password_hash TEXT NOT NULL,
  status user_status NOT NULL DEFAULT 'INVITED',
  timezone TEXT NOT NULL DEFAULT 'Europe/Istanbul',
  locale TEXT NOT NULL DEFAULT 'tr-TR',
  notification_settings JSONB,
  last_login_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(org_id, email)
);

CREATE TABLE roles (
  id UUID PRIMARY KEY,
  org_id UUID REFERENCES orgs(id),
  role_key TEXT NOT NULL,
  display_name TEXT NOT NULL,
  system_role BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(org_id, role_key)
);

CREATE TABLE user_roles (
  user_id UUID NOT NULL REFERENCES users(id),
  role_id UUID NOT NULL REFERENCES roles(id),
  PRIMARY KEY (user_id, role_id)
);

-- 3. MATTER & PARTIES

CREATE TABLE matters (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  title TEXT NOT NULL,
  reference_number TEXT,
  status matter_status NOT NULL DEFAULT 'ACTIVE',
  summary TEXT,
  description TEXT,
  tags JSONB, -- Stored as JSON list of strings
  opened_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  closed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE matter_assignees (
  id UUID PRIMARY KEY,
  matter_id UUID NOT NULL REFERENCES matters(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role TEXT NOT NULL,
  assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(matter_id, user_id)
);

CREATE TABLE parties (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  full_name TEXT NOT NULL,
  type party_type NOT NULL DEFAULT 'OTHER',
  email TEXT,
  phone TEXT,
  tax_number TEXT,
  address TEXT,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(org_id, tax_number)
);

CREATE TABLE matter_party_roles (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  role_key TEXT NOT NULL,
  display_name TEXT NOT NULL,
  category party_category NOT NULL,
  is_system BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(org_id, role_key)
);

CREATE TABLE matter_parties (
  id UUID PRIMARY KEY,
  matter_id UUID NOT NULL REFERENCES matters(id) ON DELETE CASCADE,
  party_id UUID NOT NULL REFERENCES parties(id),
  role_id UUID NOT NULL REFERENCES matter_party_roles(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(matter_id, party_id, role_id)
);

-- 4. FINANCIALS & LITIGATION

CREATE TABLE litigation_details (
  id UUID PRIMARY KEY REFERENCES matters(id) ON DELETE CASCADE,
  court_name TEXT,
  case_number TEXT,
  judge_name TEXT,
  clerk_name TEXT,
  decision_summary TEXT,
  decision_date DATE,
  appeal_deadline DATE,
  appeal_filed BOOLEAN NOT NULL DEFAULT false,
  final_judgment_date DATE
);

CREATE TABLE files (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  storage_provider storage_provider NOT NULL DEFAULT 'S3',
  storage_key TEXT NOT NULL,
  file_name TEXT NOT NULL,
  mime_type TEXT,
  size_bytes BIGINT,
  sha256 TEXT,
  ocr_status ocr_status NOT NULL DEFAULT 'NONE',
  extracted_text TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE fee_agreements (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  matter_id UUID NOT NULL UNIQUE REFERENCES matters(id) ON DELETE CASCADE,
  fee_type fee_type NOT NULL,
  status fee_agreement_status NOT NULL DEFAULT 'DRAFT',
  base_amount DECIMAL(19,4),
  success_percentage DECIMAL(5,2),
  success_basis TEXT,
  currency currency NOT NULL DEFAULT 'TRY',
  signed_at TIMESTAMPTZ,
  signed_file_id UUID REFERENCES files(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. OPERATIONS & AUDIT

CREATE TABLE universal_events (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  matter_id UUID REFERENCES matters(id) ON DELETE CASCADE,
  type universal_event_type NOT NULL,
  title TEXT NOT NULL,
  description_html TEXT,
  location TEXT,
  start_at TIMESTAMPTZ NOT NULL,
  end_at TIMESTAMPTZ,
  rrule TEXT,
  status universal_event_status NOT NULL DEFAULT 'SCHEDULED',
  created_by_id UUID NOT NULL REFERENCES users(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE activity_events (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id),
  user_id UUID NOT NULL REFERENCES users(id),
  matter_id UUID REFERENCES matters(id) ON DELETE CASCADE,
  action activity_action NOT NULL,
  entity_type entity_type,
  entity_id UUID,
  correlation_id TEXT,
  ip_address TEXT,
  user_agent TEXT,
  summary TEXT,
  metadata JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE search_documents (
  id UUID PRIMARY KEY,
  org_id UUID NOT NULL REFERENCES orgs(id) ON DELETE CASCADE,
  entity_type entity_type NOT NULL,
  entity_id UUID NOT NULL,
  title TEXT NOT NULL,
  body TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 6. INDICES
CREATE INDEX idx_matters_org_status ON matters(org_id, status);
CREATE INDEX idx_events_org_start ON universal_events(org_id, start_at);
CREATE INDEX idx_files_org ON files(org_id);
CREATE INDEX idx_files_sha256 ON files(sha256);
CREATE INDEX idx_activity_org_matter ON activity_events(org_id, matter_id, created_at);
CREATE INDEX idx_search_org ON search_documents(org_id);
