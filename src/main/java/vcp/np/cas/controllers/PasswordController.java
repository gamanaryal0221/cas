package vcp.np.cas.controllers;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vcp.np.cas.domains.ClientService;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.services.JwtTokenService;
import vcp.np.cas.services.PasswordService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.JwtToken;
import vcp.np.cas.utils.Constants.Error;
import vcp.np.cas.utils.Helper;
import vcp.np.cas.utils.enums.JwtTokenPurpose;

@Controller
@RequestMapping("/password")
public class PasswordController {
	

	@Autowired
	private CommonService commonService;

	@Autowired
	private JwtTokenService jwtTokenService;
	
	@Autowired
	private PasswordService passwordService;
	

    @GetMapping("/forgot")
    public String showForgotPasswordPage(HttpServletRequest request, Model model) {
    	
        model.addAllAttributes(Helper.getAllModelAndView(request).getModel());
        
        if (!Helper.isGenuineRequest(request)) {
        	return Constants.Templates.ERROR;
        }
        
        return Constants.Templates.FORGOT_PASSWORD;
    }
   

    @GetMapping("/reset")
    public String showResetPasswordPage(HttpServletRequest request, Model model) {
    	
    	try {
    		
    		String jwtToken = request.getParameter(Constants.JwtToken.KEY);
    		System.out.println("jwtToken: " + jwtToken);
        	if (jwtToken == null || jwtToken.isEmpty()) {
        		model.addAllAttributes(Helper.error(Error.Title.MALFORMED_URL, Error.Message.TRY_WITH_VALID_URL));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
        	}
        	
        	Claims claims = jwtTokenService.parseToken(List.of(JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()), jwtToken);
    		System.out.println("claims: " + claims);
    		if (claims == null) {
        		model.addAllAttributes(Helper.error(Error.Title.FORBIDDEN, Error.Message.REQUEST_IS_INVALID));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
    		}
    		    		
    		String purpose = (String) claims.get(JwtToken.PURPOSE);
    		String description = "<pstyle=\"font-size: small; text-align: left;\"><span style=\"font-weight: bold; color: red;\">Portal validity:</span> till <b>" + claims.getExpiration().toLocaleString() + "</b>.</p>";
    		if (JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode().equals(purpose)) {
    			description = ""
    					+ "Your password has been expired since it was last changed <b>" + claims.get(JwtToken.DAYS_SINCE_LAST_PASSWORD_CHANGE) + "</b> day(s) earlier.<br>"
    					+ "Please change your password to proceed.<br><br>" + description;
    		}else {
    			description = "Please create a new password.<br><br>" + description;
    		}
			model.addAttribute(Constants.PAGE_DESCRIPTION, description);

    		
    		String requestHost = (String) claims.get(JwtToken.REQUEST_HOST);
    		String hostUrl = (String) claims.get(JwtToken.HOST_URL);
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
    

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestParam String jwtToken, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
    	
    	try {
    		
    		System.out.println("jwtToken: " + jwtToken);
        	if (jwtToken == null || jwtToken.isEmpty()) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.COULD_NOT_PROCESS);
        	}
    		
        	Claims claims = jwtTokenService.parseToken(List.of(JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()), jwtToken);
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
        	            return ResponseEntity.ok("Passwords reset success !!!");
    	            }else {
    	            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((String) passwordResetData.getOrDefault(Constants.JwtToken.KEY, Error.Message.SMTH_WENT_WRONG));
    	            }

    	        }
    			
    		}

    	}catch(Exception ex) {
    		ex.printStackTrace();

    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.SMTH_WENT_WRONG);
    	}
    	
    	
    }
    

    @PostMapping("/forgot")
    public String forgotPassword(HttpServletRequest request, @RequestParam String username, Model model, HttpServletResponse httpServletResponse) {
		return null;
    }


}
