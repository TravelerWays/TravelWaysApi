package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.response.UserResponse;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Override
    @Transactional
    public AppUser getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserResponse> getAll() {
        List<UserResponse> users = new ArrayList<>();
        UserResponse userResponse;
        for(AppUser appUser : userRepository.findAll()){
            userResponse = UserResponse.of(appUser, imageService.getImageSummary(appUser));
            users.add(userResponse);
        }
        return users;
    }

    @Override
    public AppUser getByHash(String hash) {
        return userRepository.findByHash(hash);
    }

    @Override
    public void save(AppUser appUser) {
        userRepository.save(appUser);
    }

    @Override
    public AppUser getLoggedUser() {
        return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public AppUser getTripOwner(Trip trip) {
        return userRepository.findOwnerByTripsTripAndTripsIsOwnerTrue(trip);
    }

    @Override
    @Transactional
    @SneakyThrows
    public Image addImage(AddImageRequest request) {
        AppUser user = this.getByHash(request.getHash());
        if (!user.equals(this.getLoggedUser())) {
            throw new ServerException("You don't have permission to add image", HttpStatus.FORBIDDEN);
        }
        Image image = imageService.createImage(request.getImageData().getOriginalFilename(), request.getImageData());
        user.setImage(image);
        return image;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteImage(String userHash) {
        AppUser user = this.getByHash(userHash);
        if (!user.equals(this.getLoggedUser())) {
            throw new ServerException("You don't have permission to delete image", HttpStatus.FORBIDDEN);
        }
        String imageHash = imageService.getImageSummary(user).getHash();
        user.setImage(null);
        imageService.deleteImage(imageHash);
    }

}
