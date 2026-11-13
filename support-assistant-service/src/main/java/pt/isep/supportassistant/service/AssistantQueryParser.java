package pt.isep.supportassistant.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AssistantQueryParser {

    private static final int MAX_LOCATION_TOKENS = 4;

    private static final List<TypeRule> TYPE_RULES = List.of(
            new TypeRule("DAY_CENTRE", List.of(
                    "centro de dia", "centros de dia", "centro diurno", "lar de dia",
                    "day care", "day centre", "day center"
            )),
            new TypeRule("HEALTH_CENTRE", List.of(
                    "centro de saude", "centros de saude", "posto de saude", "unidade de saude",
                    "health centre", "health center", "usf"
            )),
            new TypeRule("HOME_SUPPORT", List.of(
                    "domicil", "home support", "apoio em casa", "cuidados em casa",
                    "ajuda em casa", "apoio domiciliar"
            )),
            new TypeRule("ADAPTED_TRANSPORT", List.of(
                    "transport", "transporte", "mobilidade"
            )),
            new TypeRule("PHARMACY", List.of(
                    "farmac", "pharmac", "medicament"
            ))
    );

    private static final List<LocationRule> LOCATION_RULES = List.of(
            new LocationRule("Lordelo do Ouro", List.of("lordelo do ouro", "lordelo")),
            new LocationRule("Foz do Douro", List.of("foz do douro", "foz")),
            new LocationRule("Vila Nova de Gaia", List.of("vila nova de gaia", "gaia")),
            new LocationRule("Cidade Universitária", List.of("cidade universitaria")),
            new LocationRule("Santo Ildefonso", List.of("santo ildefonso")),
            new LocationRule("São Nicolau", List.of("sao nicolau")),
            new LocationRule("Boavista", List.of("boavista", "boa vista")),
            new LocationRule("Campanhã", List.of("campanha")),
            new LocationRule("Cedofeita", List.of("cedofeita")),
            new LocationRule("Bonfim", List.of("bonfim")),
            new LocationRule("Paranhos", List.of("paranhos")),
            new LocationRule("Massarelos", List.of("massarelos")),
            new LocationRule("Aldoar", List.of("aldoar")),
            new LocationRule("Nevogilde", List.of("nevogilde")),
            new LocationRule("Ramalde", List.of("ramalde")),
            new LocationRule("Miragaia", List.of("miragaia")),
            new LocationRule("Vitória", List.of("vitoria")),
            new LocationRule("Aliados", List.of("aliados")),
            new LocationRule("Bolhão", List.of("bolhao")),
            new LocationRule("Ribeira", List.of("ribeira")),
            new LocationRule("Trindade", List.of("trindade")),
            new LocationRule("Asprela", List.of("asprela")),
            new LocationRule("Prelada", List.of("prelada")),
            new LocationRule("Amial", List.of("amial")),
            new LocationRule("Covelo", List.of("covelo")),
            new LocationRule("Contumil", List.of("contumil")),
            new LocationRule("Pasteleira", List.of("pasteleira")),
            new LocationRule("Matosinhos", List.of("matosinhos")),
            new LocationRule("Gondomar", List.of("gondomar")),
            new LocationRule("Valongo", List.of("valongo"))
    );

    private static final Pattern PROXIMITY_PATTERN = Pattern.compile(
            "(?:perto de|perto da|perto do|junto a|junto da|junto do|proximo de|proxima de"
                    + "|nas proximidades de|na zona de|na zona da|na zona do|near)\\s+(.+)"
    );

    private static final Set<String> LEADING_NOISE = Set.of(
            "the", "a", "o", "os", "as", "um", "uma"
    );

    private static final Set<String> TRAILING_NOISE = Set.of(
            "aberto", "aberta", "abertos", "abertas", "open", "closed",
            "fechado", "fechada", "fechados", "fechadas", "agora", "now", "hoje", "today",
            "por", "favor", "please", "disponivel", "disponiveis", "funcionamento",
            "area", "zona", "regiao", "local", "the"
    );

    private static final Set<String> IGNORED_LOCATION_TOKENS = Set.of(
            "casa", "mim", "minha", "meu", "minhas", "meus", "zona", "regiao", "local",
            "aqui", "ali", "todo", "toda", "todos", "todas", "perto", "junto",
            "proximo", "proxima", "hoje", "agora", "house", "home"
    );

    private static final Set<String> CITY_WIDE_LOCATIONS = Set.of("porto");

    private static final Set<String> CONNECTOR_TOKENS = Set.of(
            "de", "do", "da", "dos", "das", "e"
    );

    public AssistantQuery parse(String rawQuery) {
        String normalizedQuery = QueryText.normalize(rawQuery);

        return new AssistantQuery(
                detectType(normalizedQuery),
                detectLocation(normalizedQuery),
                detectStatus(normalizedQuery)
        );
    }

    private String detectType(String normalizedQuery) {
        for (TypeRule rule : TYPE_RULES) {
            for (String keyword : rule.keywords()) {
                if (QueryText.containsKeyword(normalizedQuery, keyword)) {
                    return rule.type();
                }
            }
        }

        return null;
    }

    private AssistantQuery.StatusPreference detectStatus(String normalizedQuery) {
        boolean mentionsNow = QueryText.containsKeyword(normalizedQuery, "agora")
                || QueryText.containsKeyword(normalizedQuery, "now");

        if (mentionsNow && QueryText.containsKeyword(normalizedQuery, "dispon")) {
            return AssistantQuery.StatusPreference.OPEN_ONLY;
        }
        if (QueryText.containsKeyword(normalizedQuery, "abert")
                || QueryText.containsKeyword(normalizedQuery, "open")) {
            return AssistantQuery.StatusPreference.OPEN_ONLY;
        }
        if (QueryText.containsKeyword(normalizedQuery, "fechad")
                || QueryText.containsKeyword(normalizedQuery, "encerrad")
                || QueryText.containsKeyword(normalizedQuery, "closed")) {
            return AssistantQuery.StatusPreference.CLOSED_ONLY;
        }

        return AssistantQuery.StatusPreference.ANY;
    }

    private String detectLocation(String normalizedQuery) {
        for (LocationRule rule : LOCATION_RULES) {
            for (String alias : rule.aliases()) {
                if (QueryText.containsPhrase(normalizedQuery, alias)) {
                    return rule.displayName();
                }
            }
        }

        return detectProximityLocation(normalizedQuery);
    }

    private String detectProximityLocation(String normalizedQuery) {
        Matcher matcher = PROXIMITY_PATTERN.matcher(normalizedQuery);
        if (!matcher.find()) {
            return null;
        }

        return toLocationName(matcher.group(1));
    }

    private String toLocationName(String rawCapture) {
        List<String> tokens = new ArrayList<>(List.of(rawCapture.trim().split("\\s+")));
        tokens.removeIf(String::isBlank);

        while (!tokens.isEmpty() && TRAILING_NOISE.contains(tokens.get(tokens.size() - 1))) {
            tokens.remove(tokens.size() - 1);
        }
        while (!tokens.isEmpty() && LEADING_NOISE.contains(tokens.get(0))) {
            tokens.remove(0);
        }

        if (tokens.isEmpty() || IGNORED_LOCATION_TOKENS.contains(tokens.get(0))) {
            return null;
        }

        List<String> limitedTokens = tokens.size() > MAX_LOCATION_TOKENS
                ? tokens.subList(0, MAX_LOCATION_TOKENS)
                : tokens;

        String joinedTokens = String.join(" ", limitedTokens);
        if (CITY_WIDE_LOCATIONS.contains(joinedTokens) || detectType(joinedTokens) != null) {
            return null;
        }

        return toDisplayName(limitedTokens);
    }

    private String toDisplayName(List<String> tokens) {
        StringBuilder displayName = new StringBuilder();

        for (int index = 0; index < tokens.size(); index++) {
            if (index > 0) {
                displayName.append(' ');
            }

            String token = tokens.get(index);
            if (index > 0 && CONNECTOR_TOKENS.contains(token)) {
                displayName.append(token);
            } else {
                displayName.append(Character.toUpperCase(token.charAt(0)))
                        .append(token.substring(1));
            }
        }

        return displayName.toString();
    }

    private record TypeRule(String type, List<String> keywords) {
    }

    private record LocationRule(String displayName, List<String> aliases) {
    }
}
