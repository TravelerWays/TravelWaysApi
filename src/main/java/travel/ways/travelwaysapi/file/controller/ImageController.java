package travel.ways.travelwaysapi.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.service.shared.ImageService;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{imageHash}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageHash) {
        Image image = imageService.getImage(imageHash);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", image.getExtension())
                .body(image.getData());
    }

}
