package mindaz.simplehtmxcrudauth.domain.order.repository;

import mindaz.simplehtmxcrudauth.domain.order.entity.Cart;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

