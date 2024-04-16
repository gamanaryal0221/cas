package vcp.np.cas.config.datasource.usermanagement.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import vcp.np.cas.config.datasource.usermanagement.domains.ClientServiceTheme;


public interface ClientServiceThemeRepository extends JpaRepository<ClientServiceTheme, Long> {
    Optional<ClientServiceTheme> findByClientIdAndServiceId(Long clientId, Long serviceId);
    List<ClientServiceTheme> findAllByClientId(Long clientId);
}
