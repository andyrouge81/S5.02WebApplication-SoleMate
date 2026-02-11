package cat.itacademy.webappsolemate.domain.entities;

import cat.itacademy.webappsolemate.domain.enums.SwipeAction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "foot_swipes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "foot_id"}))
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FootSwipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "foot_id", nullable = false)
    private Foot foot;

    @Enumerated(EnumType.STRING)
    private SwipeAction action;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
