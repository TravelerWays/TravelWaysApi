package travel.ways.travelwaysapi.user.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.*;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.map.model.db.ScratchMapCountry;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.db.TripInvitation;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser extends BaseEntity {
    @Column
    private String name;
    @Column
    private String surname;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean active;
    @Column(unique = true, nullable = false)
    private String hash;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Attraction.class, mappedBy = "user")
    @ToString.Exclude
    private Collection<Attraction> attractions = new ArrayList<>();

    @OneToMany(targetEntity = PasswordRecovery.class, mappedBy = "user")
    @ToString.Exclude
    private Collection<PasswordRecovery> passwordRecoveries = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = ScratchMapCountry.class, mappedBy = "user")
    @ToString.Exclude
    private Collection<ScratchMapCountry> scratchedCountries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @ToString.Exclude
    private Set<AppUserTrip> trips = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    private Image image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<TripInvitation> invitations = new HashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser appUser)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(email, appUser.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }
}
