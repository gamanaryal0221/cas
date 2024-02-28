package vcp.np.cas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vcp.np.cas.domains.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmployerIdAndUsername(Long employerId, String username);
}
