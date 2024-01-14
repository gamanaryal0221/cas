package vcp.np.cas.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.services.LoginService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.domains.ClientService;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private CommonService commonService;

    @Autowired
    private LoginService loginService;
    

    @GetMapping
    public String showLoginPage(HttpServletRequest request, Model model) {
    	System.out.println("Browsed login page");
    	System.out.println(request);
    	
    	ClientService clientService = commonService.getClientServiceDetail(request);
    	System.out.println("clientService: " + clientService);
    	

		Map<String, String> clientServiceThemeMap = commonService.getClientServiceTheme(
				(clientService != null)? clientService.getClient().getId():null,
						(clientService != null)? clientService.getService().getId():null
								);
    	
    	model.addAttribute(Constants.LOGO_URL, clientServiceThemeMap.getOrDefault(Constants.LOGO_URL, null));
    	model.addAttribute(Constants.BACKGROUND_IMAGE_URL, clientServiceThemeMap.getOrDefault(Constants.BACKGROUND_IMAGE_URL, null));
    	model.addAttribute(Constants.BACKGROUND_COLOR_CODE, clientServiceThemeMap.getOrDefault(Constants.BACKGROUND_COLOR_CODE, null));
    	
    	
    	if (clientService != null) {
    		model.addAttribute(Constants.CLIENT_DISPLAY_NAME, clientService.getClient().getDisplayName());
    		
        	return "login";
        	
    	}else {
    		model.addAttribute(Constants.ERROR_TITLE, "Malformed URL");
    		model.addAttribute(Constants.ERROR_MESSAGE, "Please check the service URL and try again.");
    		
        	return "error";
    	}
    	
    }
}
