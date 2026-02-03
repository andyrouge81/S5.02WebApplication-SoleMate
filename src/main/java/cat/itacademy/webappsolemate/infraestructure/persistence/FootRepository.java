package cat.itacademy.webappsolemate.infraestructure.persistence;

import cat.itacademy.webappsolemate.domain.entities.Foot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FootRepository extends JpaRepository<Foot, Long> {

    List<Foot> findByOwnerId(Long ownerId);
}
