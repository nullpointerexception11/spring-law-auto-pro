-- LawAuto Prestige Seed (V2 Platinum) - Final Corrected
-- Synchronized with Java Entities and v8.2 Schema

-- 1. Organizations
INSERT INTO "Org" ("id", "name", "displayName") VALUES
('11111111-1111-1111-1111-111111111111', 'Prestige Legal Partners', 'Prestige Legal');

-- 2. Global Users
INSERT INTO "User" ("id", "orgId", "email", "fullName", "passwordHash", "status") VALUES
('f1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'senior@prestige.com', 'Senior Partner', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy.Y6Apt661L94GZ.nC11mX8aK2p9j/6', 'ACTIVE');

-- 3. Matter Party Roles (Required for MatterParty)
INSERT INTO "MatterPartyRole" ("id", "orgId", "name", "category") VALUES
('10000000-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'MÜVEKKİL', 'CLIENT'),
('20000000-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'KARŞI TARAF', 'OPPOSING');

-- 4. Sample Parties
INSERT INTO "Party" ("id", "orgId", "fullName") VALUES
('30000000-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Global Tech Corp'),
('40000000-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Mehmet Yılmaz');

-- 5. A Litigation Matter
INSERT INTO "Matter" ("id", "orgId", "title", "referenceNumber", "status", "openedAt") VALUES
('50000000-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Yılmaz vs Ticaret Davası', '2024/452 E.', 'ACTIVE', NOW());

-- 6. Attach Parties to Matter
INSERT INTO "MatterParty" ("matterId", "partyId", "roleId") VALUES
('50000000-1111-1111-1111-111111111111', '30000000-1111-1111-1111-111111111111', '10000000-1111-1111-1111-111111111111'),
('50000000-1111-1111-1111-111111111111', '40000000-2222-2222-2222-222222222222', '20000000-2222-2222-2222-222222222222');

-- 7. Add a Legal Detail
INSERT INTO "LitigationDetail" ("matterId", "courtName", "caseNumber") VALUES
('50000000-1111-1111-1111-111111111111', 'İstanbul 5. Asliye Ticaret Mahkemesi', '2024/452 E.');

-- 8. Activity Event
INSERT INTO "ActivityEvent" ("id", "orgId", "userId", "matterId", "action", "summary") VALUES
(gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', '50000000-1111-1111-1111-111111111111', 'CREATED', 'Dava Senior Partner tarafından başlatıldı');
