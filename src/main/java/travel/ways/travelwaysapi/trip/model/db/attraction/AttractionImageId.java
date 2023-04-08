package travel.ways.travelwaysapi.trip.model.db.attraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttractionImageId implements Serializable {
    private Long attractionId;
    private Long imageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttractionImageId that = (AttractionImageId) o;
        return Objects.equals(attractionId, that.attractionId) && Objects.equals(imageId, that.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attractionId, imageId);
    }
}
