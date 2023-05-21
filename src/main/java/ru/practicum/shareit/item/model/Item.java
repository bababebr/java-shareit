package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.review.Review;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class Item {
    Long id;
    User owner;
    Boolean available;
    String description;
    String name;
    final List<Review> reviewList = new ArrayList<>();
}