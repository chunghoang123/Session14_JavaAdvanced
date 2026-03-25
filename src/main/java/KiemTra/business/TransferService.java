package KiemTra.business;

import KiemTra.Database.MyDatabase;
import KiemTra.model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransferService {
    
    public void performSafeTransfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        Connection conn = null;
        
        try {
            conn = MyDatabase.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến database");
                return;
            }
            
            conn.setAutoCommit(false);
            
            if (!checkAccountExistsAndSufficientBalance(conn, fromAccountId, amount)) {
                System.err.println("Tài khoản không tồn tại hoặc không đủ số dư");
                conn.rollback();
                return;
            }
            
            if (!checkAccountExists(conn, toAccountId)) {
                System.err.println("Tài khoản người nhận không tồn tại");
                conn.rollback();
                return;
            }
            
            callUpdateBalanceProcedure(conn, fromAccountId, amount.negate());
            
            callUpdateBalanceProcedure(conn, toAccountId, amount);
            
            conn.commit();
            System.out.println("Chuyển khoản thành công!");
            
            displayTransferResults(conn, fromAccountId, toAccountId);
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Đã rollback transaction");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Lỗi hệ thống: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Đã rollback transaction");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    private boolean checkAccountExistsAndSufficientBalance(Connection conn, String accountId, BigDecimal amount) throws SQLException {
        String sql = "SELECT Balance FROM Accounts WHERE AccountId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal currentBalance = rs.getBigDecimal("Balance");
                    return currentBalance.compareTo(amount) >= 0;
                }
                return false;
            }
        }
    }
    
    private boolean checkAccountExists(Connection conn, String accountId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Accounts WHERE AccountId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    private void callUpdateBalanceProcedure(Connection conn, String accountId, BigDecimal amount) throws SQLException {
        String callSql = "{CALL sp_UpdateBalance(?, ?)}";
        
        try (CallableStatement cstmt = conn.prepareCall(callSql)) {
            cstmt.setString(1, accountId);
            cstmt.setBigDecimal(2, amount);
            
            cstmt.execute();
            System.out.println("Cập nhật tài khoản " + accountId + " với số tiền: " + amount);
        }
    }
    
    private void displayTransferResults(Connection conn, String fromAccountId, String toAccountId) throws SQLException {
        String sql = "SELECT AccountId, FullName, Balance FROM Accounts WHERE AccountId IN (?, ?) ORDER BY AccountId";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fromAccountId);
            pstmt.setString(2, toAccountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\nKẾT QUẢ ĐỐI SOÁT SAU CHUYỂN KHOẢN:");
                System.out.println("+----------+----------------------+-----------------+");
                System.out.println("| Account  | Full Name            | Balance         |");
                System.out.println("+----------+----------------------+-----------------+");
                
                List<Account> accounts = new ArrayList<>();
                while (rs.next()) {
                    Account account = new Account();
                    account.setAccountId(rs.getString("AccountId"));
                    account.setFullName(rs.getString("FullName"));
                    account.setBalance(rs.getBigDecimal("Balance"));
                    accounts.add(account);
                    System.out.println(account);
                }
                
                System.out.println("+----------+----------------------+-----------------+");
                System.out.println("Tổng số tài khoản: " + accounts.size());
            }
        }
    }
}
