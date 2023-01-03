package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.AttractionResponse;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
public class AttractionController {
    private final AttractionService attractionService;
    private final UserService userService;

    @PostMapping()
    public AttractionResponse add(@RequestBody CreateAttractionRequest createAttractionRequest) {
        var attraction = attractionService.createAttraction(createAttractionRequest);
        return AttractionResponse.of(attraction);
    }

    @GetMapping("/all")
    public List<AttractionResponse> getAll() {
        var user = userService.getLoggedUser();
        return attractionService.getUserAttraction(user).stream().map(AttractionResponse::of).toList();
    }
}
