package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.trip.model.db.trip.TripImage;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.trip.repository.TripImageRepository;
import travel.ways.travelwaysapi.trip.repository.TripRepository;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.AppUserTrip;
import travel.ways.travelwaysapi.user.repository.AppUserTripRepository;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final TripImageRepository tripImageRepository;
    private final AppUserTripRepository appUserTripRepository;

    @Override
    @Transactional
    @SneakyThrows
    public Trip createTrip(CreateTripRequest request) {
        Trip trip = new Trip();
        trip.setHash(UUID.randomUUID().toString());
        trip.setOpen(true);
        trip.setPublic(request.isPublic());
        trip.setTitle(request.getTitle());
        trip.setDescription(request.getDescription());
        trip = tripRepository.save(trip);
        userService.getLoggedUser().addTrip(trip, true);

        return trip;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteTrip(Trip trip) {
        AppUser owner = this.findOwner(trip);
        if (!userService.getLoggedUser().equals(owner)) {
            throw new ServerException("You don't have permission to delete the trip", HttpStatus.FORBIDDEN);
        }

        log.debug("removing trip with id: " + trip.getId());
        getImageSummaryList(trip).forEach(x -> deleteImage(x.getHash()));

        for (AppUserTrip appUserTrip : trip.getUsers()) {
            appUserTrip.setUser(null);
            appUserTrip.setTrip(null);
        }
        tripRepository.delete(trip);
    }

    @Override
    @SneakyThrows
    public Trip getTrip(String hash) {
        Trip trip = tripRepository.findByHash(hash);
        AppUser loggedUser = userService.getLoggedUser();
        if (trip == null || (!trip.isPublic() && !checkIfContributor(trip, loggedUser))) {
            throw new ServerException("Can't find trip or you do not have permission to see it", HttpStatus.NOT_FOUND);
        }
        return trip;
    }

    @Override
    public List<TripResponse> getUserTrips(AppUser user) {
        AppUser loggedUser = userService.getLoggedUser();
        boolean showPrivate = loggedUser.equals(user);

        return user.getTrips().stream().filter(x -> {
            var trip = x.getTrip();
            return showPrivate || trip.isPublic() || checkIfContributor(trip, loggedUser);
        }).map(x -> TripResponse.of(x.getTrip(), getImageSummaryList(x.getTrip()))).toList();
    }

    @Override
    @Transactional
    @SneakyThrows
    public void closeTrip(String hash) {
        Trip trip = this.getTrip(hash);
        if (!this.findOwner(trip).equals(userService.getLoggedUser())) {
            throw new ServerException("You do not have permission to open the trip", HttpStatus.FORBIDDEN);
        }
        trip.setOpen(false);
    }

    @Override
    @Transactional
    @SneakyThrows
    public void openTrip(String hash) {
        Trip trip = this.getTrip(hash);
        if (!this.findOwner(trip).equals(userService.getLoggedUser())) {
            throw new ServerException("You do not have permission to close the trip", HttpStatus.FORBIDDEN);
        }
        trip.setOpen(true);
    }

    @Override
    public AppUser findOwner(Trip trip) {
        return userService.getTripOwner(trip);
    }


    @Override
    @Transactional
    @SneakyThrows
    public Trip editTrip(EditTripRequest request) {
        AppUser loggedUser = userService.getLoggedUser();
        Trip trip = this.getTrip(request.getHash());
        if (!checkIfContributor(trip, loggedUser)) {
            throw new ServerException("You don't have permission to edit the trip", HttpStatus.FORBIDDEN);
        }
        trip.setTitle(request.getTitle());
        trip.setDescription(request.getDescription());
        trip.setPublic(request.getIsPublic());
        return trip;
    }

    @Override
    @Transactional
    @SneakyThrows
    public ImageDto editMainImage(Trip trip, String newMainImageHash) {
        if (!userService.getLoggedUser().equals(this.findOwner(trip))) {
            throw new ServerException("You don't have permission to edit the image", HttpStatus.FORBIDDEN);
        }

        if (newMainImageHash != null && !tripImageRepository.existsImageInTrip(trip.getId(), newMainImageHash)) {
            throw new ServerException("this image is not in the trip", HttpStatus.BAD_REQUEST);
        }

        if (newMainImageHash == null) {
            throw new ServerException("missing new main image hash", HttpStatus.BAD_REQUEST);
        }

        tripImageRepository.unsetMainImageForTrip(trip);

        var newMainTripImage = tripImageRepository.findByImageHash(newMainImageHash);
        newMainTripImage.setMain(true);
        var imageMetadata = imageService.getImageSummary(newMainImageHash);

        return ImageDto.of(imageMetadata, true);
    }

    @Override
    @Transactional
    @SneakyThrows
    public ImageDto addImage(AddImageRequest request, String tripHash) {
        var trip = this.getTrip(tripHash);
        if (!this.checkIfContributor(trip, userService.getLoggedUser())) {
            throw new ServerException("You don't have permission to add image", HttpStatus.FORBIDDEN);
        }
        var imageId = imageService.createImage(request.getImagesData()[0].getOriginalFilename(), request.getImagesData()[0]);
        // here we have to download whole image, because hybernate can't update object only by id :)
        var image = imageService.getImage(imageId);
        var newTripImage = new TripImage(trip, image);
        newTripImage.setMain(request.getIsMain());

        tripImageRepository.save(newTripImage);
        return ImageDto.of(image, request.getIsMain());
    }

    @Override
    public boolean checkIfContributor(Trip trip, AppUser appUser) {
        if (appUser == null || trip == null) {
            return false;
        }
        for (AppUserTrip appUserTrip : trip.getUsers()) {
            if (appUserTrip.getUser() == appUser) return true;
        }
        return false;
    }

    @Transactional
    @Override
    @SneakyThrows
    public void deleteImage(String imageHash) {
        Trip trip = this.getTripByImageHash(imageHash);
        if (!(this.checkIfContributor(trip, userService.getLoggedUser()))) {
            throw new ServerException("You don't have permission to delete the image", HttpStatus.FORBIDDEN);
        }
        log.debug("removing image from trip with id: " + trip.getId());
        imageService.deleteImage(imageHash);
    }

    @Override
    @SneakyThrows
    public List<ImageDto> getImageSummaryList(Trip trip) {
        AppUser appUser = userService.getLoggedUser();
        if (!trip.isPublic() && !this.checkIfContributor(trip, appUser)) {
            throw new ServerException("you do not have permission to see the images", HttpStatus.FORBIDDEN);
        }
        return imageService.getImageSummaryList(tripImageRepository.findAllImageIdInTrip(trip.getId())).stream().map(x ->
                ImageDto.of(x, tripImageRepository.isMain(x.getHash()))).toList();
    }

    @Override
    @SneakyThrows
    @Transactional
    public Trip getTripByImageHash(String imageHash) {
        Trip trip = tripRepository.findByImagesImageHash(imageHash);
        if (trip == null) {
            throw new ServerException("Trip not found", HttpStatus.NOT_FOUND);
        }
        return trip;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteUserFromTrip(String userHash, String tripHash) {
        Trip trip = this.getTrip(tripHash);
        AppUser owner = this.findOwner(trip);
        if (!owner.equals(userService.getLoggedUser())) {
            throw new ServerException("You do not have permission to remove user from the trip", HttpStatus.FORBIDDEN);
        }
        AppUser userToRemove = userService.getByHash(userHash);

        if (owner.equals(userToRemove)) {
            throw new ServerException("You can not remove owner", HttpStatus.BAD_REQUEST);
        }
        Optional<AppUserTrip> appUserTrip = userToRemove.getTrips().stream()
                .filter(auTrip -> auTrip.getTrip().equals(trip)).findAny();
        if (appUserTrip.isEmpty()) {
            throw new ServerException("This user is not in the trip", HttpStatus.BAD_REQUEST);
        }
        appUserTrip.get().getUser().getTrips().remove(appUserTrip.get());
        appUserTrip.get().getTrip().getUsers().remove(appUserTrip.get());
        appUserTripRepository.delete(appUserTrip.get());
    }
}
