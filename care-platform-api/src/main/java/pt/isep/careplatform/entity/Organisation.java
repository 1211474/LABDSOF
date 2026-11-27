package pt.isep.careplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "organisations")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "official_email", length = 320)
    private String officialEmail;

    @Column(name = "official_phone", length = 40)
    private String officialPhone;

    @Column(length = 2048)
    private String website;

    @Column(name = "coarse_location", length = 120)
    private String coarseLocation;

    protected Organisation() {
    }

    public Organisation(String name, boolean verified) {
        this.name = name;
        this.verified = verified;
        this.verifiedAt = verified ? Instant.now().truncatedTo(ChronoUnit.MICROS) : null;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isVerified() {
        return verified;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public String getOfficialPhone() {
        return officialPhone;
    }

    public String getWebsite() {
        return website;
    }

    public String getCoarseLocation() {
        return coarseLocation;
    }

    public void updatePublicDetails(
            String officialEmail,
            String officialPhone,
            String website,
            String coarseLocation
    ) {
        this.officialEmail = officialEmail;
        this.officialPhone = officialPhone;
        this.website = website;
        this.coarseLocation = coarseLocation;
    }

    public void setVerified(boolean verified) {
        if (this.verified == verified) {
            return;
        }

        this.verified = verified;
        this.verifiedAt = verified ? Instant.now().truncatedTo(ChronoUnit.MICROS) : null;
    }
}
