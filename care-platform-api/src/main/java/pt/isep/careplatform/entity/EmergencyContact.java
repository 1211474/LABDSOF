package pt.isep.careplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private Caregiver caregiver;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "phone_number", nullable = false, length = 40)
    private String phoneNumber;

    @Column(name = "relationship", length = 60)
    private String relationship;

    protected EmergencyContact() {
    }

    public EmergencyContact(Caregiver caregiver, String displayName, String phoneNumber, String relationship) {
        this.caregiver = caregiver;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;
    }

    public UUID getId() {
        return id;
    }

    public Caregiver getCaregiver() {
        return caregiver;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void update(String displayName, String phoneNumber, String relationship) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;
    }
}
