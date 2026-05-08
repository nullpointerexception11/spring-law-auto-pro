-- Update Super Admin credentials to the latest requested ones
UPDATE "User"
SET 
  email = 'superadmin@orhandogdu.com',
  "fullName" = 'Süper Yönetici Orhan Dogdu',
  "passwordHash" = '$2a$10$vI8A7szak8.rR.mXyHhXnuqR6Fp.K7vB8X/r.rY.GZ1R8m.r.r.r.' -- Temporary, will be updated via command
WHERE id = 'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e0';

-- Ensure the email is updated for the Orhan Dogdu organization admin
UPDATE "User"
SET email = 'superadmin@orhandogdu.com'
WHERE "orgId" = 'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0';
