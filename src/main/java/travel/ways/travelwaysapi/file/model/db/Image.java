package travel.ways.travelwaysapi.file.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.trip.model.db.AttractionImage;
import travel.ways.travelwaysapi.trip.model.db.TripImage;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.*;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(nullable = false)
    private byte[] data;
    @Column(nullable = false, length = 20)
    private String extension;
    @Column(nullable = false, unique = true)
    private String hash;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private TripImage trip;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private AttractionImage attraction;

    @OneToOne(mappedBy = "image")
    private AppUser user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image image)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(hash, image.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hash);
    }
}
