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
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.file.repository.ImageRepository;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.Optional;
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
    public Long saveImage(Image image) {
        return imageRepository.save(image).getId();
    }

    @Override
    public void deleteImage(Image image) {
        imageRepository.delete(image);
    }

    @Override
    @SneakyThrows
    @Transactional
    public Long createImage(String name, MultipartFile data) {
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
        return this.saveImage(image);
    }

    @Override
    @SneakyThrows
    public ImageSummaryDto getImageSummary(String hash) {
        var imageSummary = imageRepository.findImageSummaryByHash(hash);
        if (imageSummary == null) {
            log.debug("can not find image for hash " + hash);
            throw new ServerException("can not find image for the hash", HttpStatus.NOT_FOUND);
        }
        return ImageSummaryDto.of(imageSummary);
    }

    @Override
    @SneakyThrows
    public ImageSummaryDto getImageSummary(Long id) {
        var imageSummary = imageRepository.findImageSummaryById(id);
        if (imageSummary == null) {
            log.debug("can not find image for id " + id);
            throw new ServerException("can not find image for the id", HttpStatus.NOT_FOUND);
        }
        return ImageSummaryDto.of(imageSummary);
    }

    @Override
    public void deleteImage(String hash) {
        imageRepository.deleteImageByHash(hash);
    }

    @Override
    @SneakyThrows
    public Image getImage(String hash) {
        var image = imageRepository.findByHash(hash);

        throwErrorIfImageDoesntExists(image);
        throwErrorIfUserDoesntHaveAccessToImage(image.get());

        return image.get();
    }

    @Override
    @SneakyThrows
    public Image getImage(Long id) {
        var image = imageRepository.findById(id);
        throwErrorIfImageDoesntExists(image);
        throwErrorIfUserDoesntHaveAccessToImage(image.get());
        return image.get();
    }

    @Override
    public List<ImageSummaryDto> getImageSummaryList(List<Long> ids) {
        return imageRepository.getAllByIdIn(ids).stream().map(ImageSummaryDto::of).toList();
    }

    @Override
    public ImageSummaryDto getImageSummary(AppUser user) {
        return ImageSummaryDto.of(imageRepository.findImageSummaryByUserIs(user));
    }

    @SneakyThrows
    private void throwErrorIfUserDoesntHaveAccessToImage(Image image) {
        var loggedUser = userService.getLoggedUser();

        if (image.getTrip() != null) {
            if (!image.getTrip().getTrip().isPublic() && !tripService.checkIfContributor(image.getTrip().getTrip(), loggedUser)) {
                throw new ServerException("You do not have permission to get the image", HttpStatus.FORBIDDEN);
            }
        }
    }

    @SneakyThrows
    private void throwErrorIfImageDoesntExists(Optional<Image> image) {
        if (image.isEmpty()) {
            log.debug("can not find image");
            throw new ServerException("can not find image for the hash", HttpStatus.NOT_FOUND);
        }
    }
}
