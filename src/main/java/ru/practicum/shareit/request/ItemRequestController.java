package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItem(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUsersAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getUsersAll(userId);
    }

    @GetMapping("/requests/all")
    List<ItemRequestDto> getOtherRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam Integer from,
                                         @RequestParam Integer size) {
        return itemRequestService.getOtherRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@PathVariable long requestId) {
        return itemRequestService.get(requestId);
    }

}