-- ========================================
-- Update Inventory Items with Image Paths
-- Maps product images from resources folder
-- ========================================

-- BREADS & ROLLS
UPDATE inventory SET image_path = 'breads&rolls/frenchbaguette.jpg' WHERE name = 'French Baguette';
UPDATE inventory SET image_path = 'breads&rolls/pandesalclassic.jpg' WHERE name = 'Pan de Sal Classic' OR name LIKE '%Pan de Sal%' AND name NOT LIKE '%Ube%';
UPDATE inventory SET image_path = 'breads&rolls/ubepandesal.jpg' WHERE name = 'Ube Pan de Sal' OR name LIKE '%Ube Pan%';
UPDATE inventory SET image_path = 'breads&rolls/dinnerrolls.jpg' WHERE name LIKE '%Dinner Roll%' OR name = 'Dinner Rolls';
UPDATE inventory SET image_path = 'breads&rolls/garlicbread.jpg' WHERE name LIKE '%Garlic Bread%';
UPDATE inventory SET image_path = 'breads&rolls/cheesebread.jpg' WHERE name LIKE '%Cheese Bread%';
UPDATE inventory SET image_path = 'breads&rolls/wholewheatbread.jpg' WHERE name LIKE '%Whole Wheat%' OR name LIKE '%Wheat Bread%';

-- PASTRIES & DESSERTS
UPDATE inventory SET image_path = 'pastries&desserts/chocolatecroissant.jpg' WHERE name = 'Chocolate Croissant' OR name LIKE '%Chocolate Croissant%';
UPDATE inventory SET image_path = 'pastries&desserts/ensaymada.jpg' WHERE name = 'Ensaymada' OR name LIKE '%Ensaymada%';
UPDATE inventory SET image_path = 'pastries&desserts/cinnamonroll.jpg' WHERE name = 'Cinnamon Roll' OR name LIKE '%Cinnamon Roll%';
UPDATE inventory SET image_path = 'pastries&desserts/blueberrymuffin.jpg' WHERE name LIKE '%Blueberry Muffin%' OR name LIKE '%Blueberry%Muffin%';
UPDATE inventory SET image_path = 'pastries&desserts/brownies.jpg' WHERE name LIKE '%Brownie%';
UPDATE inventory SET image_path = 'pastries&desserts/chocolatechipcookie.jpg' WHERE name LIKE '%Chocolate Chip Cookie%' OR name LIKE '%Choco Chip%';
UPDATE inventory SET image_path = 'pastries&desserts/appledanish.jpg' WHERE name LIKE '%Apple Danish%';
UPDATE inventory SET image_path = 'pastries&desserts/cheesedanish.jpg' WHERE name LIKE '%Cheese Danish%';

-- CAKES & SPECIAL OCCASIONS
UPDATE inventory SET image_path = 'cakes/chocolatecakeslice.jpg' WHERE name = 'Chocolate Cake Slice' OR name LIKE '%Chocolate Cake%';
UPDATE inventory SET image_path = 'cakes/ubecakeslice.jpg' WHERE name LIKE '%Ube Cake%';
UPDATE inventory SET image_path = 'cakes/cheesecakeslice.jpg' WHERE name LIKE '%Cheesecake%';
UPDATE inventory SET image_path = 'cakes/redvelvetslice.jpg' WHERE name LIKE '%Red Velvet%';
UPDATE inventory SET image_path = 'cakes/carrotcakeslice.jpg' WHERE name LIKE '%Carrot Cake%';
UPDATE inventory SET image_path = 'cakes/blackforestslice.jpg' WHERE name LIKE '%Black Forest%';

-- BEVERAGES & EXTRAS
UPDATE inventory SET image_path = 'beverages&extras/freshbrewedcoffee.jpg' WHERE name = 'Fresh Brewed Coffee' OR name LIKE '%Brewed Coffee%';
UPDATE inventory SET image_path = 'beverages&extras/cappuccino.jpg' WHERE name LIKE '%Cappuccino%';
UPDATE inventory SET image_path = 'beverages&extras/icedcoffee.jpg' WHERE name LIKE '%Iced Coffee%';
UPDATE inventory SET image_path = 'beverages&extras/hotchocolate.jpg' WHERE name LIKE '%Hot Chocolate%' OR name LIKE '%Hot Choco%';
UPDATE inventory SET image_path = 'beverages&extras/orangejuice.jpg' WHERE name = 'Orange Juice' OR name LIKE '%Orange Juice%';
UPDATE inventory SET image_path = 'beverages&extras/mangosmoothie.jpg' WHERE name LIKE '%Mango%Smoothie%';
UPDATE inventory SET image_path = 'beverages&extras/greentea.jpg' WHERE name LIKE '%Green Tea%';
UPDATE inventory SET image_path = 'beverages&extras/bottledwater.jpg' WHERE name LIKE '%Water%' OR name LIKE '%Bottled Water%';

-- Verify updates
SELECT 
    name, 
    category, 
    image_path,
    CASE 
        WHEN image_path IS NOT NULL THEN '✅ Mapped'
        ELSE '❌ No Image'
    END as Status
FROM inventory
ORDER BY category, name;

-- ========================================
-- Summary of Image Mappings
-- ========================================
SELECT '✅ Image paths updated successfully!' AS Result;

SELECT 
    category,
    COUNT(*) as total_items,
    SUM(CASE WHEN image_path IS NOT NULL THEN 1 ELSE 0 END) as items_with_images,
    SUM(CASE WHEN image_path IS NULL THEN 1 ELSE 0 END) as items_without_images
FROM inventory
GROUP BY category
ORDER BY category;
