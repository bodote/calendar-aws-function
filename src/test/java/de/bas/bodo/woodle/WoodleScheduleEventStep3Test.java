package de.bas.bodo.woodle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit5.ScenarioTest;

import de.bas.bodo.woodle.service.PollStorageService;
import gg.jte.springframework.boot.autoconfigure.JteAutoConfiguration;

/**
 * Tests for Requirement 6 – Step-3 navigation (only Acceptance Criterion 1 for
 * now).
 */
@WebMvcTest
@ImportAutoConfiguration(JteAutoConfiguration.class)
class WoodleScheduleEventStep3Test extends
        ScenarioTest<WoodleScheduleEventStep3Test.GivenStep2Page, WoodleScheduleEventStep3Test.WhenUserOnStep2Page, WoodleScheduleEventStep3Test.ThenStep3PageIsDisplayed> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PollStorageService pollStorageService;

    private static final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

    @Test
    void shouldNavigateFromStep2ToStep3() throws Exception {
        given().the_application_is_running_with_mock_mvc(mockMvc)
                .and().the_poll_storage_service_returns_existing_data(pollStorageService, TEST_UUID);

        when().the_user_presses_next_on_step2_form(TEST_UUID);

        then().the_user_is_redirected_to_step3(TEST_UUID)
                .and().the_step3_page_is_returned_successfully(TEST_UUID);
    }

    /*
     * ---------------------------------------------------------------------
     * Acceptance Criterion 2 – Step-3 form layout
     * -------------------------------------------------------------------
     */
    @Test
    void shouldDisplayAllRequiredFieldsOnStep3Form() throws Exception {
        given().the_application_is_running_with_mock_mvc(mockMvc)
                .and().the_poll_storage_service_returns_empty_data(pollStorageService, TEST_UUID);

        when().the_user_visits_step3_page(TEST_UUID);

        then().the_step3_form_with_all_required_fields_is_displayed();
    }

    /*
     * ---------------------------------------------------------------------
     * Acceptance Criterion 3 – Back navigation from Step-3
     * -------------------------------------------------------------------
     */
    @Test
    void shouldNavigateBackFromStep3AndPreserveData() throws Exception {
        given().the_application_is_running_with_mock_mvc(mockMvc)
                .and().the_poll_storage_service_returns_step2_data(pollStorageService, TEST_UUID);

        when().the_user_clicks_back_on_step3_page(TEST_UUID);

        then().the_user_is_redirected_to_step2(TEST_UUID)
                .and().the_step2_fields_are_pre_filled_with_previous_data();
    }

    /*
     * ---------------------------------------------------------------------
     * Acceptance Criterion 4 – Default expiry-date calculation
     * -------------------------------------------------------------------
     */
    @Test
    void shouldDefaultExpiryDateBasedOnStep2StartDate() throws Exception {
        String startDate = "2024-01-15";
        String expectedExpiryDate = LocalDate.parse(startDate).plusMonths(3).toString();

        given().the_application_is_running_with_mock_mvc(mockMvc)
                .and().the_poll_storage_service_returns_step2_start_date(pollStorageService, TEST_UUID, startDate);

        when().the_user_visits_step3_page(TEST_UUID);

        then().the_expiry_date_defaults_to(expectedExpiryDate);
    }

    /*
     * ---------------------------------------------------------------------
     * Acceptance Criterion 5 – Direct access with existing UUID
     * -------------------------------------------------------------------
     */
    @Test
    void shouldDirectAccessToStep3WithExistingUuidPrepopulatesData() throws Exception {
        given().the_application_is_running_with_mock_mvc(mockMvc)
                .and().the_poll_storage_service_returns_existing_step3_data(pollStorageService, TEST_UUID);

        when().the_user_visits_step3_page(TEST_UUID);

        then().the_step3_form_is_pre_populated_with_existing_data();
    }

    /*
     * ---------------------------------------------------------------------
     * Acceptance Criterion 6 – Direct access with unknown UUID
     * -------------------------------------------------------------------
     */
    @Test
    void shouldRedirectToScheduleEventWhenUuidNotFoundInStep3() throws Exception {
        final String NON_EXISTENT_UUID = "99999999-9999-9999-9999-999999999999";

        given().the_application_is_running_with_mock_mvc(mockMvc)
                .and().the_poll_storage_service_returns_null_for_non_existent_uuid(pollStorageService);

        when().the_user_visits_step3_page_with_non_existent_uuid(NON_EXISTENT_UUID);

        then().the_user_is_redirected_to_schedule_event_with_poll_not_found_message();
    }

    /*
     * ---------------------------------------------------------------------
     * Acceptance Criterion 7 – Direct access without UUID
     * -------------------------------------------------------------------
     */
    @Test
    void shouldRedirectToScheduleEventWhenNoUuidInStep3Url() throws Exception {
        given().the_application_is_running_with_mock_mvc(mockMvc);

        when().the_user_visits_step3_page_without_uuid();

        then().the_user_is_redirected_to_schedule_event_with_poll_id_missing_message();
    }

    /** GIVEN **/
    public static class GivenStep2Page extends Stage<GivenStep2Page> {
        @ProvidedScenarioState
        private MockMvc mockMvc;

        public GivenStep2Page the_application_is_running_with_mock_mvc(MockMvc mockMvc) {
            this.mockMvc = mockMvc;
            return self();
        }

        public GivenStep2Page the_poll_storage_service_returns_existing_data(PollStorageService service, String uuid) {
            // minimal stub to satisfy compilation; behaviour not yet implemented
            Mockito.when(service.retrievePollData(uuid)).thenReturn(java.util.Collections.emptyMap());
            return self();
        }

        public GivenStep2Page the_poll_storage_service_returns_empty_data(PollStorageService service, String uuid) {
            Mockito.when(service.retrievePollData(uuid)).thenReturn(new HashMap<>());
            return self();
        }

        public GivenStep2Page the_poll_storage_service_returns_step2_data(PollStorageService service, String uuid) {
            Map<String, String> step2Data = new HashMap<>();
            step2Data.put("eventDate", "2024-01-15");
            step2Data.put("timeSlot1", "10:00");
            Mockito.when(service.retrievePollData(uuid)).thenReturn(step2Data);
            return self();
        }

        public GivenStep2Page the_poll_storage_service_returns_step2_start_date(PollStorageService service, String uuid,
                String startDate) {
            Map<String, String> data = new HashMap<>();
            data.put("eventDate", startDate);
            Mockito.when(service.retrievePollData(uuid)).thenReturn(data);
            return self();
        }

        public GivenStep2Page the_poll_storage_service_returns_existing_step3_data(PollStorageService service,
                String uuid) {
            Map<String, String> data = new HashMap<>();
            data.put("eventDate", "2024-01-15");
            data.put("expiryDate", "2024-04-15");
            Mockito.when(service.retrievePollData(uuid)).thenReturn(data);
            return self();
        }

        public GivenStep2Page the_poll_storage_service_returns_null_for_non_existent_uuid(PollStorageService service) {
            Mockito.when(service.retrievePollData(Mockito.anyString())).thenReturn(null);
            return self();
        }
    }

    /** WHEN **/
    public static class WhenUserOnStep2Page extends Stage<WhenUserOnStep2Page> {
        @ExpectedScenarioState
        private MockMvc mockMvc;

        @ProvidedScenarioState
        private ResultActions result;

        public WhenUserOnStep2Page the_user_presses_next_on_step2_form(String uuid) throws Exception {
            result = mockMvc.perform(post("/schedule-event-step2/" + uuid)
                    .param("action", "next"));
            return self();
        }

        public WhenUserOnStep2Page the_user_visits_step3_page(String uuid) throws Exception {
            result = mockMvc.perform(get("/schedule-event-step3/" + uuid));
            return self();
        }

        public WhenUserOnStep2Page the_user_clicks_back_on_step3_page(String uuid) throws Exception {
            result = mockMvc.perform(post("/schedule-event-step3/" + uuid)
                    .param("action", "back"));
            return self();
        }

        public WhenUserOnStep2Page the_user_visits_step3_page_without_uuid() throws Exception {
            result = mockMvc.perform(get("/schedule-event-step3"));
            return self();
        }

        public WhenUserOnStep2Page the_user_visits_step3_page_with_non_existent_uuid(String uuid) throws Exception {
            result = mockMvc.perform(get("/schedule-event-step3/" + uuid));
            return self();
        }
    }

    /** THEN **/
    public static class ThenStep3PageIsDisplayed extends Stage<ThenStep3PageIsDisplayed> {
        @ExpectedScenarioState
        private ResultActions result;

        @ExpectedScenarioState
        private MockMvc mockMvc;

        public ThenStep3PageIsDisplayed the_user_is_redirected_to_step3(String uuid) throws Exception {
            result.andExpect(status().isFound())
                    .andExpect(view().name("redirect:/schedule-event-step3/" + uuid));
            return self();
        }

        public ThenStep3PageIsDisplayed the_step3_page_is_returned_successfully(String uuid) throws Exception {
            // follow redirect manually
            ResultActions step3Result = mockMvc.perform(get("/schedule-event-step3/" + uuid));
            step3Result.andExpect(status().isOk())
                    .andExpect(view().name("schedule-event-step3"));
            return self();
        }

        /* ---------- Assertions for Acceptance Criterion 2 ---------- */
        public ThenStep3PageIsDisplayed the_step3_form_with_all_required_fields_is_displayed() throws Exception {
            result.andExpect(status().isOk())
                    .andExpect(view().name("schedule-event-step3"));

            String htmlContent = result.andReturn().getResponse().getContentAsString();
            Document doc = Jsoup.parse(htmlContent);

            assertThat(doc.select("input[type='date'][name='expiryDate']").size())
                    .as("Expiry date input field should be present")
                    .isEqualTo(1);

            assertThat(doc.select("button[data-test-back-button], a[data-test-back-button]").size())
                    .as("Back button should be present")
                    .isEqualTo(1);

            assertThat(doc.select("button[type='submit'][data-test-create-poll-button]").size())
                    .as("Create poll button should be present")
                    .isEqualTo(1);
            return self();
        }

        /* ---------- Assertions for Acceptance Criterion 3 ---------- */
        public ThenStep3PageIsDisplayed the_user_is_redirected_to_step2(String uuid) throws Exception {
            result.andExpect(status().isFound())
                    .andExpect(view().name("redirect:/schedule-event-step2/" + uuid));
            return self();
        }

        public ThenStep3PageIsDisplayed the_step2_fields_are_pre_filled_with_previous_data() throws Exception {
            // Follow redirect to step 2
            String redirectUrl = result.andReturn().getResponse().getRedirectedUrl();
            ResultActions step2Result = mockMvc.perform(get(redirectUrl));
            step2Result.andExpect(status().isOk())
                    .andExpect(view().name("schedule-event-step2"));

            String htmlContent = step2Result.andReturn().getResponse().getContentAsString();
            Document doc = Jsoup.parse(htmlContent);

            String dateValue = doc.select("input[data-test-date-field]").attr("value");
            assertThat(dateValue)
                    .as("Date field should be pre-filled")
                    .isEqualTo("2024-01-15");

            String timeSlot1Value = doc.select("input[data-test-time-field1]").attr("value");
            assertThat(timeSlot1Value)
                    .as("Time slot 1 should be pre-filled")
                    .isEqualTo("10:00");
            return self();
        }

        /* ---------- Assertions for Acceptance Criterion 4 ---------- */
        public ThenStep3PageIsDisplayed the_expiry_date_defaults_to(String expectedDate) throws Exception {
            result.andExpect(status().isOk())
                    .andExpect(view().name("schedule-event-step3"));

            String htmlContent = result.andReturn().getResponse().getContentAsString();
            Document doc = Jsoup.parse(htmlContent);

            String expiryDateValue = doc.select("input[name='expiryDate']").attr("value");
            assertThat(expiryDateValue)
                    .as("Expiry date should default to start date + 3 months")
                    .isEqualTo(expectedDate);
            return self();
        }

        /* ---------- Assertions for Acceptance Criterion 5 ---------- */
        public ThenStep3PageIsDisplayed the_step3_form_is_pre_populated_with_existing_data() throws Exception {
            result.andExpect(status().isOk())
                    .andExpect(view().name("schedule-event-step3"));

            String htmlContent = result.andReturn().getResponse().getContentAsString();
            Document doc = Jsoup.parse(htmlContent);

            String expiryDateValue = doc.select("input[name='expiryDate']").attr("value");
            assertThat(expiryDateValue)
                    .as("Expiry date field should be pre-filled with stored data")
                    .isEqualTo("2024-04-15");
            return self();
        }

        /* ---------- Assertions for Acceptance Criteria 6 & 7 ---------- */
        public ThenStep3PageIsDisplayed the_user_is_redirected_to_schedule_event_with_poll_not_found_message()
                throws Exception {
            result.andExpect(status().isFound())
                    .andExpect(view().name("redirect:/schedule-event?uuidNotFound=true"));

            // Follow redirect
            result = mockMvc.perform(get("/schedule-event?uuidNotFound=true"));
            result.andExpect(status().isOk())
                    .andExpect(view().name("schedule-event"))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                            .string(org.hamcrest.Matchers.containsString("Poll not found")));
            return self();
        }

        public ThenStep3PageIsDisplayed the_user_is_redirected_to_schedule_event_with_poll_id_missing_message()
                throws Exception {
            result.andExpect(status().isFound())
                    .andExpect(view().name("redirect:/schedule-event?uuidMissing=true"));

            // Follow redirect
            result = mockMvc.perform(get("/schedule-event?uuidMissing=true"));
            result.andExpect(status().isOk())
                    .andExpect(view().name("schedule-event"))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                            .string(org.hamcrest.Matchers.containsString("Poll id missing")));
            return self();
        }
    }
}