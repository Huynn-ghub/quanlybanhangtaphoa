package com.quanlybanhang;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Công cụ test mã hóa và kiểm tra mật khẩu bằng BCrypt.
 */
public class TestBCrypt {
    public static void main(String[] args) {
        String password = "password";
        String hashInDb = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.HEw5mKuiM";
        
        System.out.println("Checking BCrypt...");
        try {
            boolean check = BCrypt.checkpw(password, hashInDb);
            System.out.println("Match? " + check);
            
            String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("New generated hash for 'password': " + newHash);
            System.out.println("New hash match? " + BCrypt.checkpw(password, newHash));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
