-- LawAuto Initial Seed (Squashed V1-V18)

-- 1. Create the Main Organization
INSERT INTO "Org" (id, name, "updatedAt") 
VALUES ('f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', 'Orhan Dogdu', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 2. Create Org Settings
INSERT INTO "OrgSettings" ("orgId", "updatedAt")
VALUES ('f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', CURRENT_TIMESTAMP)
ON CONFLICT ("orgId") DO NOTHING;

-- 3. Create the Super Admin User
-- Email: superadmin@orhandogdu.com
-- Password: superadmin18695531334 (hashed below)
INSERT INTO "User" (id, "orgId", email, "fullName", "passwordHash", status, "updatedAt")
VALUES (
  'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e0', 
  'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', 
  'superadmin@orhandogdu.com', 
  'Süper Yönetici Orhan Doğdu', 
  '$2a$10$73h4KNS4ra4CXzZQ3OKQtuhijmHc0bPTgdMNqc3phudBdiaw5wNoa', 
  'ACTIVE', 
  CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

-- 4. Create the SUPER_ADMIN role
INSERT INTO "Role" (id, "orgId", "key")
VALUES ('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d0d0d0', 'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', 'SUPER_ADMIN')
ON CONFLICT (id) DO NOTHING;

-- 5. Assign the role to the user
INSERT INTO "UserRole" ("userId", "roleId")
VALUES ('e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e0', 'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d0d0d0')
ON CONFLICT ("userId", "roleId") DO NOTHING;
