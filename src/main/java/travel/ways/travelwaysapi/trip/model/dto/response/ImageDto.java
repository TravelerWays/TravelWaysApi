package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private String hash;
    private String extension;
    private String name;
    private boolean isMain;

    public static ImageDto of(ImageSummaryDto summaryDto, boolean isMain) {
        return new ImageDto(
                summaryDto.getHash(),
                summaryDto.getExtension(),
                summaryDto.getName(),
                isMain
        );
    }
}
