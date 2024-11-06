package vcp.np.cas.config;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import vcp.np.cas.profile.Profile;
import vcp.np.cas.services.JwtTokenService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.enums.JwtTokenPurpose;

public class JwtTokenConfigLoader {

    private static final String JWT_BASE_KEY = "jwtToken.";
    private static final String JWT_EXPIRATION_BASE_KEY = JWT_BASE_KEY + "expiration.";


    public static JwtTokenService configure(Profile profile) throws Exception {
        System.out.println("\n:::::::::: Initializing JWT token service ::::::::::");

        KeyPair keyPair;
        Map<String, Long> expirationMap = new HashMap<String, Long>();
            
        String _privateKey = profile.getProperty(JWT_BASE_KEY + "privateKey", "");
        if (_privateKey.isEmpty()) {
            // System.out.println("private key is empty");
            throw new Exception("Not found: private key");
        }

        String _publicKey = profile.getProperty(JWT_BASE_KEY + "publicKey", "");
        if (_publicKey.isEmpty()) {
            // System.out.println("public key is empty");
            throw new Exception("Not found: public key");
        }

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // Decode private key

        byte[] privateKeyBytes = Base64.getDecoder().decode(stripHeaders(_privateKey));
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        // Decode public key
        byte[] publicKeyBytes = Base64.getDecoder().decode(stripHeaders(_publicKey));
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        keyPair = new KeyPair(publicKey, privateKey);
            
        
            
        String loginSuccessCode = JwtTokenPurpose.LOGIN_SUCCESSFUL.getCode();
        expirationMap.put(loginSuccessCode, fetchExpirationPeriod(loginSuccessCode, Constants.Default.LOGIN_SUCCESS_JWT_EXPIRATION_PERIOD, profile));
            
        String changePasswordCode = JwtTokenPurpose.CHANGE_PASSWORD.getCode();
        expirationMap.put(changePasswordCode, fetchExpirationPeriod(changePasswordCode, Constants.Default.CHANGE_PASSWORD_JWT_EXPIRATION_PERIOD, profile));
            
        String passwordResetCode = JwtTokenPurpose.PASSWORD_RESET.getCode();
        expirationMap.put(passwordResetCode, fetchExpirationPeriod(passwordResetCode, Constants.Default.PASSWORD_RESET_JWT_EXPIRATION_PERIOD, profile));
                    
        return new JwtTokenService(keyPair, expirationMap);
    }
    

    private static Long fetchExpirationPeriod(String code, Long defaultExpirationPeriod, Profile profile) {
        Long expirationPeriod = null;

        try {
            String expirationPeriodInString = profile.getProperty(JWT_EXPIRATION_BASE_KEY + code, "");
            if (!expirationPeriodInString.isEmpty()) {
                expirationPeriod = Long.parseLong(expirationPeriodInString);
            }
        }catch(Exception e) {
            expirationPeriod = null;
            e.printStackTrace();
        }

        if (expirationPeriod == null){
            System.out.println("Configuring default -> " + JWT_EXPIRATION_BASE_KEY + code + ":" + defaultExpirationPeriod);
            return defaultExpirationPeriod;
        }else {
            return expirationPeriod;
        }
    }


    private static String stripHeaders(String key) {
        return key.replaceAll("-----BEGIN (.*)-----", "")
            .replaceAll("-----END (.*)----", "")
            .replaceAll("\r\n", "")
            .replaceAll("\n", "");
    }
}
