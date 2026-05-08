CREATE TABLE "PetitionDraft" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "caseId" UUID NOT NULL,
  "templateId" UUID,
  "title" TEXT NOT NULL,
  "content" TEXT,
  "status" TEXT NOT NULL DEFAULT 'DRAFT',
  "aiAssistEnabled" BOOLEAN NOT NULL DEFAULT false,
  "aiPrompt" TEXT,
  "createdByUserId" UUID NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "PetitionDraft_pkey" PRIMARY KEY ("id")
);

CREATE INDEX "PetitionDraft_orgId_idx" ON "PetitionDraft"("orgId");
CREATE INDEX "PetitionDraft_caseId_idx" ON "PetitionDraft"("caseId");
CREATE INDEX "PetitionDraft_templateId_idx" ON "PetitionDraft"("templateId");
CREATE INDEX "PetitionDraft_status_idx" ON "PetitionDraft"("status");

ALTER TABLE "PetitionDraft"
  ADD CONSTRAINT "PetitionDraft_orgId_fkey"
  FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "PetitionDraft"
  ADD CONSTRAINT "PetitionDraft_caseId_fkey"
  FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "PetitionDraft"
  ADD CONSTRAINT "PetitionDraft_templateId_fkey"
  FOREIGN KEY ("templateId") REFERENCES "PetitionTemplate"("id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "PetitionDraft"
  ADD CONSTRAINT "PetitionDraft_createdByUserId_fkey"
  FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
