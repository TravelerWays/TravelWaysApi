package travel.ways.travelwaysapi.file.service.shared;

import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.projection.ImageSummary;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import java.util.List;

public interface ImageService {
    Image saveImage(Image image);

    void deleteImage(Image image);

    Image createImage(String name, MultipartFile data);

    ImageSummary getImageSummary(String hash);

    String getMainImageHash(Trip trip);

    String getMainImageHash(Attraction attraction);

    void deleteImage(String hash);

    Image getImage(String hash);

    List<ImageSummary> getImageSummaryList(Trip trip);

    List<ImageSummary> getImageSummaryList(Attraction attraction);

    Boolean checkIfImageExistsInTrip(Trip trip, String imageHash);

    Boolean checkIfImageExistsInAttraction(Attraction attraction, String imageHash);

    Boolean isAttractionImage(String imageHash);

    Boolean isTripImage(String imageHash);
}
