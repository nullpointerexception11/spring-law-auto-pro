CREATE TABLE "ResearchSession" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "createdByUserId" UUID NOT NULL,
  "title" TEXT NOT NULL,
  "topic" TEXT,
  "notes" TEXT,
  "scopeType" TEXT NOT NULL,
  "status" TEXT NOT NULL DEFAULT 'ACTIVE',
  "caseId" UUID,
  "petitionId" UUID,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "ResearchSession_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "ResearchResult" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "researchSessionId" UUID NOT NULL,
  "sourceType" TEXT NOT NULL,
  "title" TEXT NOT NULL,
  "decisionDate" TIMESTAMP(3),
  "referenceNo" TEXT,
  "url" TEXT,
  "snippet" TEXT,
  "relevanceScore" DECIMAL(5,2),
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "ResearchResult_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "ResearchNote" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "researchSessionId" UUID NOT NULL,
  "userId" UUID NOT NULL,
  "noteText" TEXT NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "ResearchNote_pkey" PRIMARY KEY ("id")
);

CREATE INDEX "ResearchSession_orgId_idx" ON "ResearchSession"("orgId");
CREATE INDEX "ResearchSession_createdByUserId_idx" ON "ResearchSession"("createdByUserId");
CREATE INDEX "ResearchSession_caseId_idx" ON "ResearchSession"("caseId");
CREATE INDEX "ResearchSession_petitionId_idx" ON "ResearchSession"("petitionId");
CREATE INDEX "ResearchSession_scopeType_idx" ON "ResearchSession"("scopeType");
CREATE INDEX "ResearchResult_researchSessionId_idx" ON "ResearchResult"("researchSessionId");
CREATE INDEX "ResearchResult_sourceType_idx" ON "ResearchResult"("sourceType");
CREATE INDEX "ResearchNote_researchSessionId_idx" ON "ResearchNote"("researchSessionId");

ALTER TABLE "ResearchSession"
  ADD CONSTRAINT "ResearchSession_orgId_fkey"
  FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ResearchSession"
  ADD CONSTRAINT "ResearchSession_createdByUserId_fkey"
  FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "ResearchSession"
  ADD CONSTRAINT "ResearchSession_caseId_fkey"
  FOREIGN KEY ("caseId") REFERENCES "Case"("id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "ResearchSession"
  ADD CONSTRAINT "ResearchSession_petitionId_fkey"
  FOREIGN KEY ("petitionId") REFERENCES "Petition"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "ResearchResult"
  ADD CONSTRAINT "ResearchResult_researchSessionId_fkey"
  FOREIGN KEY ("researchSessionId") REFERENCES "ResearchSession"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "ResearchNote"
  ADD CONSTRAINT "ResearchNote_researchSessionId_fkey"
  FOREIGN KEY ("researchSessionId") REFERENCES "ResearchSession"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ResearchNote"
  ADD CONSTRAINT "ResearchNote_userId_fkey"
  FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
