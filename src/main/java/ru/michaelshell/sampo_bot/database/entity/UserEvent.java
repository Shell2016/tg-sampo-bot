package ru.michaelshell.sampo_bot.database.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "users_event")
public class UserEvent implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserEvent userEvent = (UserEvent) o;
        return id != null && Objects.equals(id, userEvent.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
