package vcp.np.cas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vcp.np.cas.domains.UserEmail;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmail, Long> {
    Optional<UserEmail> findByEmail(String email);
    Optional<UserEmail> findByEmailAndIsPrimary(String email, boolean isPrimary);

}
