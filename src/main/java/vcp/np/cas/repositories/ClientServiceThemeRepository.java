package vcp.np.cas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vcp.np.cas.domains.ClientServiceTheme;

@Repository
public interface ClientServiceThemeRepository extends JpaRepository<ClientServiceTheme, Long> {
    Optional<ClientServiceTheme> findByClientIdAndServiceId(Long clientId, Long serviceId);
    List<ClientServiceTheme> findAllByClientId(Long clientId);
}
