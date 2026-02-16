package cat.itacademy.webappsolemate.application.services.foot;


import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.dto.response.FootResponse;


import java.util.List;

public interface FootService {

    FootResponse createFoot(FootRequest request);

    List<FootResponse> getAllFeet();

    List<FootResponse> getMyFeet();

    FootResponse updateFoot(Long footId, FootRequest request);

    void deleteFoot(Long footId);


}
