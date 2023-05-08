package travel.ways.travelwaysapi.user.service.impl;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Notification;
import travel.ways.travelwaysapi.user.model.dto.NotificationModel;
import travel.ways.travelwaysapi.user.repository.NotificationRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.shared.NotificationService;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @SneakyThrows
    public void sendNotification(NotificationModel model) {
        var targetUser = userRepository.findByHash(model.getTargetUser());
        if (targetUser == null) {
            throw new ServerException("User not found", HttpStatus.NOT_FOUND);
        }
        notificationRepository.save(new Notification(
                UUID.randomUUID().toString(),
                targetUser,
                model.getContent(),
                false
        ));
    }

    @Override
    public List<Notification> getUserNotification(AppUser user) {
        return notificationRepository.findByUser(user).stream().filter(x -> !x.isRead()).toList();
    }


}
