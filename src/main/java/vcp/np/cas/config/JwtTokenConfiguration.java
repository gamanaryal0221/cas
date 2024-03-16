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

import vcp.np.cas.services.JwtTokenService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.enums.JwtTokenPurpose;

public class JwtTokenConfiguration {

    private static Map<String, Object> expirationFromConfig = null;

    public static JwtTokenService configure(Map<String, Object> jwtTokenConfig) throws Exception {
        System.out.println("\n############# Initializing JWT token service #############");

        KeyPair keyPair;
        Map<String, Long> expirationMap = new HashMap<String, Long>();
        // System.out.println("jwtTokenConfig:" + jwtTokenConfig);
            
        // String _privateKey = (String) jwtTokenConfig.getOrDefault(Config.PRIVATE_KEY, "");
        String _privateKey = "-----BEGIN PRIVATE KEY-----\r\n" + //
                        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDFdHYZPkRkhLng\r\n" + //
                        "sBrxxjfvsGHhQ3CuNL50CmYnlx5hAec6o5RT7x47ARnaoRmWpA9NrsYsFGMO4YSy\r\n" + //
                        "R3t9ZVb4V1LrKEltguKixsRoa53r3z41xfhCzpW6kA9RjD6T8xvI3d84q/tICfgc\r\n" + //
                        "jZ1BZK/z/8Y3yr/KG60thID8fVk7TWqUgCsKhy2xnrY5r+OK71/wa2oRszsqWp9i\r\n" + //
                        "WtPTHVGcqIxSaQ88BYTGidx17wy/C1QzLfj0G3AhBI0iDNt5laI1+vIdYZiFpfvx\r\n" + //
                        "jFp6qjDLCNmVQVbLF3opSBxxMx779VpdFCaYu+5YtlAH8zrQEyFzt1PW+Jey2aem\r\n" + //
                        "0hXbgd0pAgMBAAECggEAAjzhaMRUpJn/fE0qpQJU2HXUiifIQ2UsImcKxNiY2ssZ\r\n" + //
                        "eofnkrLzUgJfb2OjhVpLJRt9ufqqK71BEn8uZlurammc8jbU7DWPSX58s91CXy26\r\n" + //
                        "yAO8Hk+2kTR4Q/mVrPoUnshq8vq6AJVCV1qAhYD1YIvNzIyS82CwbANkLdhi1kHz\r\n" + //
                        "gZ6d+u1gmwSUxl9qmeyLd2eTofAy3koHiDvM+8Ni/rDNOc1mtMYNkhSDM0ZvjycR\r\n" + //
                        "zE+xw2bcgLsB42b/541Poe5ExfT/DDddMxeqScrrvRf4gW4bw7jgHH56ifgLvPyc\r\n" + //
                        "sdw/9iQFseXEYwusqfKCtiDngMu2wKMg7BMKIRqGIQKBgQDovHFiOIFHtHqGb68u\r\n" + //
                        "KLd4TwzdgZS/gINtH1fTf55QyaCycR96skM6gAJKF2Ga7mi7JDQRVMJWMEcenk5W\r\n" + //
                        "DtqgXUSnTfrMIWl9dvxNCcvnNUIZpM94nI/na0oREqAmclTijCQuadTuNDAo0TPG\r\n" + //
                        "BAHP8YtGmhfJYpnOxPg37f7wmQKBgQDZMTKCUjOOIqU/vuhVGtKGXbbFQVXIRPmj\r\n" + //
                        "ZMKlfCALkYc70LMhiDRtjstfxtpcrHyzo4eJUsoZIhwWZcwHmKtC56ckj4utCjkT\r\n" + //
                        "j7hVUW6E3MqX0ecStyQbyqC3B9ySVUoRHY7r5i8BjIow7dR+qg7Z3JtVSj7L879w\r\n" + //
                        "YPWg32nbEQKBgQDouM50FeaIShse5Pz6HjLf9cP6lwGjwKEq8+WBkqI/TG8JH/8H\r\n" + //
                        "VMvO6oPfjKPIJc/KGjiKgAPX5WyoXEoe2bmaE8l7tokrp9lxYRKHAl3Hc8UGGXxw\r\n" + //
                        "sbVgBjPJAKPt7fOdmd7wHlmSW9MwbYjk38rkITdQwPM6KAN+9Az0+GIuOQKBgER8\r\n" + //
                        "6QqDVVKKxkU1tBmyHZTVVF8bXSuL5JSLn32DiK9dMqHAC9yVEMNDdgo7sHvswF39\r\n" + //
                        "QS4idyw5v1WowA/dKpQsbF5xDYCBasIqXdw5k3o+DXyWaiFSswY4fTl6NynFz1da\r\n" + //
                        "VMk6irQYVzNaq9lNUuWMVUct5GN393Q+fvY9vSmhAoGAP8BmKs5Q079oRp9a3Bcf\r\n" + //
                        "Hsi+b9cgk9w1GNoYQVQfy/ZzP8WCsFVFukXgzd0aNWYQ26TnjZ70AlSDjP++QzRg\r\n" + //
                        "2iubi/O+QNSdZMcO8y7gTYiqVUrCNqQ+KSEueDnH2mYQgeQLUQRFvCx9S6B4B5t3\r\n" + //
                        "IeAGM+5cse5Sy9CR1UnDnl8=\r\n" + //
                        "-----END PRIVATE KEY-----\r\n" + //
                        "";

        if (_privateKey.isEmpty()) {
            throw new Exception("Not found: private key");
        }

        // String _publicKey = (String) jwtTokenConfig.getOrDefault(Config.PUBLIC_KEY, "");
        String _publicKey = "-----BEGIN PUBLIC KEY-----\r\n" + //
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxXR2GT5EZIS54LAa8cY3\r\n" + //
                        "77Bh4UNwrjS+dApmJ5ceYQHnOqOUU+8eOwEZ2qEZlqQPTa7GLBRjDuGEskd7fWVW\r\n" + //
                        "+FdS6yhJbYLiosbEaGud698+NcX4Qs6VupAPUYw+k/MbyN3fOKv7SAn4HI2dQWSv\r\n" + //
                        "8//GN8q/yhutLYSA/H1ZO01qlIArCoctsZ62Oa/jiu9f8GtqEbM7KlqfYlrT0x1R\r\n" + //
                        "nKiMUmkPPAWExoncde8MvwtUMy349BtwIQSNIgzbeZWiNfryHWGYhaX78Yxaeqow\r\n" + //
                        "ywjZlUFWyxd6KUgccTMe+/VaXRQmmLvuWLZQB/M60BMhc7dT1viXstmnptIV24Hd\r\n" + //
                        "KQIDAQAB\r\n" + //
                        "-----END PUBLIC KEY-----\r\n" + //
                        "";
        if (_publicKey.isEmpty()) {
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
            
        
        expirationFromConfig = (Map<String, Object>) jwtTokenConfig.getOrDefault(Constants.Config.EXPIRATION, new HashMap<String, Object>());
            
        String loginSuccessCode = JwtTokenPurpose.LOGIN_SUCCESSFUL.getCode();
        expirationMap.put(loginSuccessCode, fetchExpirationPeriod(loginSuccessCode, Constants.Default.LOGIN_SUCCESS_JWT_EXPIRATION_PERIOD));
            
        String changePasswordCode = JwtTokenPurpose.CHANGE_PASSWORD.getCode();
        expirationMap.put(changePasswordCode, fetchExpirationPeriod(changePasswordCode, Constants.Default.CHANGE_PASSWORD_JWT_EXPIRATION_PERIOD));
            
        String passwordResetCode = JwtTokenPurpose.PASSWORD_RESET.getCode();
        expirationMap.put(passwordResetCode, fetchExpirationPeriod(passwordResetCode, Constants.Default.PASSWORD_RESET_JWT_EXPIRATION_PERIOD));

        System.out.println("\n");
                    
        return new JwtTokenService(keyPair, expirationMap);
    }
    

    private static Long fetchExpirationPeriod(String code, Long defaultExpirationPeriod) {
        Long expirationPeriod = 0l;

        if (expirationFromConfig != null) {

            try {

                if (expirationFromConfig.containsKey(code)) {

                    Object object = expirationFromConfig.get(code);
                    if (object != null) {

                        String value = String.valueOf(object);
                        if (!value.isEmpty()){
                            expirationPeriod = Long.parseLong(value);
                        }

                    }

                }
                
            }catch(Exception e) {
                e.printStackTrace();
            }

        }else {
            System.out.println("Default: code:" + code + " - expiration period:" + defaultExpirationPeriod);
            return defaultExpirationPeriod;
        }

        if (expirationPeriod == 0l) {
            System.out.println("Default expiration: code:" + code + " - expiration period:" + defaultExpirationPeriod);
            return defaultExpirationPeriod;
        }else {
            System.out.println("Expiration: code:" + code + " - expiration period:" + expirationPeriod);
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
