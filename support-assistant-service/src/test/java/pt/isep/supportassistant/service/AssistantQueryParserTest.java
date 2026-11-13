package pt.isep.supportassistant.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AssistantQueryParserTest {

    private final AssistantQueryParser parser = new AssistantQueryParser();

    @Test
    void understandsPortuguesePlainLanguageQuestion() {
        AssistantQuery query = parser.parse("farmácias abertas perto da Boavista");

        assertThat(query.type()).isEqualTo("PHARMACY");
        assertThat(query.location()).isEqualTo("Boavista");
        assertThat(query.status()).isEqualTo(AssistantQuery.StatusPreference.OPEN_ONLY);
    }

    @Test
    void understandsEnglishPlainLanguageQuestion() {
        AssistantQuery query = parser.parse("pharmacies open near Boavista");

        assertThat(query.type()).isEqualTo("PHARMACY");
        assertThat(query.location()).isEqualTo("Boavista");
        assertThat(query.status()).isEqualTo(AssistantQuery.StatusPreference.OPEN_ONLY);
    }

    @ParameterizedTest
    @CsvSource({
            "farmácias, PHARMACY",
            "medicamentos, PHARMACY",
            "pharmacies, PHARMACY",
            "centro de dia, DAY_CENTRE",
            "centros de dia, DAY_CENTRE",
            "day care, DAY_CENTRE",
            "centro de saúde, HEALTH_CENTRE",
            "usf, HEALTH_CENTRE",
            "transporte adaptado, ADAPTED_TRANSPORT",
            "transportes, ADAPTED_TRANSPORT",
            "apoio ao domicílio, HOME_SUPPORT",
            "home support, HOME_SUPPORT"
    })
    void recognisesServiceTypes(String question, String expectedType) {
        assertThat(parser.parse(question).type()).isEqualTo(expectedType);
    }

    @ParameterizedTest
    @CsvSource({
            "farmácias abertas, OPEN_ONLY",
            "pharmacies open, OPEN_ONLY",
            "serviços disponíveis agora, OPEN_ONLY",
            "farmácias fechadas, CLOSED_ONLY",
            "farmácias encerradas, CLOSED_ONLY",
            "pharmacies closed, CLOSED_ONLY",
            "farmácias, ANY",
            "preciso de ajuda, ANY"
    })
    void recognisesStatusIntent(String question, AssistantQuery.StatusPreference expectedStatus) {
        assertThat(parser.parse(question).status()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "farmácias em Boavista, Boavista",
            "farmácias na Boa Vista, Boavista",
            "centros de dia em campanha, Campanhã",
            "apoio perto do Lordelo, Lordelo do Ouro",
            "transporte na Foz do Douro, Foz do Douro",
            "serviços em Vila Nova de Gaia, Vila Nova de Gaia",
            "serviços em Matosinhos, Matosinhos"
    })
    void recognisesAccentInsensitiveLocations(String question, String expectedLocation) {
        assertThat(parser.parse(question).location()).isEqualTo(expectedLocation);
    }

    @Test
    void extractsLocationFromProximityPhraseWhenZoneIsNotKnown() {
        AssistantQuery query = parser.parse("pharmacies near Fânzeres open now");

        assertThat(query.location()).isEqualTo("Fanzeres");
        assertThat(query.status()).isEqualTo(AssistantQuery.StatusPreference.OPEN_ONLY);
    }

    @Test
    void ignoresLeadingArticlesInProximityPhrase() {
        AssistantQuery query = parser.parse("pharmacies near the Fanzeres");

        assertThat(query.location()).isEqualTo("Fanzeres");
    }

    @Test
    void doesNotTreatGenericHomeReferencesAsLocation() {
        AssistantQuery query = parser.parse("farmácias perto de casa");

        assertThat(query.location()).isNull();
    }

    @Test
    void doesNotTreatServiceWordsAsLocation() {
        AssistantQuery query = parser.parse("ajuda perto de farmácias");

        assertThat(query.location()).isNull();
    }

    @Test
    void treatsPortoAsCityWideInsteadOfAZone() {
        AssistantQuery query = parser.parse("farmácias perto do Porto");

        assertThat(query.location()).isNull();
    }

    @Test
    void handlesCasePunctuationAndExtraWhitespace() {
        AssistantQuery query = parser.parse("   FARMÁCIAS, ABERTAS... perto da BOAVISTA?   ");

        assertThat(query.type()).isEqualTo("PHARMACY");
        assertThat(query.location()).isEqualTo("Boavista");
        assertThat(query.status()).isEqualTo(AssistantQuery.StatusPreference.OPEN_ONLY);
    }

    @Test
    void returnsNoCriteriaForBlankQuestion() {
        AssistantQuery query = parser.parse("   ");

        assertThat(query.type()).isNull();
        assertThat(query.location()).isNull();
        assertThat(query.status()).isEqualTo(AssistantQuery.StatusPreference.ANY);
    }
}
