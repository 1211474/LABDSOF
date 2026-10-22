package pt.isep.supportassistant.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.service.AiNavigatorService;
import pt.isep.supportassistant.service.AssistantUnavailableException;

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
                        List.of("A disponibilidade não é garantida.")
                ));

        mockMvc.perform(post("/api/v1/ai/navigate")
                        .contentType(APPLICATION_JSON)
                        .content("{\"query\":\"pharmacies near Boavista\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFallback").value(true))
                .andExpect(jsonPath("$.answer").isNotEmpty())
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
