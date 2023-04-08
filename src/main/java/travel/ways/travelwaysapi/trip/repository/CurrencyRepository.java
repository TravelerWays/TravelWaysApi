package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.trip.model.db.expense.Currency;
import travel.ways.travelwaysapi.trip.model.db.expense.CurrencyEnum;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Currency findByCurrency(CurrencyEnum currency);
    boolean existsByCurrency(CurrencyEnum currency);
}
