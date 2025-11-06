
-- Auto Insurance Plans
INSERT IGNORE INTO policy_plans (policy_type, plan_name, description, base_coverage, base_premium) VALUES
('Auto', 'Basic Coverage', 'Covers essential liability and damages as required by law.', 500000.00, 2500.00),
('Auto', 'Plus Coverage', 'Includes comprehensive and collision coverage for your vehicle.', 1000000.00, 4000.00),
('Auto', 'Premium Coverage', 'Full protection including roadside assistance and zero depreciation.', 2000000.00, 6000.00);

-- Home Insurance Plans
INSERT IGNORE INTO policy_plans (policy_type, plan_name, description, base_coverage, base_premium) VALUES
('Home', 'Essential Home', 'Covers the structure of your home against fire and natural calamities.', 2500000.00, 1500.00),
('Home', 'Plus Home', 'Includes protection for your personal belongings and contents.', 5000000.00, 2500.00),
('Home', 'Premium Home', 'Comprehensive coverage including theft, liability, and temporary living expenses.', 10000000.00, 4500.00);

-- Health Insurance Plans
INSERT IGNORE INTO policy_plans (policy_type, plan_name, description, base_coverage, base_premium) VALUES
('Health', 'Silver Health', 'Essential coverage for hospital visits and critical emergencies.', 300000.00, 1000.00),
('Health', 'Gold Health', 'Comprehensive coverage including specialist visits and pre/post hospitalization.', 500000.00, 1800.00),
('Health', 'Platinum Health', 'Full coverage with low deductibles, maternity, and dental/vision benefits.', 1000000.00, 3000.00);

-- Life Insurance Plan (Represents a rate, not a fixed premium)
INSERT IGNORE INTO policy_plans (policy_type, plan_name, description, base_coverage, base_premium) VALUES
('Life', 'Term Life Plan', 'Provides a lump-sum payment to your beneficiaries in the event of your passing during the policy term.', 0, 40.00);

-- Business Insurance Plans
INSERT IGNORE INTO policy_plans (policy_type, plan_name, description, base_coverage, base_premium) VALUES
('Business', 'General Liability', 'Covers common business risks like bodily injury and property damage.', 5000000.00, 5000.00),
('Business', 'Commercial Property', 'Protects your physical assets, equipment, and location from damage or loss.', 10000000.00, 8000.00);

-- Travel Insurance Plans
INSERT IGNORE INTO policy_plans (policy_type, plan_name, description, base_coverage, base_premium) VALUES
('Travel', 'Essential Traveler', 'Covers medical emergencies and trip cancellation fees.', 2500000.00, 1500.00),
('Travel', 'Explorer Plus', 'Includes baggage loss, travel delay, and adventure sports coverage.', 5000000.00, 2500.00);

-- Policy Types
INSERT IGNORE INTO policy_types (name, icon_name, description, is_enabled) VALUES
('Auto Insurance', 'CarIcon', 'Protect your vehicle against accidents, theft, and damage. Get a personalized quote in minutes.', true),
('Home Insurance', 'HomeIcon', 'Comprehensive coverage for your home and personal belongings.', true),
('Health Insurance', 'HeartIcon', 'Find the right health plan to protect you and your family.', true),
('Life Insurance', 'ShieldIcon', 'Secure your family''s future with our flexible life insurance plans.', true),
('Business Insurance', 'BriefcaseIcon', 'Protect your business with our comprehensive commercial policies.', true),
('Travel Insurance', 'GlobeIcon', 'Travel with peace of mind knowing you are covered for any eventuality.', true);