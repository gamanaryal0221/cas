package vcp.np.cas.config.datasource.usermanagement.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import vcp.np.cas.config.datasource.usermanagement.domains.User;


public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmployerIdAndUsername(Long employerId, String username);
}
