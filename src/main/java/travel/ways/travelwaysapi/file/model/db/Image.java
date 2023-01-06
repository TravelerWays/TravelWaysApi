package travel.ways.travelwaysapi.file.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.trip.model.db.TripImage;

import javax.persistence.*;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "image")
public class Image extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "data", nullable = false)
    private byte[] data;
    @Column(name = "extension", nullable = false, length = 20)
    private String extension;
    @Column(name = "hash", nullable = false, unique = true)
    private String hash;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private TripImage trip;

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
