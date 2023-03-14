package travel.ways.travelwaysapi.map.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.map.model.dto.request.CreateLocationRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@WithMockUser("JD")
@SpringBootTest
class LocationServiceImplTest {
    @Autowired
    LocationServiceImpl locationService;

    @Test
    public void getLocation_shouldReturnLocationById() {
        assertThrows(ServerException.class, () -> locationService.getByOsmId("not_exists"));
    }

    @Test
    public void create_shouldThrowException_whenAlreadyExists() {
        assertThrows(ServerException.class, () -> locationService.create(new CreateLocationRequest(
                "name",
                "54.434",
                "43.343",
                "display_name",
                "osm_id"
        )));

    }

}