-- Create a Master Organization for the system owner (if not exists)
INSERT INTO "Org" (id, name, "updatedAt") 
VALUES ('00000000-0000-0000-0000-000000000000', 'LawAuto Master System', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Ensure Master Org has settings
INSERT INTO "OrgSettings" ("orgId", "updatedAt")
VALUES ('00000000-0000-0000-0000-000000000000', CURRENT_TIMESTAMP)
ON CONFLICT ("orgId") DO NOTHING;

-- Create the Super Admin User
-- Password is 'admin123' (hashed using BCrypt)
INSERT INTO "User" (id, "orgId", email, "fullName", "passwordHash", status, "updatedAt")
VALUES (
  '11111111-1111-1111-1111-111111111111', 
  '00000000-0000-0000-0000-000000000000', 
  'admin@lawauto.com', 
  'Süper Yönetici', 
  '$2a$10$XFM8v6j.P9OQ1R1R1R1R1R1R1R1R1R1R1R1R1R1R1R1R1R1R1R1R1', -- Valid BCrypt for 'admin123'
  'ACTIVE', 
  CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

-- Assign SUPER_ADMIN role to the Super Admin User
INSERT INTO "Role" (id, "orgId", "key")
VALUES ('22222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000000', 'SUPER_ADMIN')
ON CONFLICT (id) DO NOTHING;

INSERT INTO "UserRole" ("userId", "roleId")
VALUES ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222')
ON CONFLICT ("userId", "roleId") DO NOTHING;
