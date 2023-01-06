package travel.ways.travelwaysapi.user.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.trip.model.db.AppUserTrip;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class AppUser extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "active", nullable = false)
    private boolean active;
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Attraction.class, mappedBy = "user")
    private Collection<Attraction> attractions = new ArrayList<>();

    @OneToMany(targetEntity = PasswordRecovery.class, mappedBy = "user")
    private Collection<PasswordRecovery> passwordRecoveries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<AppUserTrip> trips = new HashSet<>();

    public AppUser(String name, String surname, String username, String email, String password, Collection<Role> roles) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public static AppUser of(@NotNull CreateUserRequest request) {
        return new AppUser(
                request.getName(),
                request.getSurname(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                new ArrayList<>()
        );
    }

    public static AppUser of(@NotNull CreateUserRequest request, @NotNull List<Role> roles) {
        return new AppUser(
                request.getName(),
                request.getSurname(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                roles
        );
    }

    public void addTrip(Trip trip) {
        AppUserTrip appUserTrip = new AppUserTrip(trip, this);
        this.trips.add(appUserTrip);
        trip.getUsers().add(appUserTrip);
    }

    public void addTrip(Trip trip, Boolean owner) {
        AppUserTrip appUserTrip = new AppUserTrip(trip, this);
        if (owner) appUserTrip.setOwner(true);
        this.trips.add(appUserTrip);
        trip.getUsers().add(appUserTrip);
    }

    public void removeTrip(Trip trip) {
        for (Iterator<AppUserTrip> iterator = trips.iterator(); iterator.hasNext(); ) {
            AppUserTrip appUserTrip = iterator.next();
            if (appUserTrip.getUser().equals(this) && appUserTrip.getTrip().equals(trip)) {
                iterator.remove();
                appUserTrip.getTrip().getUsers().remove(appUserTrip);
                appUserTrip.setUser(null);
                appUserTrip.setTrip(null);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser appUser)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(email, appUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }
}
