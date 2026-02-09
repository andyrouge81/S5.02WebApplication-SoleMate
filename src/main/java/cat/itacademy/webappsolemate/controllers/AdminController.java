package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.request.UpdateAdminRequest;
import cat.itacademy.webappsolemate.application.dto.response.AdminListResponse;
import cat.itacademy.webappsolemate.application.services.admin.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin endpoints")
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public Page<AdminListResponse> listUsers (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("id").descending()
        );
        return adminService.listUsers(search, pageable);
    }

    @PutMapping("/{userId}")
    public AdminListResponse updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAdminRequest request
            ) {
        return adminService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);

    }
}
