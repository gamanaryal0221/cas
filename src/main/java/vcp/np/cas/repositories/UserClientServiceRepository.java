package vcp.np.cas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vcp.np.cas.domains.UserClientService;


public interface UserClientServiceRepository extends JpaRepository<UserClientService, Long> {
    Optional<UserClientService> findByUserIdAndClientServiceId(Long clientId, Long clientServiceId);
    
}
