package travel.ways.travelwaysapi.file.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "trip_image")
public class TripImage {
    @EmbeddedId
    private TripImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @MapsId("tripId")
    private Trip trip;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId("imageId")
    @JsonBackReference
    private Image image;

    @Column(name = "is_main")
    private boolean isMain;

    public TripImage() {
    }

    public TripImage(Trip trip, Image image) {
        this.id = new TripImageId(trip.getId(), image.getId());
        this.trip = trip;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripImage tripImage = (TripImage) o;
        return Objects.equals(trip, tripImage.trip) && Objects.equals(image, tripImage.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trip, image);
    }

    @Override
    public String toString() {
        return "TripImage{" +
                "id=" + id +
                ", isMain=" + isMain +
                '}';
    }
}
