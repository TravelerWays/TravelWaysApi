package travel.ways.travelwaysapi.file.model.projection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;
import travel.ways.travelwaysapi.trip.model.db.AttractionImage;
import travel.ways.travelwaysapi.trip.model.db.TripImage;

public interface ImageSummary {
    @JsonIgnore
    @Value("#{target.trip}")
    TripImage getTripImage();

    @JsonIgnore
    @Value("#{target.attraction}")
    AttractionImage getAttractionImage();

    String getHash();

    String getExtension();

    String getName();

    default Boolean getMain() {
        if (getTripImage() != null) {
            return getTripImage().isMain();
        }
        if (getAttractionImage() != null) {
            return getAttractionImage().isMain();
        }
        return true;
    }

}
