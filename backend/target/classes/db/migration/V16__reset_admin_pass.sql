-- Reset Super Admin Password to 'admin123' for verification
UPDATE "User"
SET "passwordHash" = '$2a$10$GRLdNijSQMUvl/au9ShLOmv8y247.zUtyE0p6u4Z66U9.C9L7Z5L2'
WHERE email = 'superadmin@orhandogdu.com';
