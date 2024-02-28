package vcp.np.cas.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public AuthenticationService() {
    }
	
    public String hashPassword(String saltValue, String rawPassword) {
        String passwordWithSalt = saltValue + rawPassword;
        return sha2Hash512(passwordWithSalt);
    }
    
    private String sha2Hash512(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not available", e);
        }
    }

    public PasswordDetails makePassword(String rawPassword) {
        String saltValue = generateSaltValue();
        String hashedPassword = hashPassword(saltValue, rawPassword);
        return new PasswordDetails(saltValue, hashedPassword);
    }

    private String generateSaltValue() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (characters.length() * Math.random());
            salt.append(characters.charAt(index));
        }
        return salt.toString();
    }

    public boolean isPasswordCorrect(String rawPassword, PasswordDetails storedPasswordDetails) {
        String enteredPasswordHash = hashPassword(storedPasswordDetails.getSaltValue(), rawPassword);
    	return enteredPasswordHash.equals(storedPasswordDetails.getHashedPassword());
    }

    public static class PasswordDetails {
    	String saltValue = "";
    	String hashedPassword = "";
    	
		PasswordDetails(String saltValue, String hashedPassword) {
			this.saltValue = saltValue;
			this.hashedPassword = hashedPassword;
		}
		
		public String getSaltValue() {
			return saltValue;
		}
		
		public void setSaltValue(String saltValue) {
			this.saltValue = saltValue;
		}
		
		public String getHashedPassword() {
			return hashedPassword;
		}
		
		public void setHashedPassword(String hashedPassword) {
			this.hashedPassword = hashedPassword;
		}
    }
}
