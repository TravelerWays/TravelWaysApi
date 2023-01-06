package travel.ways.travelwaysapi.file.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.model.ImageWithoutData;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.repository.ImageRepository;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    @Lazy
    private final TripService tripService;
    private final UserService userService;

    @Override
    @Transactional
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public void deleteImage(Image image) {
        imageRepository.delete(image);
    }

    @Override
    @SneakyThrows
    @Transactional
    public Image createImage(String name, MultipartFile data) {
        String extension = data.getContentType();
        if (extension == null || !(extension.equals(MediaType.IMAGE_JPEG_VALUE) || extension.equals(MediaType.IMAGE_PNG_VALUE))) {
            log.debug(extension + "is a bad file format, should be: " + MediaType.IMAGE_JPEG_VALUE + " or " + MediaType.IMAGE_PNG_VALUE);
            throw new ServerException("Bad file format", HttpStatus.BAD_REQUEST);
        }
        Image image = new Image();
        image.setData(data.getBytes());
        image.setName(name);
        image.setExtension(extension);
        image.setHash(UUID.randomUUID().toString());
        image = this.saveImage(image);
        return image;
    }

    @Override
    @SneakyThrows
    public ImageWithoutData getImageWithoutData(String hash) {
        ImageWithoutData imageWithoutData = imageRepository.findImageWithoutDataByHash(hash);
        if (imageWithoutData == null) {
            log.debug("can not find image for hash " + hash);
            throw new ServerException("can not find image for the hash", HttpStatus.NOT_FOUND);
        }
        return imageWithoutData;
    }

    @Override
    @SneakyThrows
    public String getTripMainImageHash(Trip trip) {
        ImageWithoutData imageWithoutData = imageRepository.findImageWithoutDataByTripTripAndTripIsMainTrue(trip);
        if (imageWithoutData == null) {
            return null;
        }
        return imageWithoutData.getHash();
    }

    @Override
    public void deleteImage(String hash) {
        imageRepository.deleteImageByHash(hash);
    }

    @Override
    @SneakyThrows
    public Image getImage(String hash) {
        AppUser appUser = userService.getLoggedUser();

        Image image = imageRepository.findByHash(hash);

        if (image == null) {
            log.debug("can not find image for hash " + hash);
            throw new ServerException("can not find image for the hash", HttpStatus.NOT_FOUND);
        }

        if (image.getTrip() != null) {
            if (!image.getTrip().getTrip().isPublic() && !tripService.checkIfContributor(image.getTrip().getTrip(), appUser)) {
                throw new ServerException("You do not have permission to get the image", HttpStatus.FORBIDDEN);
            }
        }

        return image;
    }

    @Override
    public List<ImageWithoutData> getAllImagesWithoutDataForTrip(Trip trip) {
        return imageRepository.findAllWithoutDataByTripTrip(trip);
    }


    @Override
    public Boolean checkIfImageExistsInTrip(Trip trip, String imageHash) {

        return imageRepository.existsImageByHashAndTripTrip(imageHash, trip);
    }


}
