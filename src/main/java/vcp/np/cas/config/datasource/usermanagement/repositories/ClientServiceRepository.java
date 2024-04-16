package vcp.np.cas.config.datasource.usermanagement.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import vcp.np.cas.config.datasource.usermanagement.domains.ClientService;


public interface ClientServiceRepository extends JpaRepository<ClientService, Long> {
    Optional<ClientService> findByRequestHost(String requestHost);
    Optional<ClientService> findByClientIdAndServiceId(Long clientId, Long serviceId);
}
