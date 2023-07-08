package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * TODO Sprint add-item-requests.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestClient.addItem(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "0") @Min(0)  int from,
                                            @RequestParam(defaultValue = "10") @Min(1)  int size) {
        return requestClient.getUsersAll(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0)  int from,
                                         @RequestParam(defaultValue = "10") @Min(1)  int size) {
        return requestClient.getOtherRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object>  get(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long requestId) {
        return requestClient.get(userId, requestId);
    }
}