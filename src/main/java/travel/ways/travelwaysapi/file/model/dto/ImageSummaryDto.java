package travel.ways.travelwaysapi.file.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi.file.model.projection.ImageSummary;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageSummaryDto {
    private String hash;
    private String extension;
    private String name;

    public static ImageSummaryDto of(ImageSummary imageSummary) {
        return new ImageSummaryDto(
                imageSummary.getHash(),
                imageSummary.getExtension(),
                imageSummary.getName()
        );
    }
}
