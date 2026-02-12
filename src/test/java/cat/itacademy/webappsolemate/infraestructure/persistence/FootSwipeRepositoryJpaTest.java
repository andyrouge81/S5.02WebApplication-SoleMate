package cat.itacademy.webappsolemate.infraestructure.persistence;

import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.FootSwipe;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.ArchType;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.domain.enums.SwipeAction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FootSwipeRepositoryDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FootRepository footRepository;

    @Autowired
    private FootSwipeRepository footSwipeRepository;

    @Test
    void findByUserIdAndFootId_whenExists_returnsSwipe() {

        User owner = userRepository.save(User.builder()
                .username("owner")
                .email("owner@mail.com")
                .password("p")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        User swiper = userRepository.save(User.builder()
                .username("swiper")
                .email("swiper@mail.com")
                .password("p")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        Foot foot = footRepository.save(Foot.builder()
                .title("foot")
                .imageUrl("http://img")
                .imageHash("hash123")
                .archType(ArchType.PES_RECTUS)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build());

        footSwipeRepository.save(FootSwipe.builder()
                .user(swiper)
                .foot(foot)
                .action(SwipeAction.LIKE)
                .createdAt(LocalDateTime.now())
                .build());

        var result = footSwipeRepository.findByUser_IdAndFoot_Id(swiper.getId(), foot.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAction()).isEqualTo(SwipeAction.LIKE);
    }

    @Test
    void countByFootIdAndAction_returnsCorrectCount() {

        User owner = userRepository.save(User.builder()
                .username("owner2")
                .email("owner2@mail.com")
                .password("p")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        User u1 = userRepository.save(User.builder()
                .username("u1")
                .email("u1@mail.com")
                .password("p")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        User u2 = userRepository.save(User.builder()
                .username("u2")
                .email("u2@mail.com")
                .password("p")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        Foot foot = footRepository.save(Foot.builder()
                .title("foot2")
                .imageUrl("http://img2")
                .imageHash("hash456")
                .archType(ArchType.PES_CAVUS)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build());

        footSwipeRepository.save(FootSwipe.builder().user(u1).foot(foot).action(SwipeAction.LIKE).createdAt(LocalDateTime.now()).build());
        footSwipeRepository.save(FootSwipe.builder().user(u2).foot(foot).action(SwipeAction.DISLIKE).createdAt(LocalDateTime.now()).build());

        long likes = footSwipeRepository.countByFoot_IdAndAction(foot.getId(), SwipeAction.LIKE);
        long dislikes = footSwipeRepository.countByFoot_IdAndAction(foot.getId(), SwipeAction.DISLIKE);

        assertThat(likes).isEqualTo(1);
        assertThat(dislikes).isEqualTo(1);
    }
}
