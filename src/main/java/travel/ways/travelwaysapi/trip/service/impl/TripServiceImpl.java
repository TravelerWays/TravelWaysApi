package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.db.TripImage;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.AppUserTrip;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
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
    public Trip createTrip(CreateTripRequest request, AppUser user) {
        Trip trip = new Trip();
        trip.setHash(UUID.randomUUID().toString());
        trip.setOpen(true);
        if (request.getIsPublic().equalsIgnoreCase("true")) trip.setPublic(true);
        else if (request.getIsPublic().equalsIgnoreCase("false")) trip.setPublic(true);
        else throw new ServerException("Bad request", HttpStatus.BAD_REQUEST);
        trip.setTitle(request.getTitle());
        trip = tripRepository.save(trip);
        user.addTrip(trip, true);

        Image image = imageService.createImage(trip.getTitle() + "_" + user.getUsername(), request.getData().getBytes());

        trip.addMainImage(image);

        return trip;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteTrip(Trip trip) {
        AppUser appUser = trip.findOwner();
        log.debug("removing trip with id: " + trip.getId());
        appUser.removeTrip(trip);
        this.deleteMainImage(trip);
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
        List<TripDto> trips = new ArrayList<>();
        for (AppUserTrip appUserTrip : appUser.getTrips()) {
            System.out.println(appUserTrip.getTrip().getId());

            TripDto tripDto = mapper.map(appUserTrip.getTrip(), TripDto.class);
            trips.add(tripDto);
        }
        return trips;
    }

    @Override
    @SneakyThrows
    public Image getMainImage(String hash) {
        Trip trip = getByHash(hash);
        for (TripImage tripImage : trip.getImages()) {
            if (tripImage.isMain()) return tripImage.getImage();
        }
        throw new ServerException("The trip does not have main image", HttpStatus.NOT_FOUND);
    }

    @Override
    public Image getMainImage(Trip trip) {
        for (TripImage tripImage : trip.getImages()) {
            if (tripImage.isMain()) return tripImage.getImage();
        }
        return null;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteMainImage(Trip trip) {
        log.debug("removing main image from trip with id: " + trip.getId());
        Image imageToRemove = this.getMainImage(trip);
        trip.removeImage(imageToRemove);
        tripRepository.save(trip);
        imageService.deleteImage(imageToRemove);
    }

    @Override
    @Transactional
    public Trip editTitle(Trip trip, String title) {
        trip.setTitle(title);
        return trip;
    }

    @Override
    @Transactional
    public Trip editIsPublic(Trip trip, Boolean isPublic) {
        trip.setPublic(isPublic);
        return trip;
    }

    @Override
    @Transactional
    public void editMainImage(Trip trip, Image image) {
        this.deleteMainImage(trip);
        trip.addMainImage(image);
    }

    @Override
    @Transactional
    public void addImage(Trip trip, Image image) {
        trip.addImage(image);
    }

    @Override
    public boolean checkIfContributor(Trip trip, AppUser appUser) {
        if (appUser == null || trip == null) return false;
        for (AppUserTrip appUserTrip : trip.getUsers()) {
            if (appUserTrip.getUser() == appUser) return true;
        }
        return false;
    }


}
