package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.review.Review;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class Item {
    @EqualsAndHashCode.Exclude
    Long id;
    User owner;
    Boolean available;
    String description;
    String name;
    @EqualsAndHashCode.Exclude
    final List<Review> reviewList = new ArrayList<>();
}