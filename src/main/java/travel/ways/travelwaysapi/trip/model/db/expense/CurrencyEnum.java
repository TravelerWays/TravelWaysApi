package travel.ways.travelwaysapi.trip.model.db.expense;

public enum CurrencyEnum {
    PLN(0),
    EUR(1),
    GBP(2),
    USD(3);

    private final int value;

    CurrencyEnum(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}
