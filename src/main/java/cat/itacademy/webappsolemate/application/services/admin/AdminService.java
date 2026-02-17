package cat.itacademy.webappsolemate.application.services.admin;

import cat.itacademy.webappsolemate.application.dto.request.UpdateAdminRequest;
import cat.itacademy.webappsolemate.application.dto.response.AdminListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    Page<AdminListResponse> listUsers(String search, Pageable pageable);

    AdminListResponse updateUser(Long userId, UpdateAdminRequest request);

    void deleteUser(Long userId);
}
