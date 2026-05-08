-- V7: Add Word Template support for petitions
ALTER TABLE "PetitionTemplate" 
ADD COLUMN "template_file_id" UUID;

-- Optional: You might want to link it to the FileObject table if you have one
-- ALTER TABLE "PetitionTemplate" ADD CONSTRAINT fk_template_file FOREIGN KEY ("template_file_id") REFERENCES "FileObject"("id");
