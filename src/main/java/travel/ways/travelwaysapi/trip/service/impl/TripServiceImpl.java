package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageToTripRequest;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.AppUserTrip;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDto;
import travel.ways.travelwaysapi.trip.repository.TripRepository;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();

    @Override
    @Transactional
    @SneakyThrows
    public Trip createTrip(CreateTripRequest request) {
        Trip trip = new Trip();
        trip.setHash(UUID.randomUUID().toString());
        trip.setOpen(true);
        trip.setPublic(request.getIsPublic());
        trip.setTitle(request.getTitle());
        trip.setDescription(request.getDescription());
        trip = tripRepository.save(trip);
        userService.getLoggedUser().addTrip(trip, true);

        Image image = imageService.createImage(request.getData().getOriginalFilename(), request.getData());

        trip.addMainImage(image);
        return trip;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteTrip(Trip trip) {
        AppUser owner = trip.findOwner();
        if (!userService.getLoggedUser().equals(owner)) {
            throw new ServerException("You don't have permission to delete the trip", HttpStatus.UNAUTHORIZED);
        }

        log.debug("removing trip with id: " + trip.getId());
        this.deleteMainImage(trip);
        owner.removeTrip(trip);
        tripRepository.delete(trip);
    }

    @Override
    @SneakyThrows
    public Trip getByHash(String hash) {
        Trip trip = tripRepository.findByHash(hash);
        if (trip == null) {
            throw new ServerException("Can't find trip", HttpStatus.NOT_FOUND);
        }
        return trip;
    }

    @Override
    public List<TripDto> getAllTripsForUser(AppUser appUser) {
        AppUser loggedUser = userService.getLoggedUser();
        boolean showPrivate = loggedUser.equals(appUser);

        List<TripDto> trips = new ArrayList<>();
        for (AppUserTrip appUserTrip : appUser.getTrips()) {
            if (!showPrivate) {
                if (!appUserTrip.getTrip().isPublic() && !checkIfContributor(appUserTrip.getTrip(), loggedUser)) {
                    continue;
                }
            }
            TripDto tripDto = mapper.map(appUserTrip.getTrip(), TripDto.class);
            trips.add(tripDto);
        }
        return trips;
    }

    @Override
    @SneakyThrows
    public Image getMainImage(String hash) {
        Trip trip = getByHash(hash);
        return this.getMainImage(trip);
    }

    @Override
    @SneakyThrows
    public Image getMainImage(Trip trip) {
        Image image = imageService.getMainImageForTrip(trip);
        if (image != null) {
            return image;
        }
        throw new ServerException("The trip does not have main image", HttpStatus.NOT_FOUND);

    }

    public String getMainImageHash(Trip trip) {
        return imageService.getMainImageHash(trip);
    }

    @Override
    @Transactional
    @SneakyThrows
    public void closeTrip(String hash) {
        Trip trip = this.getByHash(hash);
        if (!trip.findOwner().equals(userService.getLoggedUser())) {
            throw new ServerException("You do not have permission to open the trip", HttpStatus.FORBIDDEN);
        }
        trip.setOpen(false);
    }

    @Override
    @Transactional
    @SneakyThrows
    public void openTrip(String hash) {
        Trip trip = this.getByHash(hash);
        if (!trip.findOwner().equals(userService.getLoggedUser())) {
            throw new ServerException("You do not have permission to open the trip", HttpStatus.FORBIDDEN);
        }
        trip.setOpen(true);
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteMainImage(Trip trip) {
        if (!userService.getLoggedUser().equals(trip.findOwner())) {
            throw new ServerException("You don't have permission to delete the image", HttpStatus.UNAUTHORIZED);
        }
        log.debug("removing main image from trip with id: " + trip.getId());
        String hashImageToRemove = this.getMainImageHash(trip);
        trip.removeImage(hashImageToRemove);
        tripRepository.save(trip);
        imageService.deleteImageByHash(hashImageToRemove);
    }

    @Override
    @Transactional
    @SneakyThrows
    public Trip editTrip(EditTripRequest request) {
        AppUser appUser = userService.getLoggedUser();
        Trip trip = this.getByHash(request.getHash());
        if (!appUser.equals(trip.findOwner())) {
            throw new ServerException("You don't have permission to edit the trip", HttpStatus.UNAUTHORIZED);
        }
        trip.setTitle(request.getTitle());
        trip.setDescription(request.getDescription());
        trip.setPublic(request.getIsPublic());
        return trip;
    }

    @Override
    @Transactional
    @SneakyThrows
    public Image editMainImage(Trip trip, MultipartFile data) {
        if (!userService.getLoggedUser().equals(trip.findOwner())) {
            throw new ServerException("You don't have permission to edit the image", HttpStatus.FORBIDDEN);
        }
        Image image = imageService.createImage(data.getOriginalFilename(), data);
        this.deleteMainImage(trip);
        trip.addMainImage(image);
        return image;
    }

    @Override
    @Transactional
    @SneakyThrows
    public Image addImage(AddImageToTripRequest request) {
        Trip trip = this.getByHash(request.getHash());
        if (!this.checkIfContributor(trip, userService.getLoggedUser())) {
            throw new ServerException("You don't have permission to add image", HttpStatus.FORBIDDEN);
        }
        if (request.isMain()) {
            return editMainImage(trip, request.getData());
        }
        Image image = imageService.createImage(request.getData().getOriginalFilename(), request.getData());
        trip.addImage(image);
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


}
