package pt.isep.supportassistant.service;

import org.junit.jupiter.api.Test;
import pt.isep.supportassistant.dto.Refusal;
import pt.isep.supportassistant.dto.RefusalCategory;

import static org.assertj.core.api.Assertions.assertThat;

class RefusalFactoryTest {

    private final RefusalFactory factory = new RefusalFactory();

    @Test
    void emergencyRefusalPointsTo112AndSns24() {
        Refusal refusal = factory.create(RefusalCategory.EMERGENCY);

        assertThat(refusal.category()).isEqualTo(RefusalCategory.EMERGENCY);
        assertThat(refusal.reason()).contains("112", "SNS 24");
        assertThat(refusal.contacts())
                .extracting("phoneNumber")
                .containsExactly("112", "808242424");
        assertThat(refusal.contacts())
                .extracting("uri")
                .containsExactly("tel:112", "tel:808242424");
    }

    @Test
    void prescriptionRefusalRedirectsToHealthProfessionals() {
        Refusal refusal = factory.create(RefusalCategory.PRESCRIPTION);

        assertThat(refusal.reason()).contains("médico", "farmacêutico", "112");
        assertThat(refusal.contacts()).hasSize(2);
    }

    @Test
    void medicalAdviceRefusalRedirectsToHealthProfessionals() {
        Refusal refusal = factory.create(RefusalCategory.MEDICAL_ADVICE);

        assertThat(refusal.reason()).contains("diagnósticos", "profissional de saúde");
        assertThat(refusal.contacts()).hasSize(2);
    }
}
