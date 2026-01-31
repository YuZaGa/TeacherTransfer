-- TeacherTransfer Database Initialization Script
-- Bihar Districts and Blocks Data

-- Insert Bihar Districts (38 districts)
INSERT INTO district (name, code, lat, lng) VALUES
('Araria', 'ARA', 26.1497, 87.5146),
('Arwal', 'ARW', 25.2441, 84.6604),
('Aurangabad', 'AUR', 24.7520, 84.3742),
('Banka', 'BAN', 24.8915, 86.9225),
('Begusarai', 'BEG', 25.4182, 86.1272),
('Bhagalpur', 'BHA', 25.2425, 86.9842),
('Bhojpur', 'BHO', 25.5571, 84.4467),
('Buxar', 'BUX', 25.5640, 83.9777),
('Darbhanga', 'DAR', 26.1542, 85.8918),
('East Champaran', 'ECH', 26.6447, 84.8356),
('Gaya', 'GAY', 24.7955, 85.0002),
('Gopalganj', 'GOP', 26.4691, 84.4381),
('Jamui', 'JAM', 24.9204, 86.2251),
('Jehanabad', 'JEH', 25.2076, 84.9869),
('Kaimur', 'KAI', 25.0523, 83.5806),
('Katihar', 'KAT', 25.5388, 87.5714),
('Khagaria', 'KHA', 25.5022, 86.4687),
('Kishanganj', 'KIS', 26.0936, 87.9470),
('Lakhisarai', 'LAK', 25.1563, 86.0945),
('Madhepura', 'MAD', 25.9210, 86.7916),
('Madhubani', 'MAB', 26.3483, 86.0715),
('Munger', 'MUN', 25.3708, 86.4735),
('Muzaffarpur', 'MUZ', 26.1225, 85.3906),
('Nalanda', 'NAL', 25.1357, 85.4437),
('Nawada', 'NAW', 24.8777, 85.5422),
('Patna', 'PAT', 25.5941, 85.1376),
('Purnia', 'PUR', 25.7771, 87.4753),
('Rohtas', 'ROH', 24.9739, 84.0063),
('Saharsa', 'SAH', 25.8838, 86.5973),
('Samastipur', 'SAM', 25.8629, 85.7811),
('Saran', 'SAR', 25.9115, 84.7536),
('Sheikhpura', 'SHE', 25.1410, 85.8510),
('Sheohar', 'SHO', 26.5167, 85.3000),
('Siwan', 'SIW', 26.2214, 84.3542),
('Supaul', 'SUP', 26.1239, 86.6044),
('Vaishali', 'VAI', 25.6838, 85.2138),
('West Champaran', 'WCH', 26.7271, 84.4332);

-- Insert Subscription Plans
INSERT INTO subscription_plan (code, name, price_paise, duration_days, features) VALUES
('BASIC_1M', 'Basic Monthly', 3900, 30, '{"matches_per_day": 10, "map_view": false}'),
('PREMIUM_1M', 'Premium Monthly', 9900, 30, '{"matches_per_day": 50, "map_view": true}'),
('PREMIUM_3M', 'Premium Quarterly', 24900, 90, '{"matches_per_day": 50, "map_view": true}');
