-- LawAuto Initial Seed Data (Standardized)

-- 1. Organizations
INSERT INTO orgs (id, slug, display_name, plan, status) VALUES
('11111111-1111-1111-1111-111111111111', 'prestige-legal', 'Prestige Legal Partners', 'ENTERPRISE', 'ACTIVE');

-- 2. Users
-- password is 'password' hashed
INSERT INTO users (id, org_id, email, email_canonical, full_name, password_hash, status) VALUES
('f1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'admin@prestige.com', 'admin@prestige.com', 'System Admin', '$2a$10$DxnyFauRoL37AyO0GBVhgenfoi5noEHnlOsbhQcFuWrl9.FkTNQVK', 'ACTIVE');

-- 3. Roles
INSERT INTO roles (id, org_id, role_key, display_name, system_role) VALUES
(gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'ORG_ADMIN', 'Organization Administrator', true),
(gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'LAWYER', 'Lawyer', true);

-- 4. Matter Party Roles
INSERT INTO matter_party_roles (id, org_id, role_key, display_name, category, is_system) VALUES
('10000000-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'CLIENT', 'Müvekkil', 'CLIENT', true),
('20000000-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'OPPONENT', 'Karşı Taraf', 'OPPONENT', true);

-- 5. Sample Parties
INSERT INTO parties (id, org_id, full_name, type) VALUES
('30000000-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Global Tech Corp', 'CLIENT'),
('40000000-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Mehmet Yılmaz', 'OPPONENT');

-- 6. Sample Matter
INSERT INTO matters (id, org_id, title, reference_number, status, opened_at) VALUES
('50000000-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Yılmaz vs Ticaret Davası', '2024/452 E.', 'ACTIVE', NOW());

-- 7. Attach Parties to Matter
INSERT INTO matter_parties (id, matter_id, party_id, role_id) VALUES
(gen_random_uuid(), '50000000-1111-1111-1111-111111111111', '30000000-1111-1111-1111-111111111111', '10000000-1111-1111-1111-111111111111'),
(gen_random_uuid(), '50000000-1111-1111-1111-111111111111', '40000000-2222-2222-2222-222222222222', '20000000-2222-2222-2222-222222222222');

-- 8. Add a Legal Detail
INSERT INTO litigation_details (id, court_name, case_number) VALUES
('50000000-1111-1111-1111-111111111111', 'İstanbul 5. Asliye Ticaret Mahkemesi', '2024/452 E.');

-- 9. Activity Event
INSERT INTO activity_events (id, org_id, user_id, matter_id, action, summary) VALUES
(gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', '50000000-1111-1111-1111-111111111111', 'CREATE', 'Dava Admin tarafından başlatıldı');
