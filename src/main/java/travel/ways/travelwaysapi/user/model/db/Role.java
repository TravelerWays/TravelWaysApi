package travel.ways.travelwaysapi.user.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    @Column(unique = true, updatable = false)
    private String name;
}
