/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import coffee.ForgotPasswordPage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PTC
 */
public class AdminDao {

    Connection con = MyConnection.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;

    public int getMaxRowAdminTable() {
        int row = 0;

        try {
            st = con.createStatement();
            rs = st.executeQuery("select max(id) from admin ");

            while (rs.next()) {
                row = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return row + 1;
    }

    public boolean isAdminNameExist(String username) {
        try {
            ps = con.prepareStatement("select * from admin where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public boolean insert(Admin admin) {
        String sql = "insert into admin (id, username, password, s_ques, ans) values(?,?,?,?,?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, admin.getId());
            ps.setString(2, admin.getUsername());
            ps.setString(3, admin.getPassword());
            ps.setString(4, admin.getsQues());
            ps.setString(5, admin.getAns());

            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean login(String username, String password) {
        try {
            ps = con.prepareStatement("select * from admin where username = ? and password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean getSecurity(String username) {
        try {
            ps = con.prepareStatement("select * from admin where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                ForgotPasswordPage.jTextField3.setText(rs.getString(4));
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean getAns(String username, String newAns) {
        try {
            ps = con.prepareStatement("select * from admin where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                String oldAns = rs.getString(5);
                if (newAns.equals(oldAns)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean setPassword(String username, String password) {
        String sql = "update admin set password = ? where username = ?";

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, password);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getRole(String username) {
        String role = "user"; // Mặc định, giả định người dùng không phải là admin

        try {
            String sql = "SELECT * FROM admin WHERE username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            // Biến boolean để kiểm tra xem có tài khoản "admin" không
            boolean isAdmin = false;

            if (rs.next()) {
                isAdmin = true; // Đặt biến isAdmin thành true nếu có tài khoản "admin"
            }

            if (isAdmin && "admin".equals(username)) {
                role = "admin"; // Nếu tên tài khoản là "admin" và có tài khoản "admin" tồn tại, thì gán vai trò là admin

                // Cập nhật vai trò trong cơ sở dữ liệu
                String updateSql = "UPDATE admin SET role = ? WHERE username = ?";
                PreparedStatement updatePs = con.prepareStatement(updateSql);
                updatePs.setString(1, role);
                updatePs.setString(2, username);
                updatePs.executeUpdate();
                updatePs.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return role;
    }

    public String checkUserRole(String username) {
        String role = " "; // Mặc định, giả định người dùng không phải là admin

        try {
            String sql = "SELECT role FROM admin WHERE username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                String userRole = rs.getString("role");
                if ("admin".equals(userRole)) {
                    role = "admin";
                } else {
                    role = "user";
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return role;
    }

    public String getUsername(int id) {
        String username = " ";

        try {
            String sql = "SELECT username FROM admin WHERE id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                username = rs.getString("username");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(AdminDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return username;
    }

}
