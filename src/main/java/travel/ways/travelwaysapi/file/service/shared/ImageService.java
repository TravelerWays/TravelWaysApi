package travel.ways.travelwaysapi.file.service.shared;

import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface ImageService {
    Long saveImage(Image image);

    void deleteImage(Image image);

    Long createImage(String name, MultipartFile data);

    ImageSummaryDto getImageSummary(String hash);

    ImageSummaryDto getImageSummary(Long id);

    void deleteImage(String hash);

    Image getImage(String hash);

    Image getImage(Long id);

    List<ImageSummaryDto> getImageSummaryList(List<Long> ids);

    ImageSummaryDto getImageSummary(AppUser user);

}
