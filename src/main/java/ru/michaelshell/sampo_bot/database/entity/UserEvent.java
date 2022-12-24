package ru.michaelshell.sampo_bot.database.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "event"})
@Builder
@Entity
@Table(name = "users_event")
public class UserEvent implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private String partnerFullname;

    private LocalDateTime signedAt;

    public void setUser(User user) {
        this.user = user;
        this.user.getUserEvents().add(this);
    }

    public void setEvent(Event event) {
        this.event = event;
        this.event.getUserEvents().add(this);
    }
}
