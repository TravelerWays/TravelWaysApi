package travel.ways.travelwaysapi.trip.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.db.TripImage;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip extends BaseEntity {
    private String title;
    private boolean isPublic;
    private boolean isOpen;
    @Column(unique = true)
    private String hash;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<AppUserTrip> users = new HashSet<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<TripImage> images = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trip trip = (Trip) o;
        return Objects.equals(hash, trip.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hash);
    }

    public AppUser findOwner() {
        for (AppUserTrip appUserTrip : this.getUsers()) {
            if (appUserTrip.isOwner() && appUserTrip.getTrip().equals(this)) return appUserTrip.getUser();
        }
        return null;
    }

    public void addImage(Image image) {
        TripImage tripImage = new TripImage(this, image);
        this.images.add(tripImage);
        image.setTrip(tripImage);
    }

    @SneakyThrows
    public void addMainImage(Image image) {
        for (TripImage tripImage : images) {
            if (tripImage.isMain() && tripImage.getImage().equals(image)) {
                throw new ServerException("There is already a main image", HttpStatus.CONFLICT);
            }
        }
        TripImage tripImage = new TripImage(this, image);
        tripImage.setMain(true);
        this.images.add(tripImage);
        image.setTrip(tripImage);
    }

    public void removeImage(Image image) {
        for (Iterator<TripImage> iterator = images.iterator(); iterator.hasNext(); ) {
            TripImage tripImage = iterator.next();
            if (tripImage.getTrip().equals(this) && tripImage.getImage().equals(image)) {
                iterator.remove();
                tripImage.setImage(null);
                tripImage.setTrip(null);
            }
        }
    }

}
