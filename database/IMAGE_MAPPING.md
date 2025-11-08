# Product Image Mapping Guide

## Overview
This document maps the product images from `src/main/resources` to inventory items in the database.

## Image Directory Structure
```
src/main/resources/
├── beverages&extras/     (8 images)
├── breads&rolls/         (7 images)
├── cakes/                (6 images)
└── pastries&desserts/    (8 images)
```

---

## Complete Image Mapping

### 🍞 Breads & Rolls (7 images)
| Product Name | Image File | Path |
|-------------|-----------|------|
| French Baguette | frenchbaguette.jpg | breads&rolls/frenchbaguette.jpg |
| Pan de Sal Classic | pandesalclassic.jpg | breads&rolls/pandesalclassic.jpg |
| Ube Pan de Sal | ubepandesal.jpg | breads&rolls/ubepandesal.jpg |
| Dinner Rolls | dinnerrolls.jpg | breads&rolls/dinnerrolls.jpg |
| Garlic Bread | garlicbread.jpg | breads&rolls/garlicbread.jpg |
| Cheese Bread | cheesebread.jpg | breads&rolls/cheesebread.jpg |
| Whole Wheat Bread | wholewheatbread.jpg | breads&rolls/wholewheatbread.jpg |

### 🥐 Pastries & Desserts (8 images)
| Product Name | Image File | Path |
|-------------|-----------|------|
| Chocolate Croissant | chocolatecroissant.jpg | pastries&desserts/chocolatecroissant.jpg |
| Ensaymada | ensaymada.jpg | pastries&desserts/ensaymada.jpg |
| Cinnamon Roll | cinnamonroll.jpg | pastries&desserts/cinnamonroll.jpg |
| Blueberry Muffin | blueberrymuffin.jpg | pastries&desserts/blueberrymuffin.jpg |
| Brownies | brownies.jpg | pastries&desserts/brownies.jpg |
| Chocolate Chip Cookie | chocolatechipcookie.jpg | pastries&desserts/chocolatechipcookie.jpg |
| Apple Danish | appledanish.jpg | pastries&desserts/appledanish.jpg |
| Cheese Danish | cheesedanish.jpg | pastries&desserts/cheesedanish.jpg |

### 🍰 Cakes & Special Occasions (6 images)
| Product Name | Image File | Path |
|-------------|-----------|------|
| Chocolate Cake Slice | chocolatecakeslice.jpg | cakes/chocolatecakeslice.jpg |
| Ube Cake Slice | ubecakeslice.jpg | cakes/ubecakeslice.jpg |
| Cheesecake Slice | cheesecakeslice.jpg | cakes/cheesecakeslice.jpg |
| Red Velvet Slice | redvelvetslice.jpg | cakes/redvelvetslice.jpg |
| Carrot Cake Slice | carrotcakeslice.jpg | cakes/carrotcakeslice.jpg |
| Black Forest Slice | blackforestslice.jpg | cakes/blackforestslice.jpg |

### ☕ Beverages & Extras (8 images)
| Product Name | Image File | Path |
|-------------|-----------|------|
| Fresh Brewed Coffee | freshbrewedcoffee.jpg | beverages&extras/freshbrewedcoffee.jpg |
| Cappuccino | cappuccino.jpg | beverages&extras/cappuccino.jpg |
| Iced Coffee | icedcoffee.jpg | beverages&extras/icedcoffee.jpg |
| Hot Chocolate | hotchocolate.jpg | beverages&extras/hotchocolate.jpg |
| Orange Juice | orangejuice.jpg | beverages&extras/orangejuice.jpg |
| Mango Smoothie | mangosmoothie.jpg | beverages&extras/mangosmoothie.jpg |
| Green Tea | greentea.jpg | beverages&extras/greentea.jpg |
| Bottled Water | bottledwater.jpg | beverages&extras/bottledwater.jpg |

---

## Database Schema

The `inventory` table includes an `image_path` column:
```sql
CREATE TABLE inventory (
    ...
    image_path TEXT,
    ...
);
```

## How to Apply Mappings

### Option 1: Run SQL Script
Execute the provided SQL update script:
```bash
sqlite3 bakery_kiosk.db < database/sql/update_image_paths.sql
```

### Option 2: Use Java Utility
Use the `ImagePathMapper` utility class to programmatically update image paths.

### Option 3: Manual Database Update
```sql
UPDATE inventory SET image_path = 'breads&rolls/frenchbaguette.jpg' 
WHERE name = 'French Baguette';
```

---

## Loading Images in Java

### Example Code:
```java
// Load image from resources
String imagePath = product.getImagePath();
ImageIcon icon = new ImageIcon(
    getClass().getClassLoader().getResource(imagePath)
);

// Or use the utility
ImageIcon icon = ImageLoader.loadProductImage(imagePath);
```

---

## Adding New Products

When adding new products to inventory:

1. **Add image file** to appropriate category folder in `src/main/resources/`
2. **Update database** with the image path:
   ```sql
   INSERT INTO inventory (..., image_path) VALUES 
   (..., 'category/imagename.jpg');
   ```
3. **Update this mapping** document for reference

---

## Image Requirements

- **Format:** JPG (preferred), PNG (supported)
- **Naming:** lowercase, no spaces (use hyphens or camelCase)
- **Size:** Recommended 300x300px minimum
- **Aspect Ratio:** Square (1:1) or 4:3 for best display

---

## Troubleshooting

### Image Not Displaying?
1. Check the path is relative to `src/main/resources/`
2. Verify file exists in the resources folder
3. Ensure file name matches database entry (case-sensitive)
4. Check image file is copied to `target/classes/` during build

### Path Format
✅ Correct: `breads&rolls/frenchbaguette.jpg`  
❌ Wrong: `/breads&rolls/frenchbaguette.jpg`  
❌ Wrong: `src/main/resources/breads&rolls/frenchbaguette.jpg`

---

## Summary

- **Total Images:** 29 product images
- **Categories:** 4 main categories
- **Path Format:** `category/filename.jpg`
- **Storage:** `src/main/resources/`
- **Database Column:** `inventory.image_path`

Last Updated: November 4, 2025
