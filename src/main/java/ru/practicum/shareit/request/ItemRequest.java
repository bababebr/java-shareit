package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "Request")
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String description;
    LocalDateTime created;
    @Column(name = "Requester_id")
    Long requesterID;
    @Column(name = "Item_id")
    Long itemId = null;
}