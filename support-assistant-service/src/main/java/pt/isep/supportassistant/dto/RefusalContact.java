package pt.isep.supportassistant.dto;

/**
 * A human or institutional contact offered after a query is refused.
 */
public record RefusalContact(
        String label,
        String phoneNumber,
        String uri
) {
}
