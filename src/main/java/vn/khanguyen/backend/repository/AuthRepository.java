package vn.khanguyen.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.khanguyen.backend.domain.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
