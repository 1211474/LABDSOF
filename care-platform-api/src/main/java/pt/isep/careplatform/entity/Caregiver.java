package pt.isep.careplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "caregivers")
public class Caregiver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "phone_number", nullable = false, length = 40)
    private String phoneNumber;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "coarse_location", nullable = false, length = 120)
    private String coarseLocation;

    protected Caregiver() {
    }

    public Caregiver(User user, String displayName, String phoneNumber, String coarseLocation) {
        this.user = user;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.coarseLocation = coarseLocation;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCoarseLocation() {
        return coarseLocation;
    }

    public void update(String displayName, String phoneNumber, String coarseLocation) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.coarseLocation = coarseLocation;
    }
}
