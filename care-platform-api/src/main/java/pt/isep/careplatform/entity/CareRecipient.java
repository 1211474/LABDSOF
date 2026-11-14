package pt.isep.careplatform.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "care_recipients")
public class CareRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private Caregiver caregiver;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @ElementCollection
    @CollectionTable(name = "care_recipient_accessibility_tags", joinColumns = @JoinColumn(name = "recipient_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false, length = 50)
    private Set<AccessibilityTag> coarseAccessibilityTags = new HashSet<>();

    protected CareRecipient() {
    }

    public CareRecipient(Caregiver caregiver, String displayName, Set<AccessibilityTag> coarseAccessibilityTags) {
        this.caregiver = caregiver;
        this.displayName = displayName;
        this.coarseAccessibilityTags = new HashSet<>(coarseAccessibilityTags);
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

    public void update(String displayName, Set<AccessibilityTag> tags) {
        this.displayName = displayName;
        this.coarseAccessibilityTags = new HashSet<>(tags);
    }

    public Set<AccessibilityTag> getCoarseAccessibilityTags() {
        return Set.copyOf(coarseAccessibilityTags);
    }
}
