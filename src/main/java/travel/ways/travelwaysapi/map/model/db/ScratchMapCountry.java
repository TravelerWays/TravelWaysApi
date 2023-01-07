package travel.ways.travelwaysapi.map.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScratchMapCountry extends BaseEntity {
    @Column
    private String countryCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private AppUser user;
}
