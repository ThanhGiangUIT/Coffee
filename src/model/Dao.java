/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author PTC
 */
public class Dao {

    Connection con = MyConnection.getConnection();
    PreparedStatement ps, ps2;
    Statement st;
    ResultSet rs;

    public boolean insertProduct(Product p) {
        String sql = "insert into product (name, price, image, qty) values (?,?,?,?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setBytes(3, p.getImage());
            ps.setInt(4, p.getQty());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public void getallProducts(JTable table) {
        String sql = "select * from product order by id desc";

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();

            Object[] row;

            while (rs.next()) {
                row = new Object[5];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getDouble(3);
                row[3] = rs.getBytes(4);
                row[4] = rs.getInt(5);
                model.addRow(row);
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean update(Product product) {
        String sql = "update product set name = ?, price = ?, qty = ? where id = ?";

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getQty());
            ps.setInt(4, product.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean delete(Product product) {
        try {
            ps = con.prepareStatement("delete from product where id = ?");
            ps.setInt(1, product.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public int getMaxRowOrderTable() {
        int row = 0;

        try {
            st = con.createStatement();
            rs = st.executeQuery("select max(cid) from cart");

            while (rs.next()) {
                row = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return row + 1;
    }

    public boolean isProductExist(int cid, int pid) {
        try {
            ps = con.prepareStatement("select * from cart where cid = ? and pid = ?");
            ps.setInt(1, cid);
            ps.setInt(2, pid);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public boolean insertCart(Cart cart) {
        String sql = "insert into cart(cid, pid, pName, qty, price, total) values (?,?,?,?,?,?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cart.getId());
            ps.setInt(2, cart.getPid());
            ps.setString(3, cart.getpName());
            ps.setInt(4, cart.getQty());
            ps.setDouble(5, cart.getPrice());
            ps.setDouble(6, cart.getTotal());

            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public int getCartQuantity(int cid){
        String sql = "select qty from cart where cid = ?";
        int cartQuantity = 0;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cid);
            rs = ps.executeQuery();
             while (rs.next()) {
                cartQuantity = rs.getInt("qty");
            }         
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) rs.close();
               if (ps != null) ps.close();
            } catch (SQLException e) {
            e.printStackTrace();
            }
        }
        return cartQuantity;
    }
    public boolean updateCartQuantity(int cid, int qty, int proId, double total){
        String sql = "UPDATE cart SET qty = ?, total = ? WHERE cid = ? AND pid = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, qty);
            ps.setDouble(2, total);
            ps.setInt(3, cid);
            ps.setInt(4, proId);
            int rowUpdated = ps.executeUpdate();
            return rowUpdated > 0;       
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
           
    public int getMaxRowPaymentTable() {
        int row = 0;

        try {
            st = con.createStatement();
            rs = st.executeQuery("select max(pid) from payment");

            while (rs.next()) {
                row = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return row + 1;
    }

    public int getMaxRowCartTable() {
        int row = 0;

        try {
            st = con.createStatement();
            rs = st.executeQuery("select max(cid) from cart");

            while (rs.next()) {
                row = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return row;
    }

    public double subTotal() {
        double subTotal = 0.0;
        int cid = getMaxRowCartTable();

        try {
            st = con.createStatement();
            rs = st.executeQuery("select sum(total) as 'total' from cart where cid = '" + cid + "'");

            if (rs.next()) {
                subTotal = rs.getDouble(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subTotal;
    }

    public void getProductsFromCart(JTable table) {
        int cid = getMaxRowCartTable();
        String sql = "select * from cart where cid = ?";

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cid);
            rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();

            Object[] row;

            while (rs.next()) {
                row = new Object[6];
                row[0] = rs.getInt(1);
                row[1] = rs.getInt(2);
                row[2] = rs.getString(3);
                row[3] = rs.getInt(4);
                row[4] = rs.getDouble(5);
                row[5] = rs.getDouble(6);
                model.addRow(row);
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void recalculateQuantityFromProduct(int cid){
        String sql = "select (p.qty - c.qty) as newQty, p.id from product p join cart c where p.id = c.pid and c.cid = ?";
        String updateProductQuantity = "UPDATE product SET qty = ? WHERE id = ?";

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cid);
            ps2 = con.prepareStatement(updateProductQuantity);
            rs = ps.executeQuery();
             while (rs.next()) {
                int quantity = rs.getInt("newQty");
                int productId = rs.getInt("p.id");
                ps2.setInt(1, quantity);
                ps2.setInt(2, productId);
                ps2.executeUpdate();
            }           
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Đảm bảo đóng các tài nguyên để tránh rò rỉ tài nguyên
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (ps2 != null) ps2.close();
                if (con != null) con.close();
            } catch (SQLException e) {
            e.printStackTrace();
            }
        }
    }
    
       public int getProductQuantity(int pid){
        String sql = "select qty from product where id = ?";
        int stockQuantity = 0;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, pid);
            rs = ps.executeQuery();
             while (rs.next()) {
                stockQuantity = rs.getInt("qty");
            }         
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) rs.close();
               if (ps != null) ps.close();
            } catch (SQLException e) {
            e.printStackTrace();
            }
        }
        return stockQuantity;
    }
    
    public boolean insertPayment(Payment payment) {
        String sql = "insert into payment(pid, cName, proid, pName, total, pdate) values (?,?,?,?,?,?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, payment.getPid());
            ps.setString(2, payment.getcName());
            ps.setString(3, payment.getProId());
            ps.setString(4, payment.getProName());
            ps.setDouble(5, payment.getTotal());
            ps.setString(6, payment.getDate());
            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            return false;
        }
    }

    public boolean deleteCart(int cid) {
        try {
            ps = con.prepareStatement("delete from cart where cid = ?");
            ps.setInt(1, cid);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            return false;
        }
    }
    
    

    public void getPaymentDetails(JTable table) {
        String sql = "select * from payment order by pid desc";

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();

            Object[] row;

            while (rs.next()) {
                row = new Object[6];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getDouble(5);
                row[5] = rs.getString(6);
                model.addRow(row);
            }
        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int totalProducts() {
        int total = 0;

        try {
            st = con.createStatement();
            rs = st.executeQuery("select count(*) as 'total' from product");
            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public double todayRevenue(String date) {
        double total = 0.0;

        try {
            st = con.createStatement();
            rs = st.executeQuery("select sum(total) as 'total' from payment where pdate ='" + date + "'");
            if (rs.next()) {
                total = rs.getDouble(1);
            }

        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public int totalRevenue() {
        int total = 0;

        try {
            st = con.createStatement();
            rs = st.executeQuery("select sum(total) as 'total' from payment");
            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (Exception ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

}
