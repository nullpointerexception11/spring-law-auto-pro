-- Ensure Organization names and User emails are unique for login support
ALTER TABLE "Org" ADD CONSTRAINT "Org_name_key" UNIQUE ("name");
ALTER TABLE "User" ADD CONSTRAINT "User_email_key" UNIQUE ("email");
