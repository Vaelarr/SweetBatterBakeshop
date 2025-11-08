package kiosk.database.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import kiosk.database.DatabaseConnection;
import kiosk.model.CustomOrder;
import kiosk.model.CustomOrder.*;
import kiosk.model.OrderAddOn;

/**
 * Data Access Object for Custom Orders
 */
public class CustomOrderDAO {
    private Connection connection;
    
    public CustomOrderDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Generate unique order number
     */
    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT COUNT(*) FROM custom_orders WHERE order_number LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "CO-" + datePart + "%");
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
            return String.format("CO-%s-%04d", datePart, count + 1);
        } catch (SQLException e) {
            System.err.println("Error generating order number: " + e.getMessage());
            return "CO-" + datePart + "-0001";
        }
    }
    
    /**
     * Insert a new custom order
     */
    public boolean insert(CustomOrder order) {
        String sql = "INSERT INTO custom_orders (order_number, customer_id, product_code, order_type, " +
                    "servings, message_on_item, special_instructions, base_price, addons_total, " +
                    "subtotal, discount_amount, tax_amount, delivery_fee, total_amount, " +
                    "deposit_required, payment_status, fulfillment_type, pickup_datetime, " +
                    "delivery_address_id, delivery_datetime, order_status, balance_due) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            connection.setAutoCommit(false);
            
            String orderNumber = generateOrderNumber();
            order.setOrderNumber(orderNumber);
            
            // Insert main order
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, orderNumber);
                pstmt.setString(2, order.getCustomerId());
                pstmt.setString(3, order.getProductCode());
                pstmt.setString(4, order.getOrderType());
                pstmt.setInt(5, order.getServings());
                pstmt.setString(6, order.getMessageOnItem());
                pstmt.setString(7, order.getSpecialInstructions());
                pstmt.setDouble(8, order.getBasePrice());
                pstmt.setDouble(9, order.getAddonsTotal());
                pstmt.setDouble(10, order.getSubtotal());
                pstmt.setDouble(11, order.getDiscountAmount());
                pstmt.setDouble(12, order.getTaxAmount());
                pstmt.setDouble(13, order.getDeliveryFee());
                pstmt.setDouble(14, order.getTotalAmount());
                pstmt.setDouble(15, order.getDepositRequired());
                pstmt.setString(16, order.getPaymentStatus().toString());
                pstmt.setString(17, order.getFulfillmentType().toString());
                pstmt.setTimestamp(18, Timestamp.valueOf(order.getPickupDatetime()));
                pstmt.setObject(19, order.getDeliveryAddressId());
                pstmt.setTimestamp(20, order.getDeliveryDatetime() != null ? 
                                  Timestamp.valueOf(order.getDeliveryDatetime()) : null);
                pstmt.setString(21, order.getOrderStatus().toString());
                pstmt.setDouble(22, order.getBalanceDue());
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            // Insert add-ons
            if (order.getAddons() != null && !order.getAddons().isEmpty()) {
                insertAddons(orderNumber, order.getAddons());
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error inserting custom order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Insert order add-ons
     */
    private void insertAddons(String orderNumber, List<OrderAddOn> addons) throws SQLException {
        String sql = "INSERT INTO custom_order_addons (order_number, addon_code, addon_category, " +
                    "quantity, price_modifier, total_addon_price) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (OrderAddOn addon : addons) {
                pstmt.setString(1, orderNumber);
                pstmt.setString(2, addon.getAddonCode());
                pstmt.setString(3, addon.getCategoryCode());
                pstmt.setInt(4, addon.getQuantity());
                pstmt.setDouble(5, addon.getPriceModifier());
                pstmt.setDouble(6, addon.getTotalAddonPrice());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    /**
     * Update order status
     */
    public boolean updateStatus(String orderNumber, OrderStatus newStatus) {
        String sql = "UPDATE custom_orders SET order_status = ?, updated_at = NOW() WHERE order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.toString());
            pstmt.setString(2, orderNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update payment status
     */
    public boolean updatePaymentStatus(String orderNumber, PaymentStatus newStatus, double depositPaid) {
        String sql = "UPDATE custom_orders SET payment_status = ?, deposit_paid = ?, " +
                    "balance_due = total_amount - ?, deposit_paid_at = NOW(), updated_at = NOW() " +
                    "WHERE order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.toString());
            pstmt.setDouble(2, depositPaid);
            pstmt.setDouble(3, depositPaid);
            pstmt.setString(4, orderNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get order by order number
     */
    public CustomOrder findByOrderNumber(String orderNumber) {
        String sql = "SELECT co.*, bp.product_name " +
                    "FROM custom_orders co " +
                    "JOIN custom_order_base_products bp ON co.product_code = bp.product_code " +
                    "WHERE co.order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, orderNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                CustomOrder order = extractOrderFromResultSet(rs);
                order.setAddons(getOrderAddons(orderNumber));
                return order;
            }
        } catch (SQLException e) {
            System.err.println("Error finding order: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all orders for a customer
     */
    public List<CustomOrder> findByCustomerId(String customerId) {
        List<CustomOrder> orders = new ArrayList<>();
        String sql = "SELECT co.*, bp.product_name " +
                    "FROM custom_orders co " +
                    "JOIN custom_order_base_products bp ON co.product_code = bp.product_code " +
                    "WHERE co.customer_id = ? ORDER BY co.created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CustomOrder order = extractOrderFromResultSet(rs);
                order.setAddons(getOrderAddons(order.getOrderNumber()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Get all orders (for admin panel)
     */
    public List<CustomOrder> getAllOrders() {
        List<CustomOrder> orders = new ArrayList<>();
        String sql = "SELECT co.*, bp.product_name, " +
                    "CONCAT(c.first_name, ' ', c.last_name) as customer_name " +
                    "FROM custom_orders co " +
                    "JOIN custom_order_base_products bp ON co.product_code = bp.product_code " +
                    "JOIN customers c ON co.customer_id = c.customer_id " +
                    "ORDER BY co.created_at DESC";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                CustomOrder order = extractOrderFromResultSet(rs);
                // Load addons for this order
                order.setAddons(getOrderAddons(order.getOrderNumber()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Get orders by status
     */
    public List<CustomOrder> findByStatus(OrderStatus status) {
        List<CustomOrder> orders = new ArrayList<>();
        String sql = "SELECT co.*, bp.product_name, " +
                    "CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                    "c.phone as customer_phone " +
                    "FROM custom_orders co " +
                    "JOIN custom_order_base_products bp ON co.product_code = bp.product_code " +
                    "JOIN customers c ON co.customer_id = c.customer_id " +
                    "WHERE co.order_status = ? " +
                    "ORDER BY co.pickup_datetime ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CustomOrder order = extractOrderFromResultSet(rs);
                // Load addons for this order
                order.setAddons(getOrderAddons(order.getOrderNumber()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by status: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Get upcoming orders (within next 7 days)
     */
    public List<CustomOrder> getUpcomingOrders() {
        List<CustomOrder> orders = new ArrayList<>();
        String sql = "SELECT co.*, bp.product_name, " +
                    "CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                    "c.phone as customer_phone " +
                    "FROM custom_orders co " +
                    "JOIN custom_order_base_products bp ON co.product_code = bp.product_code " +
                    "JOIN customers c ON co.customer_id = c.customer_id " +
                    "WHERE co.pickup_datetime BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 7 DAY) " +
                    "AND co.order_status IN ('CONFIRMED', 'IN_PRODUCTION', 'READY') " +
                    "ORDER BY co.pickup_datetime ASC";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                CustomOrder order = extractOrderFromResultSet(rs);
                // Load addons for this order
                order.setAddons(getOrderAddons(order.getOrderNumber()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting upcoming orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Get order add-ons
     */
    private List<OrderAddOn> getOrderAddons(String orderNumber) {
        List<OrderAddOn> addons = new ArrayList<>();
        String sql = "SELECT oa.*, a.addon_name, a.description, ac.category_name " +
                    "FROM custom_order_addons oa " +
                    "JOIN addons a ON oa.addon_code = a.addon_code " +
                    "JOIN addon_categories ac ON oa.addon_category = ac.category_code " +
                    "WHERE oa.order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, orderNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderAddOn addon = new OrderAddOn();
                addon.setId(rs.getInt("id"));
                addon.setAddonCode(rs.getString("addon_code"));
                addon.setAddonName(rs.getString("addon_name"));
                addon.setCategoryCode(rs.getString("addon_category"));
                addon.setCategoryName(rs.getString("category_name"));
                addon.setQuantity(rs.getInt("quantity"));
                addon.setPriceModifier(rs.getDouble("price_modifier"));
                addon.setTotalAddonPrice(rs.getDouble("total_addon_price"));
                addon.setDescription(rs.getString("description"));
                addons.add(addon);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order addons: " + e.getMessage());
        }
        
        return addons;
    }
    
    /**
     * Update order with admin notes
     */
    public boolean updateAdminNotes(String orderNumber, String notes) {
        String sql = "UPDATE custom_orders SET admin_notes = ?, updated_at = NOW() WHERE order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, notes);
            pstmt.setString(2, orderNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating admin notes: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Assign baker to order
     */
    public boolean assignBaker(String orderNumber, String bakerName) {
        String sql = "UPDATE custom_orders SET assigned_baker = ?, updated_at = NOW() WHERE order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bakerName);
            pstmt.setString(2, orderNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning baker: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cancel order
     */
    public boolean cancelOrder(String orderNumber, String reason, String cancelledBy) {
        String sql = "UPDATE custom_orders SET order_status = 'CANCELLED', " +
                    "cancellation_reason = ?, cancelled_by = ?, cancelled_at = NOW(), updated_at = NOW() " +
                    "WHERE order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reason);
            pstmt.setString(2, cancelledBy);
            pstmt.setString(3, orderNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update entire order (comprehensive update)
     */
    public boolean update(CustomOrder order) {
        String sql = "UPDATE custom_orders SET customer_id = ?, product_code = ?, order_type = ?, " +
                    "servings = ?, message_on_item = ?, special_instructions = ?, base_price = ?, " +
                    "addons_total = ?, subtotal = ?, discount_amount = ?, tax_amount = ?, " +
                    "delivery_fee = ?, total_amount = ?, deposit_required = ?, payment_status = ?, " +
                    "fulfillment_type = ?, pickup_datetime = ?, delivery_address_id = ?, " +
                    "delivery_datetime = ?, order_status = ?, balance_due = ?, updated_at = NOW() " +
                    "WHERE order_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, order.getCustomerId());
            pstmt.setString(2, order.getProductCode());
            pstmt.setString(3, order.getOrderType());
            pstmt.setInt(4, order.getServings());
            pstmt.setString(5, order.getMessageOnItem());
            pstmt.setString(6, order.getSpecialInstructions());
            pstmt.setDouble(7, order.getBasePrice());
            pstmt.setDouble(8, order.getAddonsTotal());
            pstmt.setDouble(9, order.getSubtotal());
            pstmt.setDouble(10, order.getDiscountAmount());
            pstmt.setDouble(11, order.getTaxAmount());
            pstmt.setDouble(12, order.getDeliveryFee());
            pstmt.setDouble(13, order.getTotalAmount());
            pstmt.setDouble(14, order.getDepositRequired());
            pstmt.setString(15, order.getPaymentStatus().toString());
            pstmt.setString(16, order.getFulfillmentType().toString());
            pstmt.setTimestamp(17, Timestamp.valueOf(order.getPickupDatetime()));
            pstmt.setObject(18, order.getDeliveryAddressId());
            pstmt.setTimestamp(19, order.getDeliveryDatetime() != null ? 
                              Timestamp.valueOf(order.getDeliveryDatetime()) : null);
            pstmt.setString(20, order.getOrderStatus().toString());
            pstmt.setDouble(21, order.getBalanceDue());
            pstmt.setString(22, order.getOrderNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete order permanently (use with caution)
     * This is a hard delete - consider using cancelOrder() for soft delete
     */
    public boolean delete(String orderNumber) {
        try {
            connection.setAutoCommit(false);
            
            // Delete addons first (foreign key constraint)
            String deleteAddonsSql = "DELETE FROM custom_order_addons WHERE order_number = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteAddonsSql)) {
                pstmt.setString(1, orderNumber);
                pstmt.executeUpdate();
            }
            
            // Delete main order
            String deleteOrderSql = "DELETE FROM custom_orders WHERE order_number = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteOrderSql)) {
                pstmt.setString(1, orderNumber);
                int rowsAffected = pstmt.executeUpdate();
                
                connection.commit();
                connection.setAutoCommit(true);
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract CustomOrder from ResultSet
     */
    private CustomOrder extractOrderFromResultSet(ResultSet rs) throws SQLException {
        CustomOrder order = new CustomOrder();
        order.setId(rs.getInt("id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setCustomerId(rs.getString("customer_id"));
        order.setProductCode(rs.getString("product_code"));
        order.setProductName(rs.getString("product_name"));
        order.setOrderType(rs.getString("order_type"));
        order.setServings(rs.getInt("servings"));
        order.setMessageOnItem(rs.getString("message_on_item"));
        order.setSpecialInstructions(rs.getString("special_instructions"));
        order.setBasePrice(rs.getDouble("base_price"));
        order.setAddonsTotal(rs.getDouble("addons_total"));
        order.setSubtotal(rs.getDouble("subtotal"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setTaxAmount(rs.getDouble("tax_amount"));
        order.setDeliveryFee(rs.getDouble("delivery_fee"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setDepositRequired(rs.getDouble("deposit_required"));
        order.setDepositPaid(rs.getDouble("deposit_paid"));
        order.setDepositPaymentMethod(rs.getString("deposit_payment_method"));
        order.setBalanceDue(rs.getDouble("balance_due"));
        
        String paymentStatus = rs.getString("payment_status");
        if (paymentStatus != null) {
            order.setPaymentStatus(PaymentStatus.valueOf(paymentStatus));
        }
        
        String fulfillmentType = rs.getString("fulfillment_type");
        if (fulfillmentType != null) {
            order.setFulfillmentType(FulfillmentType.valueOf(fulfillmentType));
        }
        
        Timestamp pickupDt = rs.getTimestamp("pickup_datetime");
        if (pickupDt != null) {
            order.setPickupDatetime(pickupDt.toLocalDateTime());
        }
        
        order.setDeliveryAddressId(rs.getObject("delivery_address_id", Integer.class));
        
        Timestamp deliveryDt = rs.getTimestamp("delivery_datetime");
        if (deliveryDt != null) {
            order.setDeliveryDatetime(deliveryDt.toLocalDateTime());
        }
        
        String orderStatus = rs.getString("order_status");
        if (orderStatus != null) {
            order.setOrderStatus(OrderStatus.valueOf(orderStatus));
        }
        
        order.setAdminNotes(rs.getString("admin_notes"));
        order.setCancellationReason(rs.getString("cancellation_reason"));
        order.setAssignedBaker(rs.getString("assigned_baker"));
        order.setAssignedDecorator(rs.getString("assigned_decorator"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp confirmedAt = rs.getTimestamp("confirmed_at");
        if (confirmedAt != null) {
            order.setConfirmedAt(confirmedAt.toLocalDateTime());
        }
        
        return order;
    }
    
    /**
     * Get order statistics for dashboard
     */
    public OrderStatistics getStatistics() {
        OrderStatistics stats = new OrderStatistics();
        String sql = "SELECT " +
                    "COUNT(*) as total_orders, " +
                    "SUM(CASE WHEN order_status = 'PENDING' THEN 1 ELSE 0 END) as pending, " +
                    "SUM(CASE WHEN order_status = 'CONFIRMED' THEN 1 ELSE 0 END) as confirmed, " +
                    "SUM(CASE WHEN order_status = 'IN_PRODUCTION' THEN 1 ELSE 0 END) as in_production, " +
                    "SUM(CASE WHEN order_status = 'READY' THEN 1 ELSE 0 END) as ready, " +
                    "SUM(CASE WHEN order_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
                    "SUM(total_amount) as total_revenue, " +
                    "SUM(deposit_paid) as total_deposits, " +
                    "SUM(balance_due) as total_balance_due " +
                    "FROM custom_orders " +
                    "WHERE order_status != 'CANCELLED'";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                stats.totalOrders = rs.getInt("total_orders");
                stats.pendingOrders = rs.getInt("pending");
                stats.confirmedOrders = rs.getInt("confirmed");
                stats.inProductionOrders = rs.getInt("in_production");
                stats.readyOrders = rs.getInt("ready");
                stats.completedOrders = rs.getInt("completed");
                stats.totalRevenue = rs.getDouble("total_revenue");
                stats.totalDeposits = rs.getDouble("total_deposits");
                stats.totalBalanceDue = rs.getDouble("total_balance_due");
            }
        } catch (SQLException e) {
            System.err.println("Error getting order statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Inner class for order statistics
     */
    public static class OrderStatistics {
        public int totalOrders;
        public int pendingOrders;
        public int confirmedOrders;
        public int inProductionOrders;
        public int readyOrders;
        public int completedOrders;
        public double totalRevenue;
        public double totalDeposits;
        public double totalBalanceDue;
    }
}


