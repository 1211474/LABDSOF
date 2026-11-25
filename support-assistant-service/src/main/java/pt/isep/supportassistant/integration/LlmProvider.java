package pt.isep.supportassistant.integration;

/**
 * Boundary for the managed LLM provider. The service layer decides when it is safe to call it.
 */
public interface LlmProvider {

    boolean isEnabled();

    String complete(String systemPrompt, String userPrompt);
}
