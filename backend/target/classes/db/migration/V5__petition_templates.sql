CREATE TABLE "PetitionTemplate" (
  "id" UUID NOT NULL DEFAULT gen_random_uuid(),
  "orgId" UUID NOT NULL,
  "name" TEXT NOT NULL,
  "version" INTEGER NOT NULL DEFAULT 1,
  "isActive" BOOLEAN NOT NULL DEFAULT false,
  "structureJson" TEXT NOT NULL,
  "createdByUserId" UUID NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "PetitionTemplate_pkey" PRIMARY KEY ("id")
);

CREATE INDEX "PetitionTemplate_orgId_idx" ON "PetitionTemplate"("orgId");
CREATE INDEX "PetitionTemplate_orgId_isActive_idx" ON "PetitionTemplate"("orgId", "isActive");
CREATE UNIQUE INDEX "PetitionTemplate_orgId_name_version_key" ON "PetitionTemplate"("orgId", "name", "version");

ALTER TABLE "PetitionTemplate"
  ADD CONSTRAINT "PetitionTemplate_orgId_fkey"
  FOREIGN KEY ("orgId") REFERENCES "Org"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "PetitionTemplate"
  ADD CONSTRAINT "PetitionTemplate_createdByUserId_fkey"
  FOREIGN KEY ("createdByUserId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
