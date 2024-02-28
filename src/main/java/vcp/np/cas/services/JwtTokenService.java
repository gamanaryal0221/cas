package vcp.np.cas.services;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import vcp.np.cas.domains.User;
import vcp.np.cas.domains.UserClientService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.enums.JwtTokenPurpose;


public class JwtTokenService {
	
	private String issuer = "";
    private KeyPair keyPair;
    
    private Map<String, Long> expirationMap = new HashMap<String, Long>();
    
    
    public JwtTokenService(String issuer, String _privateKey, String _publicKey) throws Exception {
    	System.out.println("############# Initializing JWT token service #############");
    	
    	if (_privateKey == null) throw new Exception("Received null private key");
    	if (_privateKey.isEmpty()) throw new Exception("Received empty private key");
    	if (_publicKey == null) throw new Exception("Received null public key");
    	if (_publicKey.isEmpty()) throw new Exception("Received empty public key");
    	
    	try {
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
            if (keyPair == null) throw new Exception("Could not create key-pair");
            
            
            expirationMap.put(JwtTokenPurpose.LOGIN_SUCCESSFUL.getCode(), (3600l * 1000));
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
	}
    

    private static String stripHeaders(String key) {
        return key.replaceAll("-----BEGIN (.*)-----", "").replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "").replaceAll("\n", "");
    }

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
	

	public String generateToken(JwtTokenPurpose jwtTokenPurpose, UserClientService userClientService) {
		if (userClientService != null) {
			
			try {
				
				Map<String, Object> jwtMap = getJwtMap(jwtTokenPurpose, userClientService);
				
				PrivateKey privateKey = keyPair.getPrivate();

		        JwtBuilder builder = Jwts.builder()
		                .setIssuer(issuer)
		                .setSubject(userClientService.getUser().getUsername())
		                .setIssuedAt(Date.from(Instant.now()))
		                .setExpiration(new Date(System.currentTimeMillis() + expirationMap.get(jwtTokenPurpose.getCode())))
		                .signWith(privateKey, SignatureAlgorithm.RS256)
		                .addClaims(jwtMap);

		        return builder.compact();
				
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Could not generate jwt token");
				return null;
			}
			
		}else {
			System.out.println("Recived null data to generate jwt token");
			return null;
		}
    }


	private Map<String, Object> getJwtMap(JwtTokenPurpose jwtTokenPurpose, UserClientService userClientService) {
		Map<String, Object> jwtMap = new HashMap<String, Object>();

        jwtMap.put(Constants.JwtToken.PURPOSE, jwtTokenPurpose.getCode());
		
		User user = userClientService.getUser();
		jwtMap.put(Constants.JwtToken.FIRST_NAME, user.getFirstName());
		jwtMap.put(Constants.JwtToken.MIDDLE_NAME, user.getMiddleName());
		jwtMap.put(Constants.JwtToken.LAST_NAME, user.getLastName());
		
		jwtMap.put(Constants.JwtToken.CLIENT_DISPLAY_NAME, userClientService.getClient().getDisplayName());
		jwtMap.put(Constants.JwtToken.REQUEST_HOST, userClientService.getClientService().getRequestHost());
        
		return jwtMap;
	}

}
