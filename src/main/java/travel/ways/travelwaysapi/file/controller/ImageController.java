package travel.ways.travelwaysapi.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final TripService tripService;
    private final ImageService imageService;
    private final AttractionService attractionService;

    @DeleteMapping(value = "/{imageHash}")
    public BaseResponse deleteImage(@PathVariable String imageHash) {
        if(imageService.isAttractionImage(imageHash)){
            attractionService.deleteImage(imageHash);
        }else if(imageService.isTripImage(imageHash)){
            tripService.deleteImage(imageHash);
        }else{
            imageService.deleteImage(imageHash);
        }
        return new BaseResponse(true, "deleted image");
    }

    @PutMapping(value = "/trip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageWithoutData addImageToTrip(@Valid @ModelAttribute AddImageRequest addImageRequest) {
        Image image = tripService.addImage(addImageRequest);
        return imageService.getImageWithoutData(image.getHash());
    }

    @PutMapping(value = "/attraction", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageWithoutData addImageToAttraction(@Valid @ModelAttribute AddImageRequest addImageRequest) {
        Image image = attractionService.addImage(addImageRequest);
        return imageService.getImageWithoutData(image.getHash());
    }

    @GetMapping("/{imageHash}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageHash) {
        Image image = imageService.getImage(imageHash);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", image.getExtension())
                .body(image.getData());
    }

}
