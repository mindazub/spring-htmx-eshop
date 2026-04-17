package mindaz.simplehtmxcrudauth.domain.user.repository;

import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByDeletedFalse();
    Optional<User> findByIdAndDeletedFalse(Long id);
}

