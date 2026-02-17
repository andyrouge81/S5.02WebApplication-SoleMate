package cat.itacademy.webappsolemate.infraestructure.persistence;

import cat.itacademy.webappsolemate.domain.entities.Foot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FootRepository extends JpaRepository<Foot, Long> {

    List<Foot> findByOwnerId(Long ownerId);

    void deleteByOwnerId(Long ownerId);

    boolean existsByImageUrl(String imageUrl);

    Optional<Foot> findByImageUrl(String imageUrl);

    boolean existsByImageHash(String imageHash);

    Optional<Foot> findByImageHash(String imageHash);
}
