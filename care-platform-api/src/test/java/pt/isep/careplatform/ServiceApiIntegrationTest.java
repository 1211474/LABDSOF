package pt.isep.careplatform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pt.isep.careplatform.entity.Organisation;
import pt.isep.careplatform.entity.ServiceStatus;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.entity.SupportService;
import pt.isep.careplatform.repository.OrganisationRepository;
import pt.isep.careplatform.repository.SupportServiceRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() {
        supportServiceRepository.deleteAll();
        organisationRepository.deleteAll();

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
}
