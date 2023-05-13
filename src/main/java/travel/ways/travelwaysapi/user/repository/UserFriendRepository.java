package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.UserFriends;
import travel.ways.travelwaysapi.user.model.enums.FriendsStatus;

import java.util.List;

public interface UserFriendRepository extends JpaRepository<UserFriends, Long> {
    @Query("select u from UserFriends u where u.friend.hash = ?1 and u.status = ?2")
    List<UserFriends> findUserInvitation(String hash, FriendsStatus status);

    UserFriends findByHash(String hash);

    @Query("select (count(u) > 0) from UserFriends u where ((u.user = ?1 and u.friend = ?2) or (u.user = ?2 and u.friend = ?1)) and (u.status = 2)")
    boolean existsByUserAndFriend(AppUser user, AppUser friend);

    @Query("select (count(u) > 0) from UserFriends u where ((u.user = ?1 and u.friend = ?2) or (u.user = ?2 and u.friend = ?1)) and (u.status = 1)")
    boolean hasPendingInvitation(AppUser user, AppUser friend);

    @Query("select u.friend from UserFriends u where u.user = ?1 and u.status = 2")
    List<AppUser> findUserFriends(AppUser user);
}
