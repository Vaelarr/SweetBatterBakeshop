-- Add sample customer addresses for testing delivery functionality
-- This script inserts sample addresses for existing customers

USE kiosk_db;

-- Check if customer_addresses table exists
-- If not, create it (should already exist from schema)

-- Insert sample addresses for testing
-- Note: Replace 'CUST001' with actual customer IDs from your customers table

-- Sample address for testing
INSERT INTO customer_addresses 
    (customer_id, street_address, barangay, city, province, postal_code, is_default, created_at, updated_at)
VALUES 
    ('CUST001', '123 Main Street', 'Poblacion', 'Quezon City', 'Metro Manila', '1100', TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Verify insertion
SELECT * FROM customer_addresses;

-- To enable delivery functionality later:
-- 1. Get the customer's actual address from the delivery_address field
-- 2. Insert it into customer_addresses table
-- 3. Get the auto-generated ID
-- 4. Use that ID when creating the order

-- Example query to check customer IDs:
SELECT customer_id, CONCAT(first_name, ' ', last_name) as customer_name 
FROM customers 
LIMIT 10;
