package travel.ways.travelwaysapi.file.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.db.ImageHashOnly;
import travel.ways.travelwaysapi.file.repository.ImageRepository;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

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
            throw new ServerException("Bad file format", HttpStatus.UNPROCESSABLE_ENTITY);
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
    public Image getMainImageForTrip(Trip trip) {
        return imageRepository.findImageByTrip_TripAndTrip_isMainTrue(trip);
    }

    @Override
    @SneakyThrows
    public String getMainImageHash(Trip trip) {
        ImageHashOnly imageHashOnly = imageRepository.findImageHashByTrip_TripAndTrip_isMainTrue(trip);
        if (imageHashOnly == null) {
            log.debug("trip " + trip.getId() + " does not have main image");
            throw new ServerException("trip does not have main image", HttpStatus.NOT_FOUND);
        }
        return imageHashOnly.getHash();
    }

    @Override
    public void deleteImageByHash(String hash) {
        imageRepository.deleteImageByHash(hash);
    }


}
