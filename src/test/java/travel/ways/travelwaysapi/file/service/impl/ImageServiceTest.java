package travel.ways.travelwaysapi.file.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;
import travel.ways.travelwaysapi.file.repository.ImageRepository;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.controller.TripController;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser("JD")
class ImageServiceTest {
    @Autowired
    ImageService imageService;
    @Autowired
    TripController tripController;
    @Autowired
    AttractionService attractionService;
    @Autowired
    JwtService jwtService;
    @Autowired
    private ImageRepository imageRepository;

    @Test
    public void getImageSummary_shouldReturnImageSummaryDto() {
        // arrange
        Attraction attraction = attractionService.createAttraction(new CreateAttractionRequest(
                "osm_id",
                "title",
                "description",
                true,
                true,
                Date.valueOf(LocalDate.now().toString()),
                null,
                null
        ));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = attractionService.addImage(new AddImageRequest(multipartFile, false), attraction.getHash());

        //act & assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        assertNotNull(imageService.getImageSummary(imageDto.getHash()));
        assertEquals(imageDto.getHash(), imageService.getImageSummary(imageDto.getHash()).getHash());
        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    public void getImageSummary_shouldReturnImageSummaryDtoById() {
        // arrange
        Attraction attraction = attractionService.createAttraction(new CreateAttractionRequest(
                "osm_id",
                "title",
                "description",
                true,
                true,
                Date.valueOf(LocalDate.now().toString()),
                null,
                null
        ));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = attractionService.addImage(new AddImageRequest(multipartFile, false), attraction.getHash());

        //act & assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        assertNotNull(imageService.getImageSummary(imageService.getImage(imageDto.getHash()).getId()));
        assertEquals(imageDto.getHash(), imageService.getImageSummary(imageService.getImage(imageDto.getHash()).getId()).getHash());
        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    public void getImageSummary_shouldThrow_whenNotFoundImageByLong() {
        //act & assert
        assertThrows(ServerException.class, () -> imageService.getImageSummary(1L));
    }

    @Test
    public void getImageSummary_shouldThrow_whenNotFoundImage() {
        //act & assert
        assertThrows(ServerException.class, () -> imageService.getImageSummary("hash"));
    }
}