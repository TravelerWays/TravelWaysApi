package travel.ways.travelwaysapi.file.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.repository.ImageRepository;
import travel.ways.travelwaysapi.file.service.shared.ImageService;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public void deleteImage(Image image) {
        imageRepository.delete(image);
    }

    @Override
    @SneakyThrows
    @Transactional
    public Image createImage(String name, byte[] data) {
        Image image = new Image();
        image.setData(data);
        image.setName(name);
        image.setExtension(URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data)));
        image.setHash(UUID.randomUUID().toString());
        image = this.saveImage(image);
        return image;
    }
}
