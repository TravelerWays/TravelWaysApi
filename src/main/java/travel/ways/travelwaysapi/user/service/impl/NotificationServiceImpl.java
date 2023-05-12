package travel.ways.travelwaysapi.user.service.impl;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                model.getRelatedObjectHash(),
                model.getType(),
                false
        ));
    }

    @Override
    public List<Notification> getUserNotification(AppUser user) {
        return notificationRepository.findUserNotification(user, PageRequest.of(0, 10)).stream().toList();
    }

    @Override
    @Transactional
    public void markAllUserNotificationAsRead(AppUser user) {
        notificationRepository.markNotificationAsRead(user);
    }

    @Override
    @Transactional
    public void removeNotificationForObject(String relatedObject) {
        notificationRepository.deleteByRelatedObjectHash(relatedObject);
    }


}
