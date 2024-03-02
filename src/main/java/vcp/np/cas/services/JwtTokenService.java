package vcp.np.cas.services;

import java.io.Serializable;
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
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import vcp.np.cas.domains.User;
import vcp.np.cas.domains.UserClientService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.JwtToken;
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
            expirationMap.put(JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode(), (10l * 60 * 1000l));
            expirationMap.put(JwtTokenPurpose.PASSWORD_RESET.getCode(), (1001l * 1000));
            
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
	

	public String generateToken(JwtTokenPurpose jwtTokenPurpose, String hostUrl, UserClientService userClientService, Map<String, Object> extraData) {
		if (userClientService != null) {
			
			String log = "jwt token[purpose: '" + jwtTokenPurpose.getCode() + "'] for user[id: " + userClientService.getUser().getId() + "] on client-service[id: " + userClientService.getId() + "]";
			System.out.println("Generating " + log);
			
			try {
				
				Map<String, Object> jwtMap = getJwtMap(jwtTokenPurpose, hostUrl, userClientService, extraData);
				
				PrivateKey privateKey = keyPair.getPrivate();

		        JwtBuilder builder = Jwts.builder()
		                .setIssuer(issuer)
		                .setSubject(userClientService.getUser().getUsername())
		                .setIssuedAt(Date.from(Instant.now()))
		                .setExpiration(new Date(System.currentTimeMillis() + expirationMap.get(jwtTokenPurpose.getCode())))
		                .signWith(privateKey, SignatureAlgorithm.RS256)
		                .addClaims(jwtMap);

		        String jwtToken = builder.compact();
		        
		        if (jwtToken == null || jwtToken.isEmpty()) {
					System.out.println("Could not generate " + log);
		        	return null;
		        }else {
					System.out.println("Successfully generated " + log);
					System.out.println("jwtToken: " + jwtToken);
		        	return jwtToken;
		        }
		        
				
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Could not generate " + log);
				return null;
			}
			
		}else {
			System.out.println("Recived null data to generate jwt token purpose: '" + jwtTokenPurpose.getCode() + "' on hostUrl: '" + hostUrl + "'");
			return null;
		}
    }


	private Map<String, Object> getJwtMap(JwtTokenPurpose jwtTokenPurpose, String hostUrl, UserClientService userClientService, Map<String, Object> extraData) {
		Map<String, Object> jwtMap = new HashMap<String, Object>();

        jwtMap.put(Constants.JwtToken.PURPOSE, jwtTokenPurpose.getCode());
		
		User user = userClientService.getUser();
		jwtMap.put(Constants.JwtToken.FIRST_NAME, user.getFirstName());
		jwtMap.put(Constants.JwtToken.MIDDLE_NAME, user.getMiddleName());
		jwtMap.put(Constants.JwtToken.LAST_NAME, user.getLastName());
		
		jwtMap.put(Constants.JwtToken.CLIENT_DISPLAY_NAME, userClientService.getClient().getDisplayName());
		
		jwtMap.put(Constants.JwtToken.REQUEST_HOST, userClientService.getClientService().getRequestHost());
		if (hostUrl != null && hostUrl.isEmpty()) {
			if(List.of(JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()).contains(jwtTokenPurpose.getCode())) {
				jwtMap.put(Constants.JwtToken.HOST_URL, hostUrl);
			}
		}
		
		if(extraData != null) jwtMap.putAll(extraData);
        
		return jwtMap;
	}

	
	public Claims parseToken(List<String> possiblePurposeList, String jwtToken) {
		System.out.println("Parsing jwt token: " + jwtToken);
        try {
            PublicKey publicKey = keyPair.getPublic();

            Claims claims =  Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            
            
            // Check if token is expired
            Date expirationDate = claims.getExpiration();
            if (expirationDate != null && expirationDate.before(new Date())) {
                throw new ExpiredJwtException(null, claims, "Token: " + jwtToken + " is expired");
            }
            
            
            // Check if purpose of the token is valid
            String purpose = (String) claims.get(JwtToken.PURPOSE);
            if (purpose == null || purpose.isEmpty()) {
            	throw new InvalidJwtPurposeException("Invalid JWT purpose of token: " + jwtToken);
            }
            if (!possiblePurposeList.contains(purpose)) {
            	throw new InvalidJwtPurposeException("JWT purpose did not meet -> token: " + jwtToken);
    		}
            
            
            return claims;
            
        } catch (ExpiredJwtException ex) {
        	ex.printStackTrace();
            System.out.println("JWT token is expired");
            return null;
            
        } catch (InvalidJwtPurposeException ex) {
        	ex.printStackTrace();
            System.out.println("Invalid JWT purpose of token: " + jwtToken);
            return null;
            
        } catch (JwtException jex) {
        	jex.printStackTrace();
            System.out.println("JWT token: " + jwtToken + " parsing error: " + jex.getMessage());
            return null;
            
        } catch (Exception ex) {
            System.out.println("Unexpected error while parsing JWT token: " + jwtToken + " -> " + ex.getMessage());
            return null;
            
        }
        
    }
	
	
	public class InvalidJwtPurposeException extends RuntimeException implements Serializable {

	    private static final long serialVersionUID = 1L;

	    public InvalidJwtPurposeException(String message) {
	        super(message);
	    }

	    public InvalidJwtPurposeException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
}
