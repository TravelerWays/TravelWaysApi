package travel.ways.travelwaysapi._core.model.db;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    protected Date createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    protected Date updateAt;
}
