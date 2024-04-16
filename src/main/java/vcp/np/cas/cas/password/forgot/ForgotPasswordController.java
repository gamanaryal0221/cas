package vcp.np.cas.cas.password.forgot;


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
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vcp.np.cas.cas.password.PasswordService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.Error;
import vcp.np.cas.utils.Helper;

@Controller
@RequestMapping("/password/forgot")
public class ForgotPasswordController {
    
	@Autowired
	private PasswordService passwordService;

    @GetMapping
    public String showForgotPasswordPage(HttpServletRequest request, Model model) {
    	
        model.addAllAttributes(Helper.getAllModelAndView(request).getModel());
        
        if (!Helper.isGenuineRequest(request)) {
        	return Constants.Templates.ERROR;
        }
        
        return Constants.Templates.FORGOT_PASSWORD;
    }

    
    @PostMapping
    public ResponseEntity<String> forgotPassword(HttpServletRequest request, @RequestParam String username, Model model, HttpServletResponse httpServletResponse) {
    	
        ModelAndView modelAndView = Helper.getAllModelAndView(request);
        Map<String, Object> modelAndViewMap = modelAndView.getModel();
        model.addAllAttributes(modelAndViewMap);
    	
    	if (!Helper.isGenuineRequest(request)) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.REQUEST_IS_INVALID);
        }
    	
        model.addAttribute(Constants.USERNAME, username);
        
        String hostUrl = (String) modelAndViewMap.getOrDefault(Constants.HOST_URL, "");
        Long clientId = (Long) modelAndViewMap.get(Constants.CLIENT_ID);
        Long serviceId = (Long) modelAndViewMap.get(Constants.SERVICE_ID);
        Long clientServiceId = (Long) modelAndViewMap.get(Constants.CLIENTSERVICE_ID);
        
        if (clientId == null || serviceId == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.REQUEST_IS_INVALID);
        }
                
        try {

            Map<String, Object> forgotPasswordData = passwordService.forgotPassword(request.getRequestURL(), hostUrl, clientServiceId, username);

            if ((boolean) forgotPasswordData.getOrDefault(Constants.IS_FORGOT_PASSWORD_REQUEST_SUCCESSFUL, false)) {
	            return ResponseEntity.ok("Password rest link has been sent to respective mail address.");
            }else {
            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((String) forgotPasswordData.getOrDefault(Error.Message.KEY, Error.Message.SMTH_WENT_WRONG));
            }
            
        }catch(Exception e) {
        	e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.Message.SMTH_WENT_WRONG);
        }
    	
    }

}
