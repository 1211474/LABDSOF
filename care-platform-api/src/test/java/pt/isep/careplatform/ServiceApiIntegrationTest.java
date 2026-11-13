package pt.isep.careplatform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.entity.Organisation;
import pt.isep.careplatform.entity.ServiceStatus;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.entity.SupportService;
import pt.isep.careplatform.repository.OrganisationRepository;
import pt.isep.careplatform.repository.SupportServiceRepository;
import pt.isep.careplatform.repository.UserRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ServiceApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SupportServiceRepository supportServiceRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        supportServiceRepository.deleteAll();
        organisationRepository.deleteAll();

        userRepository.save(new User("ana@example.com", passwordEncoder.encode("password"), UserRole.CAREGIVER));
        Organisation pharmacy = organisationRepository.save(new Organisation("Test Pharmacy", true));
        Organisation dayCentre = organisationRepository.save(new Organisation("Test Day Centre", true));

        supportServiceRepository.save(new SupportService(
                pharmacy,
                "Test Pharmacy",
                ServiceType.PHARMACY,
                "Boavista",
                "09:00 - 18:00",
                ServiceStatus.OPEN,
                "+351220000000"
        ));
        supportServiceRepository.save(new SupportService(
                dayCentre,
                "Test Day Centre",
                ServiceType.DAY_CENTRE,
                "Campanhã",
                "09:00 - 17:00",
                ServiceStatus.TEMPORARILY_CLOSED,
                "+351220000001"
        ));
    }

    @Test
    void returnsAllServicesWithSourceAndFreshness() throws Exception {
        mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].verified").value(true))
                .andExpect(jsonPath("$[0].lastUpdated").isNotEmpty())
                .andExpect(jsonPath("$[0].contactPhone").isNotEmpty());
    }

    @Test
    void filtersServicesByTypeAndLocation() throws Exception {
        mockMvc.perform(get("/api/v1/services")
                        .param("type", "PHARMACY")
                        .param("coarseLocation", "Boavista"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Pharmacy"))
                .andExpect(jsonPath("$[0].currentStatus").value("OPEN"));
    }

    @Test
    void logsInAndReturnsTheUserRoleAndJwt() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("CAREGIVER"))
                .andExpect(jsonPath("$.permissions").isArray())
                .andExpect(jsonPath("$.permissions").value(org.hamcrest.Matchers.hasItems(
                        "SEARCH_SERVICES", "ASK_NAVIGATOR", "MANAGE_SUPPORT_REQUESTS"
                )));
    }

    @Test
    void rejectsInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsUnauthenticatedCurrentUserRequests() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
