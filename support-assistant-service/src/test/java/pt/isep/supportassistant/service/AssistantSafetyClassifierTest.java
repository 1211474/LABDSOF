package pt.isep.supportassistant.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pt.isep.supportassistant.dto.RefusalCategory;

import static org.assertj.core.api.Assertions.assertThat;

class AssistantSafetyClassifierTest {

    private final AssistantSafetyClassifier classifier = new AssistantSafetyClassifier();

    @ParameterizedTest
    @ValueSource(strings = {
            "Tenho dor no peito, chamem uma ambulância!",
            "É uma emergência, liguem para o 112",
            "Preciso de ajuda urgente",
            "SNS 24 pode ajudar?",
            "I need emergency help"
    })
    void refusesEmergencyRequests(String query) {
        assertThat(classifier.assess(query).category()).isEqualTo(RefusalCategory.EMERGENCY);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Preciso de uma receita para antibiótico",
            "Que medicamento devo tomar?",
            "Qual é a dosagem recomendada?",
            "Can you prescribe something for this?"
    })
    void refusesPrescriptionRequests(String query) {
        assertThat(classifier.assess(query).category()).isEqualTo(RefusalCategory.PRESCRIPTION);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "O que significa este sintoma?",
            "Tem um diagnóstico para mim?",
            "Como tratar esta doença?",
            "I need medical advice"
    })
    void refusesMedicalRequests(String query) {
        assertThat(classifier.assess(query).category()).isEqualTo(RefusalCategory.MEDICAL_ADVICE);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "farmácias abertas perto da Boavista",
            "pharmacies open near Boavista",
            "preciso de medicamentos na Boavista",
            "centro de saúde em Campanhã",
            "transporte adaptado perto do Porto",
            "preciso de ajuda"
    })
    void allowsServiceDiscoveryRequests(String query) {
        assertThat(classifier.assess(query).isUnsafe()).isFalse();
    }

    @Test
    void treatsBlankQueryAsSafe() {
        assertThat(classifier.assess("  ").isUnsafe()).isFalse();
    }
}
