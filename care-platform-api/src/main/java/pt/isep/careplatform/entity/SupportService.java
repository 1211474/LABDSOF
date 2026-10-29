package pt.isep.careplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_services")
public class SupportService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ServiceType type;

    @Column(name = "coarse_location", nullable = false, length = 120)
    private String coarseLocation;

    @Column(name = "operating_hours", nullable = false, length = 120)
    private String operatingHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 50)
    private ServiceStatus currentStatus;

    @Column(name = "contact_phone", nullable = false, length = 40)
    private String contactPhone;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    protected SupportService() {
    }

    public SupportService(
            Organisation organisation,
            String name,
            ServiceType type,
            String coarseLocation,
            String operatingHours,
            ServiceStatus currentStatus,
            String contactPhone
    ) {
        this.organisation = organisation;
        this.name = name;
        this.type = type;
        this.coarseLocation = coarseLocation;
        this.operatingHours = operatingHours;
        this.currentStatus = currentStatus;
        this.contactPhone = contactPhone;
        this.lastUpdated = Instant.now();
    }

    @PrePersist
    @PreUpdate
    void touchLastUpdated() {
        this.lastUpdated = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public String getName() {
        return name;
    }

    public ServiceType getType() {
        return type;
    }

    public String getCoarseLocation() {
        return coarseLocation;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public ServiceStatus getCurrentStatus() {
        return currentStatus;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public boolean isVerified() {
        return organisation.isVerified();
    }
}
