package travel.ways.travelwaysapi.trip.model.db.expense;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Currency {
    @Id
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    protected Date createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    protected Date updateAt;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    public Currency(Long id, CurrencyEnum currency) {
        this.id = id;
        this.currency = currency;
    }
}
