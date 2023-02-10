package travel.ways.travelwaysapi.file.service.shared;

import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface ImageService {
    Image saveImage(Image image);

    void deleteImage(Image image);

    Image createImage(String name, MultipartFile data);

    ImageSummaryDto getImageSummary(String hash);

    String getMainImageHash(Trip trip);

    String getMainImageHash(Attraction attraction);

    void deleteImage(String hash);

    Image getImage(String hash);

    List<ImageSummaryDto> getImageSummaryList(List<Long> ids);

    ImageSummaryDto getImageSummary(AppUser user);

}
