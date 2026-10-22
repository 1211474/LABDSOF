package pt.isep.supportassistant.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.supportassistant.dto.AiNavigateRequest;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.service.AiNavigatorService;

@RestController
@RequestMapping("/api/v1/ai")
public class AiNavigatorController {

    private final AiNavigatorService aiNavigatorService;

    public AiNavigatorController(AiNavigatorService aiNavigatorService) {
        this.aiNavigatorService = aiNavigatorService;
    }

    @PostMapping("/navigate")
    public AiNavigateResponse navigate(@Valid @RequestBody AiNavigateRequest request) {
        return aiNavigatorService.navigate(request.query());
    }
}
