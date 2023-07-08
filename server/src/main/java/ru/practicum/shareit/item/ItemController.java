package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemBookingHistoryDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long itemId) {
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping
    public List<ItemBookingHistoryDto> getUsersOwnItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getUsersOwnItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingHistoryDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItemByDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDTO commentDTO) {
        return itemService.addComment(itemId, userId, commentDTO);
    }
}