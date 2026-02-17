package cat.itacademy.webappsolemate.infraestructure.persistence;

import cat.itacademy.webappsolemate.domain.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository  extends JpaRepository<Review, Long> {

    boolean existsByReviewer_IdAndFoot_Id(Long reviewerId, Long footId);

    List<Review> findByFootId(Long footId);

    void deleteByFootId(Long footId);


    void deleteByReviewerId(Long reviewerId);

    void deleteByFoot_Owner_Id(Long ownerId);
}
