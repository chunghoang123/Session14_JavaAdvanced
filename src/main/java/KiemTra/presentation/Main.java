package KiemTra.presentation;

import KiemTra.business.TransferService;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TransferService transferService = new TransferService();
        
        System.out.println("CHƯƠNG TRÌNH CHUYỂN KHOẢN AN TOÀN");
        System.out.println("=====================================");
        
        try {
            // Nhập thông tin chuyển khoản
            System.out.print("Nhập tài khoản gửi (AccountId): ");
            String fromAccountId = scanner.nextLine().trim();
            
            System.out.print("Nhập tài khoản nhận (AccountId): ");
            String toAccountId = scanner.nextLine().trim();
            
            System.out.print("Nhập số tiền chuyển: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            // Kiểm tra đầu vào
            if (fromAccountId.isEmpty() || toAccountId.isEmpty()) {
                System.err.println("Tài khoản không được để trống");
                return;
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Số tiền phải lớn hơn 0");
                return;
            }
            
            if (fromAccountId.equals(toAccountId)) {
                System.err.println("Tài khoản gửi và nhận không được trùng nhau");
                return;
            }
            
            // Thực hiện chuyển khoản
            System.out.println("\nBắt đầu xử lý chuyển khoản...");
            System.out.println("Từ: " + fromAccountId + " → Đến: " + toAccountId + " | Số tiền: " + amount);
            System.out.println("-------------------------------------");
            
            transferService.performSafeTransfer(fromAccountId, toAccountId, amount);
            
        } catch (NumberFormatException e) {
            System.err.println("Số tiền không hợp lệ");
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("\nChương trình kết thúc!");
        }
    }
}
