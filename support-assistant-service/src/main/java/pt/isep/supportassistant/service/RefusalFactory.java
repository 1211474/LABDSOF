package pt.isep.supportassistant.service;

import org.springframework.stereotype.Component;
import pt.isep.supportassistant.dto.Refusal;
import pt.isep.supportassistant.dto.RefusalCategory;
import pt.isep.supportassistant.dto.RefusalContact;

import java.util.List;

/**
 * Builds the redirection returned when a query is refused by the input guardrail.
 */
@Component
public class RefusalFactory {

    static final String REFUSAL_SOURCE = "Orientação de segurança da plataforma";

    private static final RefusalContact EMERGENCY = new RefusalContact(
            "Emergência", "112", "tel:112");
    private static final RefusalContact SNS_24 = new RefusalContact(
            "SNS 24", "808242424", "tel:808242424");
    private static final List<RefusalContact> CONTACTS = List.of(EMERGENCY, SNS_24);

    public Refusal create(RefusalCategory category) {
        return new Refusal(category, reason(category), CONTACTS);
    }

    private String reason(RefusalCategory category) {
        return switch (category) {
            case EMERGENCY -> "Não posso gerir uma emergência. Ligue imediatamente para o 112. "
                    + "Para aconselhamento de saúde, ligue para o SNS 24.";
            case PRESCRIPTION -> "Não posso prescrever medicamentos nem indicar doses. "
                    + "Fale com um médico ou farmacêutico. Em caso de emergência, ligue 112 ou SNS 24.";
            case MEDICAL_ADVICE -> "Não posso fazer diagnósticos nem dar aconselhamento médico. "
                    + "Contacte um profissional de saúde. Em caso de emergência, ligue 112 ou SNS 24.";
        };
    }
}
