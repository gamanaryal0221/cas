package vcp.np.cas.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.cas.domains.User;
import vcp.np.cas.repositories.custom.CustomQueries;

@Service
public class AuthenticationService {


	@Autowired
	public CustomQueries customQueries;
	

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
    
    public boolean doesCredentialMatch(User user, String rawPassword) {
    	boolean isAuthenticationSuccessful = false;
    	
    	if (user != null && rawPassword != null && !rawPassword.isEmpty()) {
    		
    		PasswordDetails passwordDetails = new PasswordDetails(user.getSaltValue(), user.getPassword());
    		isAuthenticationSuccessful = isPasswordCorrect(rawPassword, passwordDetails);
    		
    	}
    	System.out.println("Is credential matched for user[id:" + user.getId() + "]?\n >> "+ isAuthenticationSuccessful);
		return isAuthenticationSuccessful;
    	
    }
    
    public boolean doesItMatchWithOldPassword(User user, String rawPassword) {
    	System.out.println("Checking if the password is in the user[id: " + user.getId() + "]'s password history ...");
		String enteredPasswordHash = hashPassword(user.getSaltValue(), rawPassword);
		if (enteredPasswordHash.equals(user.getPassword())) {
	    	System.out.println("User[id: " + user.getId() + "] provided the current password to reset");
			return true;
		}else {
			return customQueries.isItInUserPasswordHistory(user.getId(), rawPassword);
		}
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
