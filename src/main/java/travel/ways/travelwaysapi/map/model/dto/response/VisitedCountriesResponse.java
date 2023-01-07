package travel.ways.travelwaysapi.map.model.dto.response;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VisitedCountriesResponse {
    private List<String> visitedCountries;
    private List<String> plannedCountries;
}
