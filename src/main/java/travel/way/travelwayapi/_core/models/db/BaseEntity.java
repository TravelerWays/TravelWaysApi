package travel.way.travelwayapi._core.models.db;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    public Date createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    public Date updateAt;
}
