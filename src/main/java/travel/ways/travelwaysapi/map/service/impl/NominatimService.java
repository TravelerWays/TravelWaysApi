package travel.ways.travelwaysapi.map.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import travel.ways.travelwaysapi._core.properity.NominatimProperty;
import travel.ways.travelwaysapi.map.model.dto.osm.LocationDto;
import travel.ways.travelwaysapi.map.service.shared.SearchService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NominatimService implements SearchService {
    private final NominatimProperty nominatimProperty;
    private final ObjectMapper mapper;

    private final String baseParams = "?format=json&addressdetails=1&limit=10&";

    @Override
    @SneakyThrows
    public List<LocationDto> Search(String query) {
        return makeRequest(URI.create(nominatimProperty.getUrl() + "search" + baseParams + "q=" + query));
    }

    @Override
    public List<LocationDto> Search(double lat, double lon) {
        return makeRequest(URI.create(nominatimProperty.getUrl() + "reverse" + baseParams + "lat=" + lat + "&lon=" + lon));
    }

    @SneakyThrows
    private List<LocationDto> makeRequest(URI uri) {
        var client = HttpClient.newHttpClient();

        var request = HttpRequest
                .newBuilder(uri)
                .header("accept", "application/json")
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            return mapper.readValue(response.body(), new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            return List.of(mapper.readValue(response.body(), LocationDto.class));
        }
    }
}
