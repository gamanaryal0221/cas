package vcp.np.cas.cas.password.reset;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import vcp.np.cas.cas.password.PasswordService;
import vcp.np.datasource.usermanagement.domains.ClientService;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.services.JwtTokenService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.Error;
import vcp.np.cas.utils.Constants.JwtToken;
import vcp.np.cas.utils.Helper;
import vcp.np.cas.utils.enums.JwtTokenPurpose;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/password/reset")
public class ResetPasswordController {

    
	@Autowired
	private CommonService commonService;

	@Autowired
	private PasswordService passwordService;

	private final JwtTokenService jwtTokenService;
	

    public ResetPasswordController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }
  
	
	@GetMapping
    public String showResetPasswordPage(HttpServletRequest request, Model model) {
    	
    	try {
    		
    		String jwtToken = request.getParameter(Constants.JwtToken.KEY);
    		System.out.println("jwtToken: " + jwtToken);
        	if (jwtToken == null || jwtToken.isEmpty()) {
        		model.addAllAttributes(Helper.error(Error.Title.MALFORMED_URL, Error.Message.TRY_WITH_VALID_URL));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
        	}
        	
        	Claims claims = jwtTokenService.parseToken(List.of(JwtTokenPurpose.CHANGE_PASSWORD.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()), jwtToken);

    		System.out.println("claims: " + claims);
    		if (claims == null) {
        		model.addAllAttributes(Helper.error(Error.Title.FORBIDDEN, Error.Message.REQUEST_IS_INVALID));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
    		}
    		    		
    		String purpose = (String) claims.get(JwtToken.PURPOSE);
			String title = "";
    		String description = "<pstyle=\"font-size: small; text-align: left;\"><span style=\"font-weight: bold; color: red;\">Expire after: </span><b>" + claims.getExpiration().toLocaleString() + "</b>.</p>";
    		if (JwtTokenPurpose.CHANGE_PASSWORD.getCode().equals(purpose)) {
				title = "Change Password";
    			description = ""
    					+ "Your password has been expired since it was last changed <b>" + claims.get(JwtToken.DAYS_SINCE_LAST_PASSWORD_CHANGE) + "</b> day(s) earlier.<br>"
    					+ "Please change your password to proceed.<br><br>" + description;
    		}else {
				title = "Reset Password";
    			description = "Please create a new password.<br><br>" + description;
    		}
			model.addAttribute(Constants.PAGE_TITLE, title);
			model.addAttribute(Constants.PAGE_DESCRIPTION, description);

    		
    		String requestHost = (String) claims.get(JwtToken.REQUEST_HOST);
    		String hostUrl = (String) claims.get(JwtToken.HOST_URL);
    		System.out.println("hostUrl:'" + hostUrl + "'");
    		model.addAttribute(Constants.HOST_URL, hostUrl);
    		
    		ClientService clientService = (requestHost != null && !requestHost.isEmpty())? commonService.getClientServiceDetail(requestHost):null;
    		if (clientService == null) clientService = (hostUrl != null && !hostUrl.isEmpty())? commonService.getClientServiceDetail(Helper.parseUrl(hostUrl)):null;
    		
    		if (clientService == null) {
        		model.addAllAttributes(Helper.error(Error.Title.MALFORMED_URL, Error.Message.TRY_WITH_VALID_URL));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
    		}else {
    			model.addAttribute(Constants.JwtToken.KEY, jwtToken);
    			model.addAttribute(Constants.CLIENT_DISPLAY_NAME, clientService.getClient().getDisplayName());
        		model.addAllAttributes(commonService.getClientServiceTheme(clientService.getClient().getId(), clientService.getService().getId()));
                return Constants.Templates.PASSWORD_RESET;
    		}

    	}catch(Exception ex) {
    		ex.printStackTrace();
    		
    		model.addAllAttributes(Helper.error(Constants.Error.Title.TECHNICAL_ERROR, Constants.Error.Message.SMTH_WENT_WRONG));
    		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
    		return Constants.Templates.ERROR;
    	}
    	
    }


	@PostMapping
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestParam String jwtToken, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
    	
    	try {
    		
    		System.out.println("jwtToken: " + jwtToken);
        	if (jwtToken == null || jwtToken.isEmpty()) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.COULD_NOT_PROCESS);
        	}
    		
        	Claims claims = jwtTokenService.parseToken(List.of(JwtTokenPurpose.CHANGE_PASSWORD.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()), jwtToken);

    		System.out.println("claims: " + claims);
    		if (claims == null) {
        		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.Title.FORBIDDEN);
    		}
    		
    		String requestHost = (String) claims.get(JwtToken.REQUEST_HOST);
    		String hostUrl = (String) claims.get(JwtToken.HOST_URL);
    		
    		ClientService clientService = (requestHost != null && !requestHost.isEmpty())? commonService.getClientServiceDetail(requestHost):null;
    		if (clientService == null) clientService = (hostUrl != null && !hostUrl.isEmpty())? commonService.getClientServiceDetail(Helper.parseUrl(hostUrl)):null;
    		
    		if (clientService == null) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.REQUEST_IS_INVALID);
    		}else {
    			
    			if (newPassword == null || confirmPassword == null || newPassword.isEmpty() || confirmPassword.isEmpty()) {
    				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("All fields are mandatory");
    			}else if (!newPassword.equals(confirmPassword)) {
    	            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Passwords do not match");
    	        }else {
    	        	
    	        	Map<String, Object> passwordResetData = passwordService.resetPassword(claims, clientService.getId(), newPassword);

    	            if ((boolean) passwordResetData.getOrDefault(Constants.IS_PASSWORD_RESET_SUCCESSFUL, false)) {
        	            return ResponseEntity.ok("Password reset success !!!");
    	            }else {
    	            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((String) passwordResetData.getOrDefault(Error.Message.KEY, Error.Message.SMTH_WENT_WRONG));
    	            }

    	        }
    			
    		}

    	}catch(Exception ex) {
    		ex.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.Message.SMTH_WENT_WRONG);
    	}
    	
    }
    
}
