package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.map.service.shared.LocationService;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.AttractionImage;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.AttractionResponse;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.trip.repository.AttractionImageRepository;
import travel.ways.travelwaysapi.trip.repository.AttractionRepository;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {
    private final LocationService locationService;
    private final AttractionRepository attractionRepository;
    private final UserService userService;
    private final TripService tripService;
    private final ImageService imageService;
    private final AttractionImageRepository attractionImageRepository;

    @Override
    @Transactional
    public Attraction createAttraction(CreateAttractionRequest createAttractionRequest) {
        var attraction = Attraction.of(createAttractionRequest);
        var location = locationService.getByOsmId(createAttractionRequest.getOsmId());

        attraction.setLocation(location);
        attraction.setUser(userService.getLoggedUser());
        var tripHash = createAttractionRequest.getTripHash();
        var trip = tripHash != null ? tripService.getTrip(tripHash) : tripService.createTrip(new CreateTripRequest(createAttractionRequest.getTitle(), createAttractionRequest.isPublic(), createAttractionRequest.getDescription()));
        this.addAttractionToTrip(attraction, trip);

        return attractionRepository.save(attraction);
    }

    @Override
    @Transactional
    @SneakyThrows
    public List<ImageDto> addImage(AddImageRequest request, String attractionHash) {
        Attraction attraction = this.getAttraction(attractionHash);
        if (!this.checkIfContributor(attraction, userService.getLoggedUser())) {
            throw new ServerException("You don't have permission to add image", HttpStatus.FORBIDDEN);
        }
        var imagesId = Arrays.stream(request.getImagesData()).map(x -> imageService.createImage(x.getOriginalFilename(), x));

        // here we have to download whole image, because hybernate can't update object only by id :)
        var response = new ArrayList<ImageDto>();

        imagesId.forEach(x -> {
            var image = imageService.getImage(x);
            var newAttractionImage = new AttractionImage(attraction, image);
            newAttractionImage = attractionImageRepository.save(newAttractionImage);
            image.setAttraction(newAttractionImage);
            attraction.getImages().add(newAttractionImage);
            response.add(ImageDto.of(image, false));
        });

        return response;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void editMainImage(Attraction attraction, String newMainImageHash) {
        if (!userService.getLoggedUser().equals(attraction.getUser())) {
            throw new ServerException("You don't have permission to edit the image", HttpStatus.FORBIDDEN);
        }

        if (newMainImageHash != null && !attractionImageRepository.existsImageInAttraction(attraction.getId(), newMainImageHash)) {
            throw new ServerException("this image is not in the attraction", HttpStatus.BAD_REQUEST);
        }

        attractionImageRepository.unsetMainImage(attraction);

        var newMainAttractionImage = attractionImageRepository.findByImageHash(newMainImageHash);
        newMainAttractionImage.setMain(true);
    }

    @Override
    public List<AttractionResponse> getUserAttractions(AppUser user) {
        AppUser loggedUser = userService.getLoggedUser();
        boolean showPrivate = loggedUser.equals(user);

        return mapToAttractionResponse(attractionRepository.findAllByUser(user), showPrivate);

    }

    @Override
    @Transactional
    @SneakyThrows
    public Attraction addAttractionToTrip(Attraction attraction, Trip trip) {
        AppUser loggedUser = userService.getLoggedUser();
        if (!tripService.checkIfContributor(trip, loggedUser)) {
            throw new ServerException("you do not have permission to add attraction to the trip", HttpStatus.FORBIDDEN);
        }
        attraction.setTrip(trip);
        return attraction;
    }

    @Override
    public List<AttractionResponse> getTripAttractions(Trip trip) {
        AppUser loggedUser = userService.getLoggedUser();
        boolean showPrivate = loggedUser.equals(tripService.findOwner(trip));

        return mapToAttractionResponse(attractionRepository.findAllByTrip(trip), showPrivate);
    }

    @Override
    @SneakyThrows
    @Transactional
    public void deleteAttraction(String attractionHash) {
        AppUser loggedUser = userService.getLoggedUser();
        Attraction attraction = this.getAttraction(attractionHash);
        if (!loggedUser.equals(attraction.getUser())) {
            throw new ServerException("You do not have permission to delete the attraction", HttpStatus.FORBIDDEN);
        }
        log.debug("removing attraction with id: " + attraction.getId());
        // maybe here should be soft delete?
        attractionRepository.delete(attraction);
    }

    @Override
    @SneakyThrows
    public Attraction getAttraction(String attractionHash) {
        Attraction attraction = attractionRepository.findByHash(attractionHash);
        if (attraction == null) {
            throw new ServerException("Can not find attraction", HttpStatus.NOT_FOUND);
        }
        return attraction;
    }

    @Override
    public boolean checkIfContributor(Attraction attraction, AppUser appUser) {
        if (attraction.getTrip() != null && !tripService.checkIfContributor(attraction.getTrip(), appUser)) {
            return false;
        }
        return attraction.getTrip() != null || attraction.getUser().equals(appUser);
    }

    @Override
    @SneakyThrows
    public List<ImageDto> getImageSummaryList(Attraction attraction) {
        AppUser appUser = userService.getLoggedUser();
        if (!attraction.isPublic() && !this.checkIfContributor(attraction, appUser)) {
            throw new ServerException("you do not have permission to see the images", HttpStatus.FORBIDDEN);
        }
        return imageService.getImageSummaryList(attractionImageRepository.findAllImageIdInAttraction(attraction.getId())).stream().map(x -> ImageDto.of(x, attractionImageRepository.isMain(x.getHash()))).toList();
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteImage(String imageHash) {
        Attraction attraction = this.getAttractionByImageHash(imageHash);
        if (!(this.checkIfContributor(attraction, userService.getLoggedUser()))) {
            throw new ServerException("You don't have permission to delete the image", HttpStatus.FORBIDDEN);
        }
        log.debug("removing image from attraction with id: " + attraction.getId());
        imageService.deleteImage(imageHash);
    }

    @Override
    @Transactional
    @SneakyThrows
    public Attraction editAttraction(EditAttractionRequest request) {
        var loggedUser = userService.getLoggedUser();
        var attraction = this.getAttraction(request.getAttractionHash());
        if (!this.checkIfContributor(attraction, loggedUser)) {
            throw new ServerException("You don't have permission to edit the trip", HttpStatus.FORBIDDEN);
        }
        if (!request.isValid()) {
            throw new ServerException("Invalid field values", HttpStatus.BAD_REQUEST);
        }
        attraction.setTitle(request.getTitle());
        attraction.setDescription(request.getDescription());
        attraction.setPublic(request.isPublic());
        attraction.setVisited(request.isVisited());
        attraction.setVisitedAt(request.getVisitedAt());
        attraction.setRate(request.getRate());
        var tripHash = request.getTripHash();
        if (tripHash != null) {
            var trip = tripService.getTrip(tripHash);
            attraction = this.addAttractionToTrip(attraction, trip);
        } else {
            attraction.setTrip(null);
        }
        return attraction;
    }

    @Override
    @SneakyThrows
    @Transactional
    public Attraction getAttractionByImageHash(String imageHash) {
        var attraction = attractionRepository.findByImagesImageHash(imageHash);
        if (attraction == null) {
            throw new ServerException("Attraction not found", HttpStatus.NOT_FOUND);
        }
        return attraction;
    }

    private List<AttractionResponse> mapToAttractionResponse(List<Attraction> attractions, boolean showPrivate) {
        var loggedUser = userService.getLoggedUser();

        return attractions.stream()
                .filter(x -> showPrivate || x.isPublic() || checkIfContributor(x, loggedUser))
                .map(x -> AttractionResponse.of(x, getImageSummaryList(x))).toList();
    }
}
