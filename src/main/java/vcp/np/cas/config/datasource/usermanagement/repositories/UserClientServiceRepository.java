package vcp.np.cas.config.datasource.usermanagement.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import vcp.np.cas.config.datasource.usermanagement.domains.UserClientService;


public interface UserClientServiceRepository extends JpaRepository<UserClientService, Long> {
    Optional<UserClientService> findByUserIdAndClientServiceId(Long clientId, Long clientServiceId);
    
}
