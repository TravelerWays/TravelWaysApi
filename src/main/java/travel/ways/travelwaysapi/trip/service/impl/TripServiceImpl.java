package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.model.projection.ImageSummary;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.db.TripImage;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.trip.repository.TripImageRepository;
import travel.ways.travelwaysapi.trip.repository.TripRepository;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.AppUserTrip;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final TripImageRepository tripImageRepository;

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
        for (ImageSummary imageSummary : imageService.getImageSummaryList(trip)) {
            this.deleteImage(imageSummary.getHash());
        }

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

        List<TripResponse> trips = new ArrayList<>();
        for (AppUserTrip appUserTrip : user.getTrips()) {
            if (!showPrivate && !appUserTrip.getTrip().isPublic() && !checkIfContributor(appUserTrip.getTrip(), loggedUser)) {
                continue;
            }
            trips.add(TripResponse.of(appUserTrip.getTrip(), this.getImageSummaryList(appUserTrip.getTrip())));
        }
        return trips;
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
    public Image editMainImage(Trip trip, String newMainImageHash) {
        if (!userService.getLoggedUser().equals(this.findOwner(trip))) {
            throw new ServerException("You don't have permission to edit the image", HttpStatus.FORBIDDEN);
        }

        if (newMainImageHash != null && !imageService.checkIfImageExistsInTrip(trip, newMainImageHash)) {
            throw new ServerException("this image is not in the trip", HttpStatus.BAD_REQUEST);
        }

        String oldMainImageHash = imageService.getMainImageHash(trip);
        if (oldMainImageHash != null) {
            TripImage oldMainTripImage = tripImageRepository.findByImageHash(oldMainImageHash);
            oldMainTripImage.setMain(false);
        }
        if (newMainImageHash == null) {
            return null;
        }
        Image newMainImage = imageService.getImage(newMainImageHash);
        TripImage newMainTripImage = tripImageRepository.findByImageHash(newMainImageHash);
        newMainTripImage.setMain(true);
        return newMainImage;
    }

    @Override
    @Transactional
    @SneakyThrows
    public Image addImage(AddImageRequest request) {
        Trip trip = this.getTrip(request.getHash());
        if (!this.checkIfContributor(trip, userService.getLoggedUser())) {
            throw new ServerException("You don't have permission to add image", HttpStatus.FORBIDDEN);
        }
        Image image = imageService.createImage(request.getImageData().getOriginalFilename(), request.getImageData());

        TripImage newTripImage = new TripImage(trip, image);
        newTripImage = tripImageRepository.save(newTripImage);
        if (request.getIsMain()) {
            image = this.editMainImage(trip, image.getHash());
        }
        trip.getImages().add(newTripImage);
        image.setTrip(newTripImage);
        return image;
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
        //TODO add some magic
//        TripImage tripImage = tripImageRepository.findByImageHash(imageHash);
//        Trip trip = tripImage.getTrip();
//
//        if (!(this.checkIfContributor(trip, userService.getLoggedUser()))) {
//            throw new ServerException("You don't have permission to delete the image", HttpStatus.FORBIDDEN);
//        }
//        if (tripImage.isMain() && !userService.getLoggedUser().equals(this.findOwner(trip))) {
//            throw new ServerException("You don't have permission to delete the main image", HttpStatus.FORBIDDEN);
//        }
//        log.debug("removing image main= " + tripImage.isMain() + " from trip with id: " + trip.getId());
//        trip.getImages().remove(tripImage);
//        tripImage.setImage(null);
//        tripImage.setTrip(null);
        imageService.deleteImage(imageHash);
    }

    @Override
    @SneakyThrows
    public List<ImageSummary> getImageSummaryList(Trip trip) {
        AppUser appUser = userService.getLoggedUser();
        if (!trip.isPublic() && !this.checkIfContributor(trip, appUser)) {
            throw new ServerException("you do not have permission to see the images", HttpStatus.FORBIDDEN);
        }
        return imageService.getImageSummaryList(trip);
    }
}
