package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public List<ItemRequestDto> getUsersAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "0") @Min(0)  int from,
                                            @RequestParam(defaultValue = "10") @Min(1)  int size) {
        return itemRequestService.getUsersAll(userId, from, size);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getOtherRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0)  int from,
                                         @RequestParam(defaultValue = "10") @Min(1)  int size) {
        return itemRequestService.getOtherRequest(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ItemRequestDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long requestId) {
        return itemRequestService.get(userId, requestId);
    }

}