package pt.isep.supportassistant.service;

/**
 * The two parts of a grounded Gemini request. Built only from allowlisted service facts.
 */
public record LlmPrompt(String systemPrompt, String userPrompt) {
}
