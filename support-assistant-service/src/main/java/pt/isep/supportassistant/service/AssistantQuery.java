package pt.isep.supportassistant.service;

public record AssistantQuery(
        String type,
        String location,
        StatusPreference status
) {

    public enum StatusPreference {
        ANY,
        OPEN_ONLY,
        CLOSED_ONLY
    }
}
