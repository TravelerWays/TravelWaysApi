package travel.way.travelwayapi.user.models.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.way.travelwayapi._core.models.BaseEntity;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    private String name;
}
