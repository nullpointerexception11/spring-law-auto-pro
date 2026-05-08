-- Create the requested Super Admin Organization
INSERT INTO "Org" (id, name, "updatedAt") 
VALUES ('f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', 'Orhan Dogdu', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET "updatedAt" = CURRENT_TIMESTAMP;

-- Ensure Org has settings
INSERT INTO "OrgSettings" ("orgId", "updatedAt")
VALUES ('f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', CURRENT_TIMESTAMP)
ON CONFLICT ("orgId") DO NOTHING;

-- Create the Super Admin User
-- Password: admin1907
INSERT INTO "User" (id, "orgId", email, "fullName", "passwordHash", status, "updatedAt")
VALUES (
  'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e0', 
  'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', 
  'admin@orhandogdu', 
  'Orhan Doğdu', 
  '$2a$10$6GfK5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G', 
  'ACTIVE', 
  CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO UPDATE SET 
  "orgId" = 'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0',
  "email" = 'admin@orhandogdu',
  "passwordHash" = '$2a$10$6GfK5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G5G',
  "updatedAt" = CURRENT_TIMESTAMP;

-- Assign SUPER_ADMIN role
INSERT INTO "Role" (id, "orgId", "key")
VALUES ('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d0d0d0', 'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', 'SUPER_ADMIN')
ON CONFLICT ("orgId", "key") DO NOTHING;

INSERT INTO "UserRole" ("userId", "roleId")
SELECT 'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e0', id FROM "Role" 
WHERE "orgId" = 'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0' AND "key" = 'SUPER_ADMIN'
ON CONFLICT ("userId", "roleId") DO NOTHING;
