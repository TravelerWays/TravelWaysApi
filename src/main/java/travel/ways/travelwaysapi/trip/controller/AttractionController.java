package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.model.projection.ImageSummary;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditAttractionMainImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.AttractionResponse;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
public class AttractionController {
    private final AttractionService attractionService;
    private final UserService userService;
    private final ImageService imageService;

    @GetMapping("/{attractionHash}")
    public AttractionResponse getAttraction(@PathVariable String attractionHash) {
        Attraction attraction = attractionService.getAttraction(attractionHash);
        return AttractionResponse.of(attraction, attractionService.getImageSummaryList(attraction));
    }

    @PostMapping
    public AttractionResponse add(@Valid @RequestBody CreateAttractionRequest createAttractionRequest) {
        var attraction = attractionService.createAttraction(createAttractionRequest);
        return AttractionResponse.of(attraction, attractionService.getImageSummaryList(attraction));
    }

    @GetMapping("/all/{userHash}")
    public List<AttractionResponse> getAll(@PathVariable String userHash) {
        var user = userService.getByHash(userHash);
        return attractionService.getUserAttractions(user);
    }

    @GetMapping("/all")
    public List<AttractionResponse> getAllLoggedUser() {
        return attractionService.getUserAttractions(userService.getLoggedUser());
    }

    @DeleteMapping("/{attractionHash}")
    public BaseResponse deleteAttraction(@PathVariable String attractionHash) {
        attractionService.deleteAttraction(attractionHash);
        return new BaseResponse(true, "attraction removed");
    }

    @PutMapping("/edit")
    public AttractionResponse editAttraction(@RequestBody EditAttractionRequest editAttractionRequest) {
        Attraction attraction = attractionService.editAttraction(editAttractionRequest);
        return AttractionResponse.of(attraction, attractionService.getImageSummaryList(attraction));
    }

    @PutMapping("/edit/main-image")
    public BaseResponse editMainImage(@Valid @RequestBody EditAttractionMainImageRequest editMainImageRequest) {

        Attraction attraction = attractionService.getAttraction(editMainImageRequest.getAttractionHash());
        Image image = attractionService.editMainImage(attraction, editMainImageRequest.getImageHash());
        if (image == null) {
            return new BaseResponse(true, "main image removed");
        }
        return new BaseResponse(true, "main image changed");
    }

    @PostMapping(value = "/{attractionHash}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageSummary addImageToAttraction(@PathVariable String attractionHash, @Valid @ModelAttribute AddImageRequest addImageRequest) {
        Image image = attractionService.addImage(addImageRequest, attractionHash);
        return imageService.getImageSummary(image.getHash());
    }

    @DeleteMapping("/image/{imageHash}")
    public BaseResponse deleteImage(@PathVariable String imageHash){
        attractionService.deleteImage(imageHash);
        return new BaseResponse(true, "image deleted");
    }
}
