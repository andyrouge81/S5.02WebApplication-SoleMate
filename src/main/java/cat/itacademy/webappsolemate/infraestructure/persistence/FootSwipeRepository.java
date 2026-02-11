package cat.itacademy.webappsolemate.infraestructure.persistence;

import cat.itacademy.webappsolemate.domain.entities.FootSwipe;
import cat.itacademy.webappsolemate.domain.enums.SwipeAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FootSwipeRepository extends JpaRepository<FootSwipe, Long> {

    Optional<FootSwipe> findByUser_IdAndFoot_Id(Long userId, Long footId);

    List<FootSwipe> findByUser_IdOrderByCreatedAtDesc(Long UserId);

    long countByFoot_IdAndAction(Long footId, SwipeAction action);

}
