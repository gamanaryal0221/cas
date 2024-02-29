package vcp.np.cas.interceptors;

import java.net.URL;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vcp.np.cas.domains.ClientService;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Helper;

public class RequestInterceptor implements HandlerInterceptor{
	
	
	private final CommonService commonService;
	
	
	public RequestInterceptor(CommonService commonService) {
		super();
		this.commonService = commonService;
	}

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // This method is called before the actual handler method is invoked
		
		System.out.println("Request received [handler: " + handler + "]");
		String requestUri = request.getRequestURI();
		String requestMethod = request.getMethod();
        String hostUrl = request.getParameter(Constants.HOST_URL);
        System.out.println("Request[method: " + requestMethod + ", uri: " + requestUri + ", hostUrl: " + hostUrl + "]");
        
        if (Constants.Request.Uri.PASSWORD_RESET.equalsIgnoreCase(requestUri)) {
        	return true;
        }
        
		boolean isGenuineRequest = false;

    	ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(Constants.HOST_URL, hostUrl);

        try {
        	
        	if (hostUrl == null || hostUrl.isEmpty()) {
    			modelAndView.addAllObjects(commonService.getClientServiceTheme(null, null));
        		modelAndView.addAllObjects(Helper.error(Constants.Error.Title.MALFORMED_URL, Constants.Error.Message.TRY_WITH_VALID_URL));
            }else {
        		        		
        		URL url = Helper.parseUrl(hostUrl);
                ClientService clientService = (url != null)? commonService.getClientServiceDetail(url):null;
                modelAndView.addObject(Constants.CLIENT_DISPLAY_NAME, (clientService != null) ? clientService.getClient().getDisplayName() : null);
        		
        		if (clientService == null) {
            		modelAndView.addAllObjects(Helper.error(Constants.Error.Title.MALFORMED_URL, Constants.Error.Message.TRY_WITH_VALID_URL));
        			modelAndView.addAllObjects(commonService.getClientServiceTheme(null, null));
        		}else {

        			isGenuineRequest = true;
        			modelAndView.addObject(Constants.CLIENTSERVICE_ID, clientService.getId());
        			modelAndView.addObject(Constants.CLIENT_ID, clientService.getClient().getId());
        			modelAndView.addObject(Constants.SERVICE_ID, clientService.getService().getId());
        			
        			modelAndView.addAllObjects(commonService.getClientServiceTheme(clientService.getClient().getId(), clientService.getService().getId()));
        		
        		}
            	
            }
        	
        }catch (Exception ex) {
        	ex.printStackTrace();
        	
    		modelAndView.addAllObjects(Helper.error(null, null));
			modelAndView.addAllObjects(commonService.getClientServiceTheme(null, null));
        }
    	

        request.setAttribute(Constants.Request.Is_GENUINE, isGenuineRequest);
        request.setAttribute(Constants.MODEL_AND_VIEW, modelAndView);
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // This method is called after the handler method is invoked but before the view is rendered
        // You can perform post-processing here
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // This method is called after the view is rendered
        // You can perform cleanup or additional tasks here
    }

}
