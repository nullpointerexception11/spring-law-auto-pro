-- Add SUPER_ADMIN to RoleKey enum
-- This must be in its own migration to be committed before use
ALTER TYPE "RoleKey" ADD VALUE 'SUPER_ADMIN' BEFORE 'ADMIN';
