package pt.isep.careplatform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.entity.User;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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

    private UUID unverifiedOrganisationId;

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
        userRepository.save(new User("provider@example.com", passwordEncoder.encode("password"), UserRole.PROVIDER));
        userRepository.save(new User("admin@example.com", passwordEncoder.encode("password"), UserRole.PLATFORM_ADMIN));
        Organisation pharmacy = organisationRepository.save(new Organisation("Test Pharmacy", true));
        Organisation dayCentre = organisationRepository.save(new Organisation("Test Day Centre", true));
        Organisation pendingOrganisation = organisationRepository.save(new Organisation("Pending Organisation", false));
        unverifiedOrganisationId = pendingOrganisation.getId();

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
                .andExpect(jsonPath("$[0].source").value("Test Day Centre"))
                .andExpect(jsonPath("$[0].lastUpdated").isNotEmpty())
                .andExpect(jsonPath("$[0].contactPhone").isNotEmpty());
    }

    @Test
    void returnsServiceDetailsWithSourceAndLastUpdate() throws Exception {
        String serviceId = supportServiceRepository.findAll().stream()
                .filter(service -> service.getName().equals("Test Pharmacy"))
                .findFirst()
                .orElseThrow()
                .getId()
                .toString();

        mockMvc.perform(get("/api/v1/services/" + serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(serviceId))
                .andExpect(jsonPath("$.name").value("Test Pharmacy"))
                .andExpect(jsonPath("$.source").value("Test Pharmacy"))
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.contactPhone").value("+351220000000"))
                .andExpect(jsonPath("$.lastUpdated").isNotEmpty());
    }

    @Test
    void returnsNotFoundForUnknownServiceId() throws Exception {
        mockMvc.perform(get("/api/v1/services/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
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
    void platformAdminCanVerifyAndRevokeAnOrganisationIdempotently() throws Exception {
        String token = tokenFor("admin@example.com");
        String endpoint = "/api/v1/admin/organisations/" + unverifiedOrganisationId + "/verification";

        String firstVerification = mockMvc.perform(put(endpoint)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"verified":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(unverifiedOrganisationId.toString()))
                .andExpect(jsonPath("$.name").value("Pending Organisation"))
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.verifiedAt").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"verifiedAt\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(put(endpoint)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"verified":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verifiedAt").value(firstVerification));

        mockMvc.perform(put(endpoint)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"verified":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.verifiedAt").doesNotExist());
    }

    @Test
    void onlyPlatformAdminsCanChangeOrganisationVerification() throws Exception {
        String endpoint = "/api/v1/admin/organisations/" + unverifiedOrganisationId + "/verification";
        String request = """
                {"verified":true}
                """;

        mockMvc.perform(get("/api/v1/admin/organisations"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/admin/organisations")
                        .header("Authorization", "Bearer " + tokenFor("provider@example.com")))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(endpoint)
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put(endpoint)
                        .header("Authorization", "Bearer " + caregiverToken())
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(endpoint)
                        .header("Authorization", "Bearer " + tokenFor("provider@example.com"))
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/admin/organisations/00000000-0000-0000-0000-000000000000/verification")
                        .header("Authorization", "Bearer " + tokenFor("admin@example.com"))
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    void organisationDetailsArePublicOnlyAfterPlatformAdminVerification() throws Exception {
        Organisation organisation = organisationRepository.findById(unverifiedOrganisationId).orElseThrow();
        organisation.updatePublicDetails(
                "contact@pending.example",
                "+351 220 000 002",
                "https://pending.example",
                "Boavista"
        );
        organisationRepository.save(organisation);
        SupportService service = supportServiceRepository.save(new SupportService(
                organisation,
                "Pending Service",
                ServiceType.PHARMACY,
                "Boavista",
                "09:00 - 17:00",
                ServiceStatus.OPEN,
                "+351220000002"
        ));
        String serviceId = service.getId().toString();
        String adminToken = tokenFor("admin@example.com");

        mockMvc.perform(get("/api/v1/services/" + serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.organisationEmail").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.organisationPhone").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.organisationWebsite").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.organisationCoarseLocation").value(org.hamcrest.Matchers.nullValue()));

        String adminOrganisations = mockMvc.perform(get("/api/v1/admin/organisations")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(adminOrganisations.contains("contact@pending.example"));

        mockMvc.perform(put("/api/v1/admin/organisations/" + unverifiedOrganisationId + "/verification")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"verified":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));

        mockMvc.perform(get("/api/v1/services/" + serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.organisationEmail").value("contact@pending.example"))
                .andExpect(jsonPath("$.organisationPhone").value("+351 220 000 002"))
                .andExpect(jsonPath("$.organisationWebsite").value("https://pending.example"))
                .andExpect(jsonPath("$.organisationCoarseLocation").value("Boavista"));

        mockMvc.perform(put("/api/v1/admin/organisations/" + unverifiedOrganisationId + "/verification")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"verified":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false));

        mockMvc.perform(get("/api/v1/services/" + serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.organisationEmail").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.organisationPhone").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.organisationWebsite").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.organisationCoarseLocation").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void platformAdminCanUpdatePublicOrganisationDetails() throws Exception {
        String serviceId = supportServiceRepository.findAll().stream()
                .filter(service -> service.getName().equals("Test Pharmacy"))
                .findFirst()
                .orElseThrow()
                .getId()
                .toString();
        String organisationId = supportServiceRepository.findAll().stream()
                .filter(service -> service.getName().equals("Test Pharmacy"))
                .findFirst()
                .orElseThrow()
                .getOrganisation()
                .getId()
                .toString();
        String adminToken = tokenFor("admin@example.com");

        mockMvc.perform(patch("/api/v1/admin/organisations/" + organisationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "officialEmail": "contact@test-pharmacy.example",
                                  "officialPhone": "+351 220 000 000",
                                  "website": "https://test-pharmacy.example",
                                  "coarseLocation": "Boavista"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officialEmail").value("contact@test-pharmacy.example"))
                .andExpect(jsonPath("$.officialPhone").value("+351 220 000 000"))
                .andExpect(jsonPath("$.website").value("https://test-pharmacy.example"))
                .andExpect(jsonPath("$.coarseLocation").value("Boavista"));

        mockMvc.perform(get("/api/v1/services/" + serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organisationEmail").value("contact@test-pharmacy.example"))
                .andExpect(jsonPath("$.organisationPhone").value("+351 220 000 000"))
                .andExpect(jsonPath("$.organisationWebsite").value("https://test-pharmacy.example"))
                .andExpect(jsonPath("$.organisationCoarseLocation").value("Boavista"));

        mockMvc.perform(patch("/api/v1/admin/organisations/" + organisationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"officialEmail":""}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officialEmail").doesNotExist())
                .andExpect(jsonPath("$.website").value("https://test-pharmacy.example"));

        mockMvc.perform(get("/api/v1/services/" + serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organisationPhone").value("+351 220 000 000"))
                .andExpect(jsonPath("$.organisationWebsite").value("https://test-pharmacy.example"))
                .andExpect(jsonPath("$.organisationCoarseLocation").value("Boavista"));
    }

    @Test
    void rejectsInvalidOrganisationContactDetails() throws Exception {
        String organisationId = supportServiceRepository.findAll().stream()
                .filter(service -> service.getName().equals("Test Pharmacy"))
                .findFirst()
                .orElseThrow()
                .getOrganisation()
                .getId()
                .toString();
        String adminToken = tokenFor("admin@example.com");

        mockMvc.perform(patch("/api/v1/admin/organisations/" + organisationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"officialEmail":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/v1/admin/organisations/" + organisationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"website":"javascript:alert(1)"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/v1/admin/organisations/" + organisationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"officialPhone":"javascript:alert(1)"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/v1/admin/organisations/" + organisationId)
                        .header("Authorization", "Bearer " + tokenFor("provider@example.com"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"officialEmail":"contact@test-pharmacy.example"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void requiresAnExplicitVerificationValue() throws Exception {
        mockMvc.perform(put("/api/v1/admin/organisations/" + unverifiedOrganisationId + "/verification")
                        .header("Authorization", "Bearer " + tokenFor("admin@example.com"))
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
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
