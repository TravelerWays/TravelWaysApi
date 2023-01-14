package travel.ways.travelwaysapi.trip.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.file.model.db.Image;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "attraction_image")
public class AttractionImage {

    @EmbeddedId
    private AttractionImageId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JsonBackReference
    @MapsId("attractionId")
    private Attraction attraction;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    @MapsId("imageId")
    private Image image;

    @Column(name = "is_main", nullable = false)
    private boolean isMain;

    public AttractionImage(Attraction attraction, Image image) {
        this.id = new AttractionImageId(attraction.getId(), image.getId());
        this.attraction = attraction;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttractionImage that = (AttractionImage) o;
        return isMain == that.isMain && Objects.equals(id, that.id) && Objects.equals(attraction, that.attraction) && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, attraction, image, isMain);
    }

    @Override
    public String toString() {
        return "AttractionImage{" +
                "id=" + id +
                ", isMain=" + isMain +
                '}';
    }
}
