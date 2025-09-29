package petwalk.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalkRouteRepository extends JpaRepository<WalkRoute, Long> {

    List<WalkRoute> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);
}