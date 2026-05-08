-- V6: Insurance Module Schema
ALTER TABLE "Case" 
ADD COLUMN "case_number" VARCHAR(100),
ADD COLUMN "case_type" VARCHAR(100),
ADD COLUMN "court_name" VARCHAR(255),
ADD COLUMN "is_insurance" BOOLEAN DEFAULT FALSE,
ADD COLUMN "notes" TEXT,
ADD COLUMN "status_court" VARCHAR(100),
ADD COLUMN "status_deadline" TIMESTAMP,
ADD COLUMN "trial_date" TIMESTAMP,
ADD COLUMN "created_at" TIMESTAMP DEFAULT NOW();

CREATE TABLE "InsuranceDetail" (
    "id" UUID PRIMARY KEY,
    "case_id" UUID NOT NULL UNIQUE REFERENCES "Case"("id") ON DELETE CASCADE,
    "crash_province" VARCHAR(100),
    "car_mark" VARCHAR(100),
    "car_model" VARCHAR(100),
    "car_plate" VARCHAR(50),
    "car_km" INTEGER,
    "car_price" DECIMAL(19,2),
    "damage_amount" DECIMAL(19,2),
    "part_replacement" TEXT,
    "part_repaired" TEXT,
    "defect_rate" VARCHAR(50),
    "opponent_name" VARCHAR(255),
    "opponent_id_card_no" VARCHAR(50),
    "opponent_plate" VARCHAR(50),
    "insurance_company" VARCHAR(255),
    "policy_no" VARCHAR(100),
    "policy_start" DATE,
    "policy_end" DATE,
    "arbitration_subject" TEXT, -- tahkim_konu
    "dispute_amount" DECIMAL(19,2), -- uyusmazlik_miktari
    "special_notes" TEXT,
    "created_at" TIMESTAMP DEFAULT NOW()
);
