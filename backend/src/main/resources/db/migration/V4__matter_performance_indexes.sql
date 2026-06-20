CREATE INDEX IF NOT EXISTS idx_matter_org_opened_at
    ON "matter" ("org_id", "opened_at" DESC);

CREATE INDEX IF NOT EXISTS idx_hearing_matter_hearing_date
    ON "hearing" ("matter_id", "hearing_date");
