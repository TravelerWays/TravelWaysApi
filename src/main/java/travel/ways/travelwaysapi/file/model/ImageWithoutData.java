package travel.ways.travelwaysapi.file.model;

import org.springframework.beans.factory.annotation.Value;

public interface ImageWithoutData {
    String getHash();

    String getExtension();

    String getName();

    @Value("#{target.trip.main}")
    Boolean getMain();

}
