package travel.ways.travelwaysapi.trip.model.db.expense;
    public enum ExpenseCategoryEnum {
        Transport(0),
        Restaurants(1),
        Food(2),
        Attractions(3),
        Hotels(4);

        private final int value;

        ExpenseCategoryEnum(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }

    }
