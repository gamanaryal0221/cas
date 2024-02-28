package vcp.np.cas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vcp.np.cas.domains.ClientService;

@Repository
public interface ClientServiceRepository extends JpaRepository<ClientService, Long> {
    Optional<ClientService> findByRequestHost(String requestHost);
    Optional<ClientService> findByClientIdAndServiceId(Long clientId, Long serviceId);
}
