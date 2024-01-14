package vcp.np.cas.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.cas.domains.ClientService;
import vcp.np.cas.domains.User;
import vcp.np.cas.repositories.ClientServiceRepository;
import vcp.np.cas.repositories.UserRepository;

@Service
public class LoginService {

	@Autowired
	public UserRepository userRepository;

	@Autowired
	public ClientServiceRepository clientServiceRepository;
	
	
	public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    } 

    public Optional<ClientService> getClientServiceByRequestHost(String requestHost) {
        return clientServiceRepository.findByRequestHost(requestHost);
    }
    
}
