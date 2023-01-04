package travel.ways.travelwaysapi.file.service.shared;

import travel.ways.travelwaysapi.file.model.db.Image;

public interface ImageService {
    Image saveImage(Image image);

    void deleteImage(Image image);

    Image createImage(String name, byte[] data);

}
