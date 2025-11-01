-- ========================================
-- TEST DATA GENERATION
-- Populate database with realistic test data
-- ========================================

USE kiosk_db;

-- ========================================
-- CLEAR EXISTING TEST DATA (Optional)
-- ========================================
-- Uncomment to start fresh
/*
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE sales_items;
TRUNCATE TABLE sales_transactions;
TRUNCATE TABLE inventory_stock_history;
TRUNCATE TABLE help_requests;
TRUNCATE TABLE daily_sales_summary;
TRUNCATE TABLE product_sales_analytics;
TRUNCATE TABLE admin_activity_log;
SET FOREIGN_KEY_CHECKS = 1;
*/

-- ========================================
-- INVENTORY TEST DATA
-- ========================================

-- Insert comprehensive product catalog
INSERT INTO inventory (name, category, price, stock_quantity, min_stock_level, date_baked, good_until, expiration_date, barcode, supplier, description) VALUES
-- Breads & Rolls (baked fresh early morning, 3:00-5:00 AM)
('French Baguette', 'Breads & Rolls', 85.00, 30, 15, CONCAT(CURDATE(), ' 04:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 12:00:00'), 'BR-001', 'Chef Maria Santos', 'Traditional French bread, crusty outside and soft inside'),
('Whole Wheat Bread', 'Breads & Rolls', 95.00, 25, 15, CONCAT(CURDATE(), ' 05:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 4 DAY), ' 12:00:00'), 'BR-002', 'Baker Juan Reyes', '100% whole wheat, high fiber'),
('Ube Pan de Sal', 'Breads & Rolls', 45.00, 50, 25, CONCAT(CURDATE(), ' 03:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 12:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 12:00:00'), 'BR-003', 'Chef Maria Santos', 'Filipino classic with purple yam'),
('Cheese Bread', 'Breads & Rolls', 55.00, 40, 20, CONCAT(CURDATE(), ' 04:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 18:00:00'), 'BR-004', 'Baker Juan Reyes', 'Soft bread topped with cheese'),
('Garlic Bread', 'Breads & Rolls', 65.00, 35, 15, CONCAT(CURDATE(), ' 05:30:00'), CONCAT(CURDATE(), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 20:00:00'), 'BR-005', 'Chef Maria Santos', 'Toasted bread with garlic butter'),
('Pandesal Classic', 'Breads & Rolls', 35.00, 60, 30, CONCAT(CURDATE(), ' 03:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 10:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 10:00:00'), 'BR-006', 'Baker Rosa Cruz', 'Traditional Filipino bread roll'),
('Dinner Rolls (6pcs)', 'Breads & Rolls', 75.00, 30, 15, CONCAT(CURDATE(), ' 04:45:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 18:00:00'), 'BR-007', 'Baker Juan Reyes', 'Soft dinner rolls, pack of 6'),

-- Pastries & Desserts (baked mid-morning, 6:00-8:00 AM, shorter shelf life)
('Chocolate Croissant', 'Pastries & Desserts', 75.00, 35, 15, CONCAT(CURDATE(), ' 06:00:00'), CONCAT(CURDATE(), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 20:00:00'), 'PD-001', 'Pastry Chef Antoine', 'Buttery croissant with rich chocolate'),
('Ensaymada', 'Pastries & Desserts', 35.00, 40, 20, CONCAT(CURDATE(), ' 06:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 12:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 12:00:00'), 'PD-002', 'Baker Rosa Cruz', 'Sweet pastry with butter and sugar'),
('Cinnamon Roll', 'Pastries & Desserts', 65.00, 25, 12, CONCAT(CURDATE(), ' 07:00:00'), CONCAT(CURDATE(), ' 21:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 21:00:00'), 'PD-003', 'Chef Maria Santos', 'Soft roll with cinnamon and cream cheese'),
('Blueberry Muffin', 'Pastries & Desserts', 55.00, 30, 15, CONCAT(CURDATE(), ' 07:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 18:00:00'), 'PD-004', 'Baker Juan Reyes', 'Fresh blueberries in moist muffin'),
('Chocolate Chip Cookie', 'Pastries & Desserts', 25.00, 60, 30, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 5 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 7 DAY), ' 18:00:00'), 'PD-005', 'Pastry Chef Antoine', 'Classic chocolate chip cookies'),
('Apple Danish', 'Pastries & Desserts', 70.00, 20, 10, CONCAT(CURDATE(), ' 06:15:00'), CONCAT(CURDATE(), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 20:00:00'), 'PD-006', 'Pastry Chef Antoine', 'Flaky pastry with apple filling'),
('Cheese Danish', 'Pastries & Desserts', 70.00, 20, 10, CONCAT(CURDATE(), ' 06:15:00'), CONCAT(CURDATE(), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 20:00:00'), 'PD-007', 'Pastry Chef Antoine', 'Flaky pastry with cream cheese'),
('Brownies', 'Pastries & Desserts', 45.00, 35, 15, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 13:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 5 DAY), ' 18:00:00'), 'PD-008', 'Chef Maria Santos', 'Fudgy chocolate brownies'),

-- Cakes & Special Occasions (prepared previous day afternoon, refrigerated, 3-4 days shelf life)
('Chocolate Cake Slice', 'Cakes & Special Occasions', 120.00, 20, 8, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 15:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 4 DAY), ' 20:00:00'), 'CK-001', 'Cake Specialist Lisa', 'Rich chocolate layer cake'),
('Red Velvet Slice', 'Cakes & Special Occasions', 130.00, 15, 8, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 15:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 4 DAY), ' 20:00:00'), 'CK-002', 'Cake Specialist Lisa', 'Classic red velvet with cream cheese'),
('Carrot Cake Slice', 'Cakes & Special Occasions', 125.00, 18, 8, CONCAT(CURDATE(), ' 09:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 4 DAY), ' 20:00:00'), 'CK-003', 'Cake Specialist Lisa', 'Moist carrot cake with walnuts'),
('Ube Cake Slice', 'Cakes & Special Occasions', 135.00, 15, 8, CONCAT(CURDATE(), ' 09:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 4 DAY), ' 20:00:00'), 'CK-004', 'Baker Rosa Cruz', 'Purple yam cake with ube halaya'),
('Black Forest Slice', 'Cakes & Special Occasions', 140.00, 12, 6, CONCAT(CURDATE(), ' 10:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 4 DAY), ' 20:00:00'), 'CK-005', 'Cake Specialist Lisa', 'Chocolate cake with cherries'),
('Cheesecake Slice', 'Cakes & Special Occasions', 150.00, 15, 8, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 16:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 4 DAY), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 5 DAY), ' 20:00:00'), 'CK-006', 'Cake Specialist Lisa', 'New York style cheesecake'),

-- Beverages & Extras (prepared fresh or pre-packaged, varying shelf life)
('Fresh Brewed Coffee', 'Beverages & Extras', 55.00, 100, 30, NULL, NULL, CONCAT(DATE_ADD(CURDATE(), INTERVAL 90 DAY), ' 23:59:59'), 'BV-001', 'Manila Coffee Roasters', 'Premium arabica coffee'),
('Cappuccino', 'Beverages & Extras', 75.00, 80, 25, NULL, NULL, CONCAT(DATE_ADD(CURDATE(), INTERVAL 90 DAY), ' 23:59:59'), 'BV-002', 'Manila Coffee Roasters', 'Espresso with steamed milk'),
('Iced Coffee', 'Beverages & Extras', 65.00, 90, 30, NULL, NULL, CONCAT(DATE_ADD(CURDATE(), INTERVAL 90 DAY), ' 23:59:59'), 'BV-003', 'Manila Coffee Roasters', 'Cold brewed coffee over ice'),
('Hot Chocolate', 'Beverages & Extras', 70.00, 75, 25, NULL, NULL, CONCAT(DATE_ADD(CURDATE(), INTERVAL 120 DAY), ' 23:59:59'), 'BV-004', 'Tableya Chocolate Co.', 'Rich hot chocolate drink'),
('Orange Juice', 'Beverages & Extras', 45.00, 60, 20, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 08:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 18:00:00'), 'BV-005', 'TropiFresh Beverages', 'Freshly squeezed orange juice'),
('Bottled Water', 'Beverages & Extras', 25.00, 150, 50, NULL, NULL, CONCAT(DATE_ADD(CURDATE(), INTERVAL 365 DAY), ' 23:59:59'), 'BV-006', 'Summit Water Corp.', 'Purified drinking water'),
('Mango Smoothie', 'Beverages & Extras', 85.00, 40, 15, CONCAT(CURDATE(), ' 08:00:00'), CONCAT(CURDATE(), ' 20:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 20:00:00'), 'BV-007', 'TropiFresh Beverages', 'Fresh mango smoothie'),
('Green Tea', 'Beverages & Extras', 50.00, 70, 25, NULL, NULL, CONCAT(DATE_ADD(CURDATE(), INTERVAL 180 DAY), ' 23:59:59'), 'BV-008', 'Celestial Tea Imports', 'Premium green tea')
ON DUPLICATE KEY UPDATE name=name;

-- Add some expired items for testing (discounted/removed from active sale)
INSERT INTO inventory (name, category, price, stock_quantity, min_stock_level, date_baked, good_until, expiration_date, barcode, supplier, is_active) VALUES
('Day-Old Pandesal', 'Breads & Rolls', 20.00, 15, 0, CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 03:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 10:00:00'), CONCAT(CURDATE(), ' 10:00:00'), 'EXP-001', 'Baker Rosa Cruz', FALSE),
('Expired Croissant', 'Pastries & Desserts', 30.00, 8, 0, CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 06:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 20:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 20:00:00'), 'EXP-002', 'Pastry Chef Antoine', FALSE)
ON DUPLICATE KEY UPDATE name=name;

-- ========================================
-- SALES TRANSACTION TEST DATA
-- ========================================

-- Generate sales for the last 30 days
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS generate_test_sales()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT DEFAULT 0;
    DECLARE trans_date TIMESTAMP;
    DECLARE trans_id VARCHAR(50);
    DECLARE item_count INT;
    DECLARE subtotal_amt DECIMAL(10,2);
    DECLARE discount_amt DECIMAL(10,2);
    DECLARE tax_amt DECIMAL(10,2);
    DECLARE total_amt DECIMAL(10,2);
    
    -- Generate sales for last 30 days
    WHILE i < 30 DO
        SET j = 0;
        SET trans_date = DATE_SUB(NOW(), INTERVAL i DAY);
        
        -- Generate 5-15 transactions per day
        WHILE j < (5 + FLOOR(RAND() * 10)) DO
            SET trans_id = CONCAT('TXN-', DATE_FORMAT(trans_date, '%Y%m%d'), '-', LPAD(j+1, 4, '0'));
            SET item_count = 1 + FLOOR(RAND() * 5);
            SET subtotal_amt = 100 + (RAND() * 500);
            
            -- Random discount (20% chance)
            IF RAND() < 0.2 THEN
                SET discount_amt = subtotal_amt * (0.05 + (RAND() * 0.15));
            ELSE
                SET discount_amt = 0;
            END IF;
            
            SET tax_amt = (subtotal_amt - discount_amt) * 0.12;
            SET total_amt = subtotal_amt - discount_amt + tax_amt;
            
            -- Insert transaction
            INSERT INTO sales_transactions (
                transaction_id,
                transaction_date,
                subtotal,
                discount_amount,
                tax_amount,
                total,
                payment_method,
                payment_amount,
                change_amount,
                discount_applied,
                customer_type
            ) VALUES (
                trans_id,
                DATE_ADD(trans_date, INTERVAL FLOOR(RAND() * 12) HOUR),
                subtotal_amt,
                discount_amt,
                tax_amt,
                total_amt,
                ELT(FLOOR(1 + RAND() * 3), 'CASH', 'ECASH', 'CARD'),
                total_amt + (RAND() * 100),
                (RAND() * 100),
                discount_amt > 0,
                ELT(FLOOR(1 + RAND() * 4), 'REGULAR', 'SENIOR', 'PWD', 'REGULAR')
            );
            
            -- Insert 1-5 items per transaction
            INSERT INTO sales_items (transaction_id, item_name, category, price, quantity, subtotal)
            SELECT 
                trans_id,
                name,
                category,
                price,
                1 + FLOOR(RAND() * 3),
                price * (1 + FLOOR(RAND() * 3))
            FROM inventory 
            WHERE is_active = TRUE
            ORDER BY RAND()
            LIMIT item_count;
            
            SET j = j + 1;
        END WHILE;
        
        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;

-- Execute the procedure to generate test data
-- CALL generate_test_sales();

-- Alternative: Manual sample transactions for today
INSERT INTO sales_transactions (transaction_id, transaction_date, subtotal, discount_amount, tax_amount, total, payment_method, customer_type, discount_applied) VALUES
('TXN-TODAY-0001', NOW(), 245.00, 24.50, 26.46, 246.96, 'CASH', 'REGULAR', TRUE),
('TXN-TODAY-0002', NOW(), 180.00, 0.00, 21.60, 201.60, 'ECASH', 'REGULAR', FALSE),
('TXN-TODAY-0003', NOW(), 320.00, 64.00, 30.72, 286.72, 'CASH', 'SENIOR', TRUE),
('TXN-TODAY-0004', NOW(), 155.00, 0.00, 18.60, 173.60, 'CARD', 'REGULAR', FALSE),
('TXN-TODAY-0005', NOW(), 420.00, 42.00, 45.36, 423.36, 'ECASH', 'REGULAR', TRUE)
ON DUPLICATE KEY UPDATE transaction_id=transaction_id;

INSERT INTO sales_items (transaction_id, item_name, category, price, quantity, subtotal) VALUES
-- Transaction 1
('TXN-TODAY-0001', 'French Baguette', 'Breads & Rolls', 85.00, 2, 170.00),
('TXN-TODAY-0001', 'Fresh Brewed Coffee', 'Beverages & Extras', 55.00, 1, 55.00),
('TXN-TODAY-0001', 'Chocolate Chip Cookie', 'Pastries & Desserts', 25.00, 1, 20.00),

-- Transaction 2
('TXN-TODAY-0002', 'Chocolate Cake Slice', 'Cakes & Special Occasions', 120.00, 1, 120.00),
('TXN-TODAY-0002', 'Cappuccino', 'Beverages & Extras', 75.00, 1, 60.00),

-- Transaction 3
('TXN-TODAY-0003', 'Ensaymada', 'Pastries & Desserts', 35.00, 4, 140.00),
('TXN-TODAY-0003', 'Pandesal Classic', 'Breads & Rolls', 35.00, 3, 105.00),
('TXN-TODAY-0003', 'Orange Juice', 'Beverages & Extras', 45.00, 2, 75.00),

-- Transaction 4
('TXN-TODAY-0004', 'Blueberry Muffin', 'Pastries & Desserts', 55.00, 2, 110.00),
('TXN-TODAY-0004', 'Iced Coffee', 'Beverages & Extras', 65.00, 1, 45.00),

-- Transaction 5
('TXN-TODAY-0005', 'Red Velvet Slice', 'Cakes & Special Occasions', 130.00, 2, 260.00),
('TXN-TODAY-0005', 'Cinnamon Roll', 'Pastries & Desserts', 65.00, 2, 130.00),
('TXN-TODAY-0005', 'Hot Chocolate', 'Beverages & Extras', 70.00, 1, 30.00)
ON DUPLICATE KEY UPDATE transaction_id=transaction_id;

-- ========================================
-- HELP REQUEST TEST DATA
-- ========================================

INSERT INTO help_requests (request_id, location, request_type, description, priority, status, assigned_to) VALUES
('HELP-001', 'Kiosk-A', 'ASSISTANCE', 'Customer needs help selecting items', 'MEDIUM', 'PENDING', 'admin'),
('HELP-002', 'Kiosk-B', 'TECHNICAL', 'Payment terminal not responding', 'HIGH', 'IN_PROGRESS', 'admin'),
('HELP-003', 'Kiosk-C', 'ASSISTANCE', 'Customer asking about allergen information', 'LOW', 'PENDING', NULL),
('HELP-004', 'Kiosk-A', 'TECHNICAL', 'Screen frozen on cart page', 'URGENT', 'RESOLVED', 'admin'),
('HELP-005', 'Kiosk-D', 'ASSISTANCE', 'Customer wants to apply senior discount', 'MEDIUM', 'RESOLVED', 'manager')
ON DUPLICATE KEY UPDATE request_id=request_id;

-- Update resolved requests with resolution time
UPDATE help_requests 
SET resolved_at = DATE_ADD(created_at, INTERVAL FLOOR(5 + RAND() * 20) MINUTE),
    resolution_notes = 'Issue resolved successfully'
WHERE status = 'RESOLVED';

-- ========================================
-- ADMIN ACTIVITY LOG TEST DATA
-- ========================================

INSERT INTO admin_activity_log (admin_username, action_type, action_description, affected_table) VALUES
('admin', 'LOGIN', 'User logged into admin panel', NULL),
('admin', 'INVENTORY_UPDATE', 'Updated stock for French Baguette', 'inventory'),
('manager', 'LOGIN', 'User logged into admin panel', NULL),
('admin', 'SALES_REPORT', 'Generated daily sales report', 'sales_transactions'),
('manager', 'HELP_REQUEST', 'Resolved help request HELP-005', 'help_requests'),
('admin', 'INVENTORY_ADD', 'Added new product: Green Tea', 'inventory'),
('admin', 'SETTINGS_UPDATE', 'Updated tax rate setting', 'system_settings')
ON DUPLICATE KEY UPDATE id=id;

-- ========================================
-- UPDATE SUMMARIES
-- ========================================

-- Update today's sales summary
CALL update_daily_sales_summary(CURDATE());

-- ========================================
-- VERIFICATION QUERIES
-- ========================================

SELECT '=== TEST DATA SUMMARY ===' AS Info;

SELECT 
    'Inventory Items' AS metric,
    COUNT(*) AS count,
    SUM(stock_quantity) AS total_units,
    CONCAT('₱', FORMAT(SUM(price * stock_quantity), 2)) AS total_value
FROM inventory WHERE is_active = TRUE;

SELECT 
    'Sales Transactions' AS metric,
    COUNT(*) AS count,
    CONCAT('₱', FORMAT(SUM(total), 2)) AS total_revenue
FROM sales_transactions;

SELECT 
    'Sales Items' AS metric,
    COUNT(*) AS count,
    SUM(quantity) AS total_qty
FROM sales_items;

SELECT 
    'Help Requests' AS metric,
    COUNT(*) AS count,
    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending,
    SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved
FROM help_requests;

SELECT 
    'Admin Users' AS metric,
    COUNT(*) AS count
FROM admin_users WHERE is_active = TRUE;

SELECT '✅ Test data generation completed!' AS Status;

-- ========================================
-- CLEANUP PROCEDURE
-- ========================================

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS cleanup_test_data()
BEGIN
    SET FOREIGN_KEY_CHECKS = 0;
    
    DELETE FROM sales_items WHERE transaction_id LIKE 'TXN-TEST%' OR transaction_id LIKE 'TXN-TODAY%';
    DELETE FROM sales_transactions WHERE transaction_id LIKE 'TXN-TEST%' OR transaction_id LIKE 'TXN-TODAY%';
    DELETE FROM help_requests WHERE request_id LIKE 'HELP-%';
    DELETE FROM inventory WHERE barcode LIKE 'EXP-%';
    DELETE FROM admin_activity_log WHERE id > 0;
    DELETE FROM inventory_stock_history WHERE id > 0;
    
    SET FOREIGN_KEY_CHECKS = 1;
    
    SELECT 'Test data cleaned up successfully' AS Status;
END //
DELIMITER ;

-- To clean up: CALL cleanup_test_data();
