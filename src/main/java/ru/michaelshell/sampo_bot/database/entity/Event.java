package ru.michaelshell.sampo_bot.database.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@ToString(exclude = {"userEvent"})
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

//    @Builder.Default
//    @OneToMany(mappedBy = "event")
//    private List<Couple> couples = new ArrayList<>();

}
