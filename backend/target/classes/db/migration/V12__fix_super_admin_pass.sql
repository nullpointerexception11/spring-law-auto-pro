-- Fix Super Admin Password to 'admin123'
UPDATE "User" 
SET "passwordHash" = '$2a$10$GRLdNijSQMUvl/au9ShLOmv8y247.zUtyE0p6u4Z66U9.C9L7Z5L2' 
WHERE email = 'admin@lawauto.com';

-- Ensure Organization ID is exactly the system master ID
UPDATE "User"
SET "orgId" = '00000000-0000-0000-0000-000000000000'
WHERE email = 'admin@lawauto.com';
