package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.request.SwipeRequest;
import cat.itacademy.webappsolemate.application.dto.response.SwipeResponse;
import cat.itacademy.webappsolemate.application.services.swipe.FootSwipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feet")
public class FootSwipeController {

    private final FootSwipeService footSwipe;

    public FootSwipeController(FootSwipeService footSwipe) {
        this.footSwipe = footSwipe;
    }

    @PostMapping("/{footId}/swipe")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public SwipeResponse saveOrUpdateSwipe(
            @PathVariable Long footId,
            @Valid @RequestBody SwipeRequest request) {

        return footSwipe.saveOrUpdateSwipe(footId, request);
    }

    @GetMapping("/swipes/me")
    @PreAuthorize("isAuthenticated()")
    public List<SwipeResponse> getMySwipes() {
        return footSwipe.getMySwipes();

    }

}
