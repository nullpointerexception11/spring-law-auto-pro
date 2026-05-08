-- LawAuto Prestige Seed (V2 LMMS)
-- Populates the Prestige Matter-Centric Schema

-- 1. Organizations
INSERT INTO "Org" ("id", "name", "subdomain") VALUES
('11111111-1111-1111-1111-111111111111', 'Prestige Legal Partners', 'prestige');

-- 2. Global Users
INSERT INTO "User" ("id", "email", "fullName", "passwordHash", "status") VALUES
('f1111111-1111-1111-1111-111111111111', 'senior@prestige.com', 'Senior Partner', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy.Y6Apt661L94GZ.nC11mX8aK2p9j/6', 'ACTIVE');

-- 3. User-Org Junction
INSERT INTO "UserOrg" ("userId", "orgId", "isOwner") VALUES
('f1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', true);

-- 4. Sample Parties (Client and Opponent)
INSERT INTO "Party" ("id", "orgId", "fullName", "type", "createdByUserId") VALUES
('c1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Global Tech Corp', 'COMPANY', 'f1111111-1111-1111-1111-111111111111'),
('c2222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Mehmet Yılmaz', 'PERSON', 'f1111111-1111-1111-1111-111111111111');

-- 5. A Litigation Matter (A Case)
INSERT INTO "Matter" ("id", "orgId", "type", "title", "referenceNumber", "createdByUserId") VALUES
('m1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'LITIGATION', 'Commercial Breach vs Yılmaz', '2024/452 E.', 'f1111111-1111-1111-1111-111111111111');

-- 6. Attach Parties to Matter
INSERT INTO "MatterParty" ("matterId", "partyId", "role", "isPrimary") VALUES
('m1111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'CLIENT', true),
('m1111111-1111-1111-1111-111111111111', 'c2222222-2222-2222-2222-222222222222', 'OPPONENT', false);

-- 7. Add a Legal Detail
INSERT INTO "LitigationDetail" ("matterId", "courtName", "courtCity", "degree") VALUES
('m1111111-1111-1111-1111-111111111111', 'İstanbul 5. Asliye Ticaret Mahkemesi', 'İstanbul', 'First Degree');

-- 8. Activity Event (Timeline)
INSERT INTO "ActivityEvent" ("orgId", "userId", "matterId", "action", "entityType", "entityId", "summary") VALUES
('11111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'm1111111-1111-1111-1111-111111111111', 'CREATED', 'MATTER', 'm1111111-1111-1111-1111-111111111111', 'Matter initiated by Senior Partner');
