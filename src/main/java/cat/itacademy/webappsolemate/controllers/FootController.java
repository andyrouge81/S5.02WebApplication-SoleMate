package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.dto.response.FootResponse;
import cat.itacademy.webappsolemate.application.services.foot.FootService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Foot endpoints")
@RestController
@RequestMapping("/feet")
public class FootController {

    final private FootService footService;

    public FootController(FootService footService) {
        this.footService = footService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public FootResponse createFoot(@Valid @RequestBody FootRequest request) {

        return footService.createFoot(request);
    }

    @GetMapping
    public List<FootResponse> getAllFeet() {

        return footService.getAllFeet();
    }

    @GetMapping("/me")
    public List<FootResponse> getMyFeet() {

        return footService.getMyFeet();
    }

    @PutMapping("/{footId}")
    @PreAuthorize("hasRole('ADMIN') or @footSecurity.isOwner(#footId)")
    public FootResponse updateFoot(
            @PathVariable Long footId,
            @Valid @RequestBody FootRequest request) {

        return footService.updateFoot(footId, request);
    }

    @DeleteMapping("/{footId}")
    @PreAuthorize("hasRole('ADMIN') or @footSecurity.isOwner(#footId)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteFoot(@PathVariable Long footId) {

        footService.deleteFoot(footId);
    }
}
