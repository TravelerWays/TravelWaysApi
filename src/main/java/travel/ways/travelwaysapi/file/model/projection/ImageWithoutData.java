package travel.ways.travelwaysapi.file.model.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ImageWithoutData {
    String getHash();

    String getExtension();

    String getName();

    @Value("#{target.trip.main}")
    Boolean getMain();

}
