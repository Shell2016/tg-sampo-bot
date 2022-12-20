package ru.michaelshell.sampo_bot.database.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userName")
@Builder
@Entity
@Table(name = "users")
public class User implements BaseEntity<Long>{

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime registeredAt;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<UserEvent> userEvents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Couple> couples = new ArrayList<>();
}
