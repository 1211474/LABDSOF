package pt.isep.supportassistant.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.dto.GroundedService;
import pt.isep.supportassistant.dto.Refusal;
import pt.isep.supportassistant.dto.RefusalCategory;
import pt.isep.supportassistant.dto.RefusalContact;
import pt.isep.supportassistant.dto.SourceContext;
import pt.isep.supportassistant.service.AiNavigatorService;
import pt.isep.supportassistant.service.AssistantUnavailableException;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiNavigatorController.class)
class AiNavigatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiNavigatorService aiNavigatorService;

    @Test
    void returnsNavigatorResponse() throws Exception {
        when(aiNavigatorService.navigate("pharmacies near Boavista"))
                .thenReturn(new AiNavigateResponse(
                        "Foram encontrados 1 serviço(s) no diretório verificado.",
                        List.of(),
                        true,
                        List.of("A disponibilidade não é garantida."),
                        new SourceContext(
                                "Diretório de serviços verificado da plataforma",
                                Instant.parse("2026-11-17T10:00:00Z"),
                                GroundedService.ALLOWED_FIELDS,
                                1
                        )
                ));

        mockMvc.perform(post("/api/v1/ai/navigate")
                        .contentType(APPLICATION_JSON)
                        .content("{\"query\":\"pharmacies near Boavista\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFallback").value(true))
                .andExpect(jsonPath("$.answer").isNotEmpty())
                .andExpect(jsonPath("$.limitations").isArray())
                .andExpect(jsonPath("$.sourceContext.source").isNotEmpty())
                .andExpect(jsonPath("$.sourceContext.recordCount").value(1))
                .andExpect(jsonPath("$.sourceContext.allowedFields").isArray());
    }

    @Test
    void returnsRefusalWithRedirectContacts() throws Exception {
        when(aiNavigatorService.navigate("emergência"))
                .thenReturn(new AiNavigateResponse(
                        "Não posso gerir uma emergência. Ligue 112. Fonte: Orientação de segurança da plataforma.",
                        List.of(),
                        false,
                        List.of("O assistente não substitui serviços de emergência."),
                        new SourceContext(
                                "Orientação de segurança da plataforma",
                                Instant.parse("2026-11-25T10:00:00Z"),
                                List.of(),
                                0
                        ),
                        new Refusal(
                                RefusalCategory.EMERGENCY,
                                "Não posso gerir uma emergência.",
                                List.of(new RefusalContact("Emergência", "112", "tel:112"))
                        )
                ));

        mockMvc.perform(post("/api/v1/ai/navigate")
                        .contentType(APPLICATION_JSON)
                        .content("{\"query\":\"emergência\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refusal.category").value("EMERGENCY"))
                .andExpect(jsonPath("$.refusal.reason").isNotEmpty())
                .andExpect(jsonPath("$.refusal.contacts[0].uri").value("tel:112"))
                .andExpect(jsonPath("$.services").isEmpty())
                .andExpect(jsonPath("$.sourceContext.source").isNotEmpty())
                .andExpect(jsonPath("$.limitations").isArray());
    }

    @Test
    void rejectsBlankQuery() throws Exception {
        mockMvc.perform(post("/api/v1/ai/navigate")
                        .contentType(APPLICATION_JSON)
                        .content("{\"query\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns503WithDirectoryFallback() throws Exception {
        when(aiNavigatorService.navigate(anyString()))
                .thenThrow(new AssistantUnavailableException("down", new RuntimeException("timeout")));

        mockMvc.perform(post("/api/v1/ai/navigate")
                        .contentType(APPLICATION_JSON)
                        .content("{\"query\":\"pharmacies near Boavista\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.isFallback").value(true))
                .andExpect(jsonPath("$.fallbackUrl").value("/api/v1/services"));
    }
}
