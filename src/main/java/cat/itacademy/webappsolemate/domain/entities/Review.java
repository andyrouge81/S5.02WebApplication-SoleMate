package cat.itacademy.webappsolemate.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
// para no dejar que se ponga mas de un comentario por ususario
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(
        columnNames = {"reviewer_id", "foot_id"}))
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String comment;

    @Column(nullable = false)
    private Integer rateAspect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foot_id", nullable = false)
    private Foot foot;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
