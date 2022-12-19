package ru.michaelshell.sampo_bot.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "event")
public class Event implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String info;

    private LocalDateTime time;

    private LocalDateTime createdAt;

    private String createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "event")
    private List<UserEvent> userEvents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "event")
    private List<Couple> couples = new ArrayList<>();

}
