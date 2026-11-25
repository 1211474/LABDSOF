package pt.isep.supportassistant.dto;

import java.util.List;

/**
 * A safety refusal returned instead of an answer for medical, prescription or emergency requests.
 */
public record Refusal(
        RefusalCategory category,
        String reason,
        List<RefusalContact> contacts
) {
}
