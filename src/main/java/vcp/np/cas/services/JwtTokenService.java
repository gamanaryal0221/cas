package vcp.np.cas.services;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.JwtToken;
import vcp.np.cas.utils.Helper;
import vcp.np.cas.utils.StringUtils;
import vcp.np.cas.utils.enums.JwtTokenPurpose;
import vcp.np.datasource.usermanagement.domains.User;
import vcp.np.datasource.usermanagement.domains.UserClientService;


public class JwtTokenService {
	
    private KeyPair keyPair;
    private Map<String, Long> expirationMap = new HashMap<String, Long>();

	@Autowired
	public PlainSqlQueries plainSqlQueries;
    
    
    public JwtTokenService(KeyPair keyPair, Map<String, Long> expirationMap) throws Exception {
    	this.keyPair = keyPair;
    	this.expirationMap = expirationMap;

	}
	

	public Map<String, Object> generateToken(JwtTokenPurpose jwtTokenPurpose, StringBuffer fullCasUrl, String hostUrl, UserClientService userClientService, Map<String, Object> extraData) {
		Map<String, Object> jwtMap = new HashMap<String, Object>();

		if (userClientService != null) {
			
			String log = "jwt token[purpose: '" + jwtTokenPurpose.getCode() + "'] for user[id: " + userClientService.getUser().getId() + "] on client-service[id: " + userClientService.getId() + "]";
			System.out.println("Generating " + log);
			
			try {
				
				String issuer = Helper.getCustomizedFullCasUrl(fullCasUrl, null, null);
				Map<String, Object> claims = makeJwtClaims(jwtTokenPurpose, hostUrl, userClientService, extraData);
				
				PrivateKey privateKey = keyPair.getPrivate();
				Date issuedAt = Date.from(Instant.now());
				Date expirationAt = new Date(System.currentTimeMillis() + expirationMap.get(jwtTokenPurpose.getCode()));

		        JwtBuilder builder = Jwts.builder()
		                .setIssuer(issuer)
		                .setSubject(String.valueOf(userClientService.getUser().getId()))
		                .setIssuedAt(issuedAt)
		                .setExpiration(expirationAt)
		                .signWith(privateKey, SignatureAlgorithm.RS256)
		                .addClaims(claims);

		        String jwtToken = builder.compact();
		        
		        if (jwtToken == null || jwtToken.isEmpty()) {
					System.out.println("Could not generate " + log);
		        	return null;
		        }else {
		        	
		        	jwtMap.put(JwtToken.KEY, jwtToken);
		        	jwtMap.put(JwtToken.ISSUED_AT, issuedAt);
		        	jwtMap.put(JwtToken.EXPIRATION_AT, expirationAt);
		        	
					System.out.println("Successfully generated " + log);
					System.out.println("jwtToken: " + jwtToken);
		        	return jwtMap;
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


	private Map<String, Object> makeJwtClaims(JwtTokenPurpose jwtTokenPurpose, String hostUrl, UserClientService userClientService, Map<String, Object> extraData) {
		Map<String, Object> jwtMap = new HashMap<String, Object>();

        jwtMap.put(JwtToken.PURPOSE, jwtTokenPurpose.getCode());
		
		jwtMap.put(JwtToken.REQUEST_HOST, userClientService.getClientService().getRequestHost());
		
		if(List.of(JwtTokenPurpose.CHANGE_PASSWORD.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()).contains(jwtTokenPurpose.getCode())) {
			if (hostUrl != null && !hostUrl.isEmpty()) {
				jwtMap.put(Constants.JwtToken.HOST_URL, hostUrl);
			}
		}

		if(JwtTokenPurpose.LOGIN_SUCCESSFUL == jwtTokenPurpose) {
			User user = userClientService.getUser();
			jwtMap.put(JwtToken.USERNAME, user.getUsername());
		
			jwtMap.put(JwtToken.FIRST_NAME, user.getFirstName());
			jwtMap.put(JwtToken.MIDDLE_NAME, user.getMiddleName());
			jwtMap.put(JwtToken.LAST_NAME, user.getLastName());
			
			jwtMap.put(JwtToken.MAIL_ADDRESS, user.getMailAddress());
			jwtMap.put(JwtToken.NUMBER, user.getNumber());
			
			jwtMap.put(JwtToken.CLIENT_ID, userClientService.getClient().getId());
			jwtMap.put(JwtToken.CLIENT_DISPLAY_NAME, userClientService.getClient().getDisplayName());
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
            
            
            // Checking if token is expired
            Date expirationDate = claims.getExpiration();
            if (expirationDate != null && expirationDate.before(new Date())) {
                throw new ExpiredJwtException(null, claims, "Token: " + jwtToken + " is expired");
            }
            
            
            // Checking if purpose of the token is valid
            String purpose = (String) claims.get(JwtToken.PURPOSE);
            if (purpose == null || purpose.isEmpty()) {
            	throw new InvalidJwtPurposeException("Invalid purpose of jwt token:" + jwtToken);
            }
            if (!possiblePurposeList.contains(purpose)) {
            	throw new InvalidJwtPurposeException("Token purpose did not meet requirement -> [expected: " + (StringUtils.listToString(possiblePurposeList, ", ")) + ", received: " + jwtToken + "]");
    		}

			// Checking if token has been already used to update password
			if(List.of(JwtTokenPurpose.CHANGE_PASSWORD.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()).contains(purpose)) {
				if (plainSqlQueries.isPasswordUpdatedAfterTokenIssue(Long.parseLong(claims.getSubject()), Helper.dateToTimestamp(claims.getIssuedAt()))) {
					throw new ExpiredJwtException(null, claims, "Token has been already used", null);
				}
			}
            
            return claims;
            
        } catch (ExpiredJwtException ex) {
        	ex.printStackTrace();
            System.out.println("Token is expired : " + jwtToken);
            return null;
            
        } catch (InvalidJwtPurposeException ex) {
        	ex.printStackTrace();
            System.out.println("Invalid jwt purpose of token: " + jwtToken);
            return null;
            
        } catch (JwtException ex) {
        	ex.printStackTrace();
            System.out.println("jwt token: " + jwtToken + " parsing error: " + ex.getMessage());
            return null;
            
        } catch (Exception ex) {
			ex.printStackTrace();
            System.out.println("Unexpected error while parsing jwt token: " + jwtToken + " -> " + ex.getMessage());
            return null;
            
        }
        
    }
	
	
	public class InvalidJwtPurposeException extends RuntimeException {

	    private static final long serialVersionUID = 1L;

	    public InvalidJwtPurposeException(String message) {
	        super(message);
	    }

	    public InvalidJwtPurposeException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
}
