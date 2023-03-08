package travel.ways.travelwaysapi.trip.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.map.model.db.Location;
import travel.ways.travelwaysapi.map.service.shared.LocationService;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.repository.AttractionImageRepository;
import travel.ways.travelwaysapi.trip.repository.AttractionRepository;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
@ActiveProfiles("test")
class AttractionServiceImplTest {

    @Mock
    LocationService locationService;
    @Mock
    AttractionRepository attractionRepository;
    @Mock
    UserService userService;
    @Mock
    TripService tripService;
    @Mock
    ImageService imageService;
    @Mock
    AttractionImageRepository attractionImageRepository;

    AttractionServiceImpl attractionService;

    @BeforeEach
    void setUp() {
        attractionService = new AttractionServiceImpl(locationService, attractionRepository,
                userService, tripService, imageService, attractionImageRepository);
    }

    @Test
    public void createAttraction_ShouldCreateAccount_WhenGotRequestWithoutTrip() {
        CreateAttractionRequest request = new CreateAttractionRequest();
        request.setTitle("testTitle");
        request.setDescription("testDescription");
        when(locationService.getByOsmId(any())).thenReturn(new Location());
        when(userService.getLoggedUser()).thenReturn(new AppUser());
        when(attractionRepository.save(any())).thenAnswer(i -> i.getArgument(0, Attraction.class));
        Attraction attraction = attractionService.createAttraction(request);
        assertEquals("testTitle", attraction.getTitle());
        assertEquals("testDescription", attraction.getDescription());
        assertNull(attraction.getTrip());
    }

    @Test
    public void createAttraction_ShouldCreateAccount_WhenGotRequestWithTrip() {
        CreateAttractionRequest request = new CreateAttractionRequest();
        request.setTitle("testTitle");
        request.setDescription("testDescription");
        request.setTripHash("testTripHash");
        when(locationService.getByOsmId(any())).thenReturn(new Location());
        when(userService.getLoggedUser()).thenReturn(new AppUser());
        Trip trip = new Trip();
        trip.setHash("testTripHash");
        when(tripService.getTrip(any())).thenReturn(trip);
        when(attractionRepository.save(any())).thenAnswer(i -> i.getArgument(0, Attraction.class));
        when(tripService.checkIfContributor(any(), any())).thenReturn(true);
        Attraction attraction = attractionService.createAttraction(request);
        assertEquals("testTitle", attraction.getTitle());
        assertEquals("testDescription", attraction.getDescription());
        assertEquals("testTripHash", attraction.getTrip().getHash());
    }

    @Test
    public void createAttraction_ShouldThrowException_WhenUserIsNotContributor() {
        CreateAttractionRequest request = new CreateAttractionRequest();
        when(locationService.getByOsmId(any())).thenReturn(new Location());
        when(userService.getLoggedUser()).thenReturn(new AppUser());
        when(tripService.checkIfContributor(any(), any())).thenReturn(false);
        request.setTripHash("testTripHash");
        Trip trip = new Trip();
        trip.setHash("testTripHash");
        when(tripService.getTrip(any())).thenReturn(trip);
        assertThrows(ServerException.class, () -> attractionService.createAttraction(request));
    }
//    @Test
//    public void addImage_ShouldAddImage_WhenGotProperRequest() {
//        AddImageRequest addImageRequest = new AddImageRequest();
//        MultipartFile multipartFile = new MockMultipartFile("testFile", "someImage".getBytes());
//        Image image = new Image();
//        image.setExtension(MediaType.IMAGE_JPEG_VALUE);
//        image.setData("someImage".getBytes());
//        image.setHash(UUID.randomUUID().toString());
//        when(imageService.createImage(any(), any())).thenReturn(image);
//        AttractionImage attractionImage = new AttractionImage();
//        attractionImage.setImage(image);
//        when(attractionRepository.save(any())).thenReturn(attractionImage);
//        attractionService.addImage(addImageRequest, )
//    }


}