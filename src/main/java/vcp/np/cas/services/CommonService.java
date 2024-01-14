package vcp.np.cas.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import vcp.np.cas.domains.ClientService;
import vcp.np.cas.domains.ClientServiceTheme;
import vcp.np.cas.repositories.ClientServiceRepository;
import vcp.np.cas.repositories.ClientServiceThemeRepository;
import vcp.np.cas.repositories.UserRepository;
import vcp.np.cas.utils.Constants;

@Service
public class CommonService {
	
	@Autowired
	public UserRepository userRepository;

	@Autowired
	public ClientServiceRepository clientServiceRepository;

	@Autowired
	public ClientServiceThemeRepository clientServiceThemeRepository;
	
	public ClientService getClientServiceDetail(HttpServletRequest request) {
	    System.out.println("Fetching client-service detail...");
	    System.out.println(request);
	    ClientService clientService = null;

	    if (request != null) {

	        try {
	            String hostUrl = request.getParameter(Constants.HOST_URL);
	            System.out.println("hostUrl: " + hostUrl);

	            if (hostUrl != null && !hostUrl.isEmpty()) {
	                URL url = new URL(hostUrl);

	                String requestHost = url.getHost();
	                System.out.println("requestHost: " + requestHost);

	                Optional<ClientService> optionalClientService = clientServiceRepository.findByRequestHost(requestHost);
	                clientService = optionalClientService.orElse(null);
	            	System.out.println("clientService: " + clientService);

	                if (clientService == null) {
	                    System.out.println("Could not find requestHost: " + requestHost);
	                }

	            } else {
	                System.out.println("Host URL is empty or null.");
	            }

	        } catch (MalformedURLException e) {
	            System.out.println("Malformed hostUrl: " + e.getMessage());
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Error occurred in getClientServiceDetail: " + e.getMessage());
	        }

	    } else {
	        System.out.println("Request is null.");
	    }

	    return clientService;
	}
	
	public Map<String, String> getClientServiceTheme(Long clientId, Long serviceId) {
	    System.out.println("Fetching client-service theme [clientId:" + clientId + ",serviceId:" + serviceId + "]...");
		Map<String, String> clientServiceThemeMap = new HashMap<String, String>();
		
		if (clientId != null && serviceId != null) {
			List<ClientServiceTheme> clientServiceThemeList = clientServiceThemeRepository.findAllByClientId(clientId);
			
			if (clientServiceThemeList != null && !clientServiceThemeList.isEmpty()) {
				
				Optional<ClientServiceTheme> optionalClientServiceTheme = clientServiceThemeList.stream()
				        .filter(theme -> theme.getClient().getId().equals(clientId) && theme.getService().getId().equals(serviceId))
				        .findFirst();

				if (optionalClientServiceTheme.isPresent()) {
				    ClientServiceTheme clientServiceTheme = optionalClientServiceTheme.get();
					System.out.println("Found client-service[clientId:" + clientId + ",serviceId:" + serviceId + "] theme:" + clientServiceTheme);

				    clientServiceThemeMap.put(Constants.LOGO_URL, clientServiceTheme.getLogoUrl());
				    clientServiceThemeMap.put(Constants.BACKGROUND_IMAGE_URL, clientServiceTheme.getBackgroundImageUrl());
				    clientServiceThemeMap.put(Constants.BACKGROUND_COLOR_CODE, clientServiceTheme.getColorCode());
				} else {
					System.out.println("Could not find client-service[clientId:" + clientId + ",serviceId:" + serviceId + "] theme");
					System.out.println("Searching for client theme");
					
					
					Optional<ClientServiceTheme> optionalClientTheme = clientServiceThemeList.stream()
					        .filter(theme -> theme.getClient().getId().equals(clientId) && theme.getService() == null)
					        .findFirst();

					if (optionalClientTheme.isPresent()) {
					    ClientServiceTheme clientTheme = optionalClientTheme.get();
						System.out.println("Found client[id:" + clientId + "] theme:" + clientTheme);
					    
					    clientServiceThemeMap.put(Constants.LOGO_URL, clientTheme.getLogoUrl());
					    clientServiceThemeMap.put(Constants.BACKGROUND_IMAGE_URL, clientTheme.getBackgroundImageUrl());
					    clientServiceThemeMap.put(Constants.BACKGROUND_COLOR_CODE, clientTheme.getColorCode());
					} else {
						System.out.println("Could not find client[id:" + clientId + "] theme");
					}
					
				}
				
			}else {
				System.out.println("Not found any theme for client-service[clientId:" + clientId + ",serviceId:" + serviceId + "]");
			}
		}
		
		
		if (clientServiceThemeMap.isEmpty()) {
			System.out.println("Setting default theme");
			
		    clientServiceThemeMap.put(Constants.LOGO_URL, "https://img.freepik.com/free-vector/bird-colorful-logo-gradient-vector_343694-1365.jpg?size=338&ext=jpg&ga=GA1.1.1412446893.1705143600&semt=sph");
		    clientServiceThemeMap.put(Constants.BACKGROUND_IMAGE_URL, "https://img.freepik.com/free-photo/autumn-leaf-falling-revealing-intricate-leaf-vein-generated-by-ai_188544-9869.jpg?size=626&ext=jpg&ga=GA1.1.1412446893.1705017600&semt=ais");
		}
		
		return clientServiceThemeMap;
	}
}
