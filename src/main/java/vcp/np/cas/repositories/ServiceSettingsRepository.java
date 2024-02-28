package vcp.np.cas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vcp.np.cas.domains.ServiceSettings;

public interface ServiceSettingsRepository extends JpaRepository<ServiceSettings, Long> {
    Optional<ServiceSettings> findByServiceId(Long serviceId);

}
