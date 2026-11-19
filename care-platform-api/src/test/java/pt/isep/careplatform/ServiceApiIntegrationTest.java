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
import pt.isep.careplatform.repository.CareRecipientRepository;
import pt.isep.careplatform.repository.CaregiverRepository;
import pt.isep.careplatform.repository.EmergencyContactRepository;
import pt.isep.careplatform.repository.UserRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private CaregiverRepository caregiverRepository;

    @Autowired
    private CareRecipientRepository careRecipientRepository;

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        emergencyContactRepository.deleteAll();
        careRecipientRepository.deleteAll();
        caregiverRepository.deleteAll();
        userRepository.deleteAll();
        supportServiceRepository.deleteAll();
        organisationRepository.deleteAll();

        userRepository.save(new User("ana@example.com", passwordEncoder.encode("password"), UserRole.CAREGIVER));
        userRepository.save(new User("bruno@example.com", passwordEncoder.encode("password"), UserRole.CAREGIVER));
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

    @Test
    void rejectsUnsupportedUnauthenticatedServiceMutations() throws Exception {
        mockMvc.perform(post("/api/v1/services"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createsReadsUpdatesAndRemovesCareProfileAndRecipient() throws Exception {
        String token = caregiverToken();
        String caregiverId = mockMvc.perform(post("/api/v1/caregivers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ana Lopes",
                                  "phoneNumber": "+351 912 345 678",
                                  "coarseLocation": "Boavista"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value("Ana Lopes"))
                .andExpect(jsonPath("$.coarseLocation").value("Boavista"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        String recipientId = mockMvc.perform(post("/api/v1/caregivers/" + caregiverId + "/recipients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Rita",
                                  "coarseAccessibilityTags": ["wheelchair_accessible"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value("Rita"))
                .andExpect(jsonPath("$.coarseAccessibilityTags[0]").value("wheelchair_accessible"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(patch("/api/v1/caregivers/" + caregiverId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ana Silva",
                                  "phoneNumber": "+351 912 000 111",
                                  "coarseLocation": "Campanhã"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Ana Silva"));

        mockMvc.perform(patch("/api/v1/caregivers/" + caregiverId + "/recipients/" + recipientId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Rita Silva",
                                  "coarseAccessibilityTags": ["visual_assistance"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Rita Silva"));

        mockMvc.perform(get("/api/v1/caregivers/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipients[0].displayName").value("Rita Silva"));

        mockMvc.perform(delete("/api/v1/caregivers/" + caregiverId + "/recipients/" + recipientId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectsUnsupportedRecipientTags() throws Exception {
        String token = caregiverToken();
        String caregiverId = mockMvc.perform(post("/api/v1/caregivers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ana Lopes",
                                  "phoneNumber": "+351 912 345 678",
                                  "coarseLocation": "Boavista"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/v1/caregivers/" + caregiverId + "/recipients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Rita",
                                  "coarseAccessibilityTags": ["diagnosis"]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createsReadsUpdatesAndRemovesEmergencyContact() throws Exception {
        String token = caregiverToken();
        String caregiverId = mockMvc.perform(post("/api/v1/caregivers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ana Lopes",
                                  "phoneNumber": "+351 912 345 678",
                                  "coarseLocation": "Boavista"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        String contactId = mockMvc.perform(post("/api/v1/caregivers/" + caregiverId + "/emergency-contacts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Rita Lopes",
                                  "phoneNumber": "+351 913 000 111",
                                  "relationship": "Filha"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value("Rita Lopes"))
                .andExpect(jsonPath("$.phoneNumber").value("+351 913 000 111"))
                .andExpect(jsonPath("$.relationship").value("Filha"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(patch("/api/v1/caregivers/" + caregiverId + "/emergency-contacts/" + contactId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Rita Silva",
                                  "phoneNumber": "+351 913 000 222",
                                  "relationship": "Vizinha"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Rita Silva"))
                .andExpect(jsonPath("$.relationship").value("Vizinha"));

        mockMvc.perform(get("/api/v1/caregivers/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergencyContacts[0].displayName").value("Rita Silva"));

        mockMvc.perform(delete("/api/v1/caregivers/" + caregiverId + "/emergency-contacts/" + contactId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/caregivers/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergencyContacts.length()").value(0));
    }

    @Test
    void rejectsUnauthenticatedEmergencyContactMutations() throws Exception {
        String token = caregiverToken();
        String caregiverId = mockMvc.perform(post("/api/v1/caregivers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ana Lopes",
                                  "phoneNumber": "+351 912 345 678",
                                  "coarseLocation": "Boavista"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/v1/caregivers/" + caregiverId + "/emergency-contacts")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Rita Lopes",
                                  "phoneNumber": "+351 913 000 111"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void preventsOtherCaregiversFromAccessingOrMutatingEmergencyContacts() throws Exception {
        String anaToken = caregiverToken();
        String anaCaregiverId = mockMvc.perform(post("/api/v1/caregivers")
                        .header("Authorization", "Bearer " + anaToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ana Lopes",
                                  "phoneNumber": "+351 912 345 678",
                                  "coarseLocation": "Boavista"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        String contactId = mockMvc.perform(post("/api/v1/caregivers/" + anaCaregiverId + "/emergency-contacts")
                        .header("Authorization", "Bearer " + anaToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Rita Lopes",
                                  "phoneNumber": "+351 913 000 111"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        String brunoToken = tokenFor("bruno@example.com");
        mockMvc.perform(post("/api/v1/caregivers/" + anaCaregiverId + "/emergency-contacts")
                        .header("Authorization", "Bearer " + brunoToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Carlos",
                                  "phoneNumber": "+351 913 000 999"
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/v1/caregivers/" + anaCaregiverId + "/emergency-contacts/" + contactId)
                        .header("Authorization", "Bearer " + brunoToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Carlos",
                                  "phoneNumber": "+351 913 000 999"
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/caregivers/" + anaCaregiverId + "/emergency-contacts/" + contactId)
                        .header("Authorization", "Bearer " + brunoToken))
                .andExpect(status().isForbidden());
    }

    private String caregiverToken() throws Exception {
        return tokenFor("ana@example.com");
    }

    private String tokenFor(String email) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }
}
