-- Schema hardening: missing foreign keys and supporting indexes

-- CalendarEvent.relatedCaseId -> Case.id
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'CalendarEvent_relatedCaseId_fkey'
  ) THEN
    ALTER TABLE "CalendarEvent"
      ADD CONSTRAINT "CalendarEvent_relatedCaseId_fkey"
      FOREIGN KEY ("relatedCaseId") REFERENCES "Case"("id")
      ON DELETE SET NULL ON UPDATE CASCADE;
  END IF;
END $$;

-- CalendarEvent.relatedClientId -> Client.id
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'CalendarEvent_relatedClientId_fkey'
  ) THEN
    ALTER TABLE "CalendarEvent"
      ADD CONSTRAINT "CalendarEvent_relatedClientId_fkey"
      FOREIGN KEY ("relatedClientId") REFERENCES "Client"("id")
      ON DELETE SET NULL ON UPDATE CASCADE;
  END IF;
END $$;

-- LawyerProfile.adminUserId -> User.id
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'LawyerProfile_adminUserId_fkey'
  ) THEN
    ALTER TABLE "LawyerProfile"
      ADD CONSTRAINT "LawyerProfile_adminUserId_fkey"
      FOREIGN KEY ("adminUserId") REFERENCES "User"("id")
      ON DELETE RESTRICT ON UPDATE CASCADE;
  END IF;
END $$;

-- Supporting indexes for relation lookups
CREATE INDEX IF NOT EXISTS "CalendarEvent_relatedCaseId_idx" ON "CalendarEvent"("relatedCaseId");
CREATE INDEX IF NOT EXISTS "CalendarEvent_relatedClientId_idx" ON "CalendarEvent"("relatedClientId");
