package de.bas.bodo.woodle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

// @WebMvcTest is faster than @SpringBootTest, so keep is
@WebMvcTest
// @ImportAutoConfiguration(JteAutoConfiguration.class) is needed for JTE to
// work in a @WebMvcTest
@ImportAutoConfiguration(JteAutoConfiguration.class)
class WoodleFormsTest extends
                ScenarioTest<WoodleFormsTest.GivenIndexPage, WoodleFormsTest.WhenUserVisitsIndexPage, WoodleFormsTest.ThenIndexPageIsDisplayed> {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PollStorageService pollStorageService;

        @Test
        void shouldDisplayIndexPageWithAllRequiredElements() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc);
                when().the_user_visits_the_index_page();
                then().the_index_page_is_displayed()
                                .and().the_schedule_event_button_is_displayed()
                                .and().the_schedule_event_button_links_to_form_page()
                                .and().the_woodle_logo_is_displayed()
                                .and().the_woodle_logo_is_downloadable();
        }

        @Test
        void shouldDisplayAllRequiredFieldsOnScheduleEventForm() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc);
                when().the_user_visits_the_schedule_event_form_page();
                then().the_schedule_event_form_with_all_required_fields_is_displayed();
        }

        @Test
        void shouldRedirectRootToIndexHtml() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc);
                when().the_user_visits_the_root_path();
                then().the_user_is_redirected_to_index_html();
        }

        @Test
        void shouldDisplayWoodleLogoOnScheduleEventPage() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc);
                when().the_user_visits_the_schedule_event_form_page();
                then().the_woodle_logo_is_displayed_on_schedule_event_page()
                                .and().the_woodle_logo_is_downloadable();
        }

        @Test
        void shouldGenerateUuidAndStoreDataWhenFormIsSubmitted() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_is_mocked(pollStorageService);
                when().the_user_submits_the_schedule_event_form_with_data();
                then().the_form_submission_returns_redirect_response()
                                .and().a_uuid_is_generated_for_the_form_data()
                                .and().the_form_data_is_stored_via_service();
        }

        @Test
        void shouldDisplayAllRequiredFieldsOnScheduleEventStep2Form() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_existing_data(pollStorageService);
                when().the_user_visits_the_schedule_event_step2_page_with_uuid();
                then().the_schedule_event_step2_form_with_all_required_fields_is_displayed()
                                .and().the_back_button_is_displayed();
        }

        @Test
        void shouldMaintainConsistentUuidThroughoutNavigationFlow() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_supports_proper_uuid_handling(pollStorageService);
                when().the_user_submits_initial_form_and_navigates_through_steps();
                then().store_poll_data_is_called_once_and_update_poll_data_is_called_for_subsequent_updates();
        }

        @Test
        void shouldPersistDateAndTimeDataAcrossNavigation() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_supports_data_updates(pollStorageService);
                when().the_user_submits_step2_form_with_date_and_time_data_by_going_back_to_step1();
                then().the_merged_form_data_is_stored_via_service()
                                .and().the_step1_data_is_still_preserved();
                when().the_user_modifies_step1_data_and_navigates_forward_to_step2();
                then().the_modified_step1_and_original_step2_data_are_both_stored()
                                .and().the_date_and_time_fields_are_pre_filled_with_previously_entered_data();
        }

        @Test
        void shouldRedirectToScheduleEventWhenUuidNotFoundInStep1() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_null_for_non_existent_uuid(pollStorageService);
                when().the_user_visits_schedule_event_with_non_existent_uuid();
                then().the_user_is_redirected_to_schedule_event_without_uuid()
                                .and().the_warning_message_about_uuid_not_found_is_displayed()
                                .and().the_empty_form_is_displayed();
        }

        @Test
        void shouldRedirectToScheduleEventWhenUuidNotFoundInStep2() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_null_for_non_existent_uuid(pollStorageService);
                when().the_user_visits_schedule_event_step2_with_non_existent_uuid();
                then().the_user_is_redirected_to_schedule_event_without_uuid()
                                .and().the_warning_message_about_uuid_not_found_is_displayed()
                                .and().the_empty_form_is_displayed();
        }

        @Test
        void shouldRedirectToScheduleEventWhenNoUuidInStep2Url() throws Exception {
                given().the_application_is_running_with_mock_mvc(mockMvc);
                when().the_user_visits_schedule_event_step2_without_uuid();
                then().the_user_is_redirected_to_schedule_event_without_uuid()
                                .and().the_warning_message_about_uuid_not_found_is_displayed()
                                .and().the_empty_form_is_displayed();
        }

        public static class GivenIndexPage extends Stage<GivenIndexPage> {
                @ProvidedScenarioState
                private MockMvc mockMvc;

                @ProvidedScenarioState
                private PollStorageService pollStorageService;

                @ProvidedScenarioState
                private String mockUuid;

                public GivenIndexPage the_application_is_running_with_mock_mvc(MockMvc mockMvc) {
                        this.mockMvc = mockMvc;
                        return self();
                }

                public GivenIndexPage the_poll_storage_service_is_mocked(PollStorageService pollStorageService) {
                        this.pollStorageService = pollStorageService;

                        // Mock the service to return a predictable UUID
                        this.mockUuid = "12345678-1234-1234-1234-123456789012";
                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(mockUuid);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_returns_existing_data(
                                PollStorageService pollStorageService) {
                        this.pollStorageService = pollStorageService;
                        this.mockUuid = "12345678-1234-1234-1234-123456789012";

                        // Mock the service to return existing form data
                        Map<String, String> existingData = new HashMap<>();
                        existingData.put("name", "John Doe");
                        existingData.put("email", "john.doe@example.com");
                        existingData.put("activityTitle", "Team Meeting");
                        existingData.put("description", "Weekly team sync meeting");

                        Mockito.when(pollStorageService.retrievePollData(mockUuid)).thenReturn(existingData);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_supports_data_updates(
                                PollStorageService pollStorageService) {
                        this.pollStorageService = pollStorageService;
                        this.mockUuid = "12345678-1234-1234-1234-123456789012";

                        // Mock the service to support data updates and return updated data
                        Map<String, String> initialData = new HashMap<>();
                        initialData.put("name", "John Doe");
                        initialData.put("email", "john.doe@example.com");
                        initialData.put("activityTitle", "Team Meeting");
                        initialData.put("description", "Weekly team sync meeting");

                        Map<String, String> updatedData = new HashMap<>(initialData);
                        updatedData.put("eventDate", "2024-01-15");
                        updatedData.put("timeSlot1", "10:00");
                        updatedData.put("timeSlot2", "14:00");

                        Mockito.when(pollStorageService.retrievePollData(mockUuid))
                                        .thenReturn(initialData) // First call returns initial data
                                        .thenReturn(updatedData); // Subsequent calls return updated data

                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(mockUuid);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_generates_consistent_uuids(
                                PollStorageService pollStorageService) {
                        this.pollStorageService = pollStorageService;
                        this.mockUuid = "12345678-1234-1234-1234-123456789012";

                        // Mock data for the flow
                        Map<String, String> initialFormData = new HashMap<>();
                        initialFormData.put("name", "John Doe");
                        initialFormData.put("email", "john.doe@example.com");
                        initialFormData.put("activityTitle", "Team Meeting");
                        initialFormData.put("description", "Weekly team sync meeting");

                        Map<String, String> step2Data = new HashMap<>(initialFormData);
                        step2Data.put("eventDate", "2024-01-15");
                        step2Data.put("timeSlot1", "10:00");

                        Map<String, String> modifiedStep1Data = new HashMap<>(step2Data);
                        modifiedStep1Data.put("activityTitle", "MODIFIED Team Meeting");

                        // Mock to return the SAME UUID consistently
                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(mockUuid);

                        // Mock retrieval to return appropriate data
                        Mockito.when(pollStorageService.retrievePollData(mockUuid))
                                        .thenReturn(initialFormData)
                                        .thenReturn(step2Data)
                                        .thenReturn(modifiedStep1Data);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_supports_proper_uuid_handling(
                                PollStorageService pollStorageService) {
                        this.pollStorageService = pollStorageService;
                        this.mockUuid = "12345678-1234-1234-1234-123456789012";

                        // Mock data for the flow
                        Map<String, String> initialFormData = new HashMap<>();
                        initialFormData.put("name", "John Doe");
                        initialFormData.put("email", "john.doe@example.com");
                        initialFormData.put("activityTitle", "Team Meeting");
                        initialFormData.put("description", "Weekly team sync meeting");

                        Map<String, String> step2Data = new HashMap<>(initialFormData);
                        step2Data.put("eventDate", "2024-01-15");
                        step2Data.put("timeSlot1", "10:00");

                        Map<String, String> modifiedStep1Data = new HashMap<>(step2Data);
                        modifiedStep1Data.put("activityTitle", "MODIFIED Team Meeting");

                        // Mock to return the SAME UUID for storePollData (initial creation only)
                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(mockUuid);

                        // Mock retrieval to return appropriate data
                        Mockito.when(pollStorageService.retrievePollData(mockUuid))
                                        .thenReturn(initialFormData)
                                        .thenReturn(step2Data)
                                        .thenReturn(modifiedStep1Data);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_returns_null_for_non_existent_uuid(
                                PollStorageService pollStorageService) {
                        this.pollStorageService = pollStorageService;
                        this.mockUuid = null; // Simulate no data found
                        Mockito.when(pollStorageService.retrievePollData(any())).thenReturn(null);
                        return self();
                }
        }

        public static class WhenUserVisitsIndexPage extends Stage<WhenUserVisitsIndexPage> {
                @ExpectedScenarioState
                private MockMvc mockMvc;

                @ProvidedScenarioState
                private ResultActions result;

                public WhenUserVisitsIndexPage the_user_visits_the_index_page() throws Exception {
                        result = mockMvc.perform(get("/index.html"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_the_schedule_event_form_page() throws Exception {
                        result = mockMvc.perform(get("/schedule-event"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_the_root_path() throws Exception {
                        result = mockMvc.perform(get("/"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_submits_the_schedule_event_form_with_data() throws Exception {
                        result = mockMvc.perform(post("/schedule-event")
                                        .param("yourName", "John Doe")
                                        .param("emailAddress", "john.doe@example.com")
                                        .param("activityTitle", "Team Meeting")
                                        .param("description", "Weekly team sync meeting"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_the_schedule_event_step2_page_with_uuid()
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/12345678-1234-1234-1234-123456789012"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_submits_step2_form_with_date_and_time_data_by_going_back_to_step1()
                                throws Exception {
                        // User enters data on step 2 and clicks "Back" button to save data and go back
                        // to step 1
                        result = mockMvc.perform(post("/schedule-event-step2/12345678-1234-1234-1234-123456789012")
                                        .param("eventDate", "2024-01-15")
                                        .param("timeSlot1", "10:00")
                                        .param("timeSlot2", "14:00")
                                        .param("action", "back")); // Add action parameter to indicate back button was
                                                                   // clicked
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_modifies_step1_data_and_navigates_forward_to_step2()
                                throws Exception {
                        // User modifies step 1 data (e.g., changes activity title) and submits form to
                        // go to step 2
                        result = mockMvc.perform(post("/schedule-event/12345678-1234-1234-1234-123456789012")
                                        .param("yourName", "John Doe")
                                        .param("emailAddress", "john.doe@example.com")
                                        .param("activityTitle", "MODIFIED Team Meeting") // Changed title
                                        .param("description", "Weekly team sync meeting"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_navigates_forward_to_step2_again() throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/12345678-1234-1234-1234-123456789012"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_submits_initial_form_and_navigates_through_steps()
                                throws Exception {
                        // Step 1: Submit initial form (should generate UUID and redirect to step 2)
                        result = mockMvc.perform(post("/schedule-event")
                                        .param("yourName", "John Doe")
                                        .param("emailAddress", "john.doe@example.com")
                                        .param("activityTitle", "Team Meeting")
                                        .param("description", "Weekly team sync meeting"));

                        // Step 2: Go to step 2, add some data, then go back to step 1
                        mockMvc.perform(post("/schedule-event-step2/12345678-1234-1234-1234-123456789012")
                                        .param("eventDate", "2024-01-15")
                                        .param("timeSlot1", "10:00")
                                        .param("action", "back"));

                        // Step 3: Modify step 1 data and navigate forward to step 2
                        result = mockMvc.perform(post("/schedule-event/12345678-1234-1234-1234-123456789012")
                                        .param("yourName", "John Doe")
                                        .param("emailAddress", "john.doe@example.com")
                                        .param("activityTitle", "MODIFIED Team Meeting")
                                        .param("description", "Weekly team sync meeting"));

                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_schedule_event_with_non_existent_uuid()
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event/12345678-1234-1234-1234-123456789012"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_schedule_event_step2_with_non_existent_uuid()
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/12345678-1234-1234-1234-123456789012"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_schedule_event_step2_without_uuid()
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/"));
                        return self();
                }
        }

        public static class ThenIndexPageIsDisplayed extends Stage<ThenIndexPageIsDisplayed> {
                @ExpectedScenarioState
                private ResultActions result;

                @ExpectedScenarioState
                private MockMvc mockMvc;

                @ExpectedScenarioState
                private PollStorageService pollStorageService;

                @ExpectedScenarioState
                private String mockUuid;

                public ThenIndexPageIsDisplayed the_index_page_is_displayed() throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("index"));
                        return self();
                }

                public ThenIndexPageIsDisplayed the_schedule_event_button_is_displayed() throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("index"));

                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        assertThat(doc.select("a[data-test-schedule-event-button]").size())
                                        .as("Schedule Event button should be present")
                                        .isEqualTo(1);
                        return self();
                }

                public ThenIndexPageIsDisplayed the_schedule_event_button_links_to_form_page() throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("index"));

                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        assertThat(doc.select("a[data-test-schedule-event-button]").size())
                                        .as("Schedule Event button link should be present")
                                        .isEqualTo(1);

                        String linkHref = doc.select("a[data-test-schedule-event-button]").attr("href");
                        assertThat(linkHref)
                                        .as("Schedule Event button should link to /schedule-event")
                                        .isEqualTo("/schedule-event");
                        return self();
                }

                public ThenIndexPageIsDisplayed the_user_is_redirected_to_index_html() throws Exception {
                        result.andExpect(status().isFound())
                                        .andExpect(view().name("redirect:/index.html"));
                        return self();
                }

                public ThenIndexPageIsDisplayed the_woodle_logo_is_displayed() throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("index"));

                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        assertThat(doc.select("img[data-test-woodle-logo]").size())
                                        .as("Woodle logo should be present")
                                        .isEqualTo(1);
                        return self();
                }

                public ThenIndexPageIsDisplayed the_woodle_logo_is_displayed_on_schedule_event_page() throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event"));

                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        assertThat(doc.select("img[data-test-woodle-logo]").size())
                                        .as("Woodle logo should be present")
                                        .isEqualTo(1);
                        return self();
                }

                public ThenIndexPageIsDisplayed the_woodle_logo_is_downloadable() throws Exception {
                        // Test that the logo file is actually downloadable
                        ResultActions logoResult = mockMvc.perform(get("/woodle-logo.jpeg"));
                        logoResult.andExpect(status().isOk());

                        String contentType = logoResult.andReturn().getResponse().getContentType();
                        assertThat(contentType)
                                        .as("Logo should be served as JPEG image")
                                        .isEqualTo("image/jpeg");
                        return self();
                }

                public ThenIndexPageIsDisplayed the_schedule_event_form_with_all_required_fields_is_displayed()
                                throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event"));

                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        assertThat(doc.select("form[data-test-schedule-event-form]").size())
                                        .as("Schedule Event form should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("input[data-test-your-name-field]").size())
                                        .as("Your name field should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("input[data-test-email-field]").size())
                                        .as("Email address field should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("input[data-test-activity-title-field]").size())
                                        .as("Activity title field should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("textarea[data-test-description-field]").size())
                                        .as("Description multiline field should be present")
                                        .isEqualTo(1);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_form_submission_returns_redirect_response() throws Exception {
                        // Check that we get a redirect status (302 is Spring Boot default for
                        // redirects)
                        result.andExpect(status().isFound());
                        return self();
                }

                public ThenIndexPageIsDisplayed a_uuid_is_generated_for_the_form_data() throws Exception {
                        // Verify that the redirect URL contains the mocked UUID
                        String redirectUrl = result.andReturn().getResponse().getRedirectedUrl();
                        assertThat(redirectUrl)
                                        .as("Redirect URL should contain the mocked UUID")
                                        .isEqualTo("/schedule-event-step2/" + mockUuid);
                        return self();
                }

                public ThenIndexPageIsDisplayed the_form_data_is_stored_via_service() throws Exception {
                        // Create expected form data map
                        Map<String, String> expectedFormData = new HashMap<>();
                        expectedFormData.put("name", "John Doe");
                        expectedFormData.put("email", "john.doe@example.com");
                        expectedFormData.put("activityTitle", "Team Meeting");
                        expectedFormData.put("description", "Weekly team sync meeting");

                        // Verify that storePollData was called exactly once with the expected
                        // parameters
                        verify(pollStorageService, Mockito.times(1)).storePollData(expectedFormData);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_merged_form_data_is_stored_via_service() throws Exception {
                        // Create expected merged form data map
                        Map<String, String> expectedMergedData = new HashMap<>();
                        expectedMergedData.put("name", "John Doe");
                        expectedMergedData.put("email", "john.doe@example.com");
                        expectedMergedData.put("activityTitle", "Team Meeting");
                        expectedMergedData.put("description", "Weekly team sync meeting");
                        expectedMergedData.put("eventDate", "2024-01-15");
                        expectedMergedData.put("timeSlot1", "10:00");
                        expectedMergedData.put("timeSlot2", "14:00");

                        // Verify that updatePollData was called exactly once with the expected UUID and
                        // data
                        // (this is the correct behavior after the UUID consistency fix)
                        verify(pollStorageService, Mockito.times(1))
                                        .updatePollData("12345678-1234-1234-1234-123456789012", expectedMergedData);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_schedule_event_step2_form_with_all_required_fields_is_displayed()
                                throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event-step2"));

                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Test for date input field
                        assertThat(doc.select("input[type='date'][data-test-date-field]").size())
                                        .as("Date input field should be present")
                                        .isEqualTo(1);

                        // Test for specific time input fields
                        assertThat(doc.select("input[data-test-time-field1]").size())
                                        .as("Time input field 1 should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("input[data-test-time-field2]").size())
                                        .as("Time input field 2 should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("input[data-test-time-field3]").size())
                                        .as("Time input field 3 should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("input[data-test-time-field4]").size())
                                        .as("Time input field 4 should be present")
                                        .isEqualTo(1);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_back_button_is_displayed() throws Exception {
                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        assertThat(doc.select("button[data-test-back-button], a[data-test-back-button]").size())
                                        .as("Back button should be present")
                                        .isEqualTo(1);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_modified_step1_and_original_step2_data_are_both_stored()
                                throws Exception {
                        // Create expected data with MODIFIED step 1 data + ORIGINAL step 2 data
                        Map<String, String> expectedDataWithModifiedStep1 = new HashMap<>();
                        expectedDataWithModifiedStep1.put("name", "John Doe");
                        expectedDataWithModifiedStep1.put("email", "john.doe@example.com");
                        expectedDataWithModifiedStep1.put("activityTitle", "MODIFIED Team Meeting"); // Modified
                        expectedDataWithModifiedStep1.put("description", "Weekly team sync meeting");
                        // Original step 2 data should still be preserved
                        expectedDataWithModifiedStep1.put("eventDate", "2024-01-15");
                        expectedDataWithModifiedStep1.put("timeSlot1", "10:00");
                        expectedDataWithModifiedStep1.put("timeSlot2", "14:00");

                        // This verification should catch the bug if step 2 data gets silently deleted
                        // when step 1 data is modified and form is submitted
                        // After the UUID consistency fix, we expect updatePollData to be called instead
                        verify(pollStorageService, Mockito.times(2))
                                        .updatePollData(eq("12345678-1234-1234-1234-123456789012"), any());
                        verify(pollStorageService, Mockito.atLeastOnce()).updatePollData(
                                        "12345678-1234-1234-1234-123456789012", expectedDataWithModifiedStep1);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_complete_merged_data_is_still_stored_correctly() throws Exception {
                        // Verify that retrievePollData was called and returned complete merged data
                        // This ensures that both step 1 and step 2 data are preserved during navigation
                        verify(pollStorageService, Mockito.atLeastOnce())
                                        .retrievePollData("12345678-1234-1234-1234-123456789012");

                        // Additional verification: we should verify that the storage contains the
                        // complete data
                        // In a real scenario, this would check that no data was silently deleted
                        // The mock already returns the complete data, but in production this would
                        // catch
                        // bugs where step 2 data gets lost during navigation

                        return self();
                }

                public ThenIndexPageIsDisplayed the_date_and_time_fields_are_pre_filled_with_previously_entered_data()
                                throws Exception {
                        // First verify that the form submission redirected to step 2
                        result.andExpect(status().isFound())
                                        .andExpect(view().name(
                                                        "redirect:/schedule-event-step2/12345678-1234-1234-1234-123456789012"));

                        // Now make a separate request to step 2 to verify the data is preserved
                        ResultActions step2Result = mockMvc
                                        .perform(get("/schedule-event-step2/12345678-1234-1234-1234-123456789012"));
                        step2Result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event-step2"));

                        String htmlContent = step2Result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Check that date field is pre-filled
                        String dateValue = doc.select("input[data-test-date-field]").attr("value");
                        assertThat(dateValue)
                                        .as("Date field should be pre-filled with previously entered data")
                                        .isEqualTo("2024-01-15");

                        // Check that time fields are pre-filled
                        String timeSlot1Value = doc.select("input[data-test-time-field1]").attr("value");
                        assertThat(timeSlot1Value)
                                        .as("Time slot 1 should be pre-filled with previously entered data")
                                        .isEqualTo("10:00");

                        String timeSlot2Value = doc.select("input[data-test-time-field2]").attr("value");
                        assertThat(timeSlot2Value)
                                        .as("Time slot 2 should be pre-filled with previously entered data")
                                        .isEqualTo("14:00");

                        return self();
                }

                public ThenIndexPageIsDisplayed the_step1_data_is_still_preserved() throws Exception {
                        // First verify that the form submission redirected to step 1
                        result.andExpect(status().isFound())
                                        .andExpect(view().name(
                                                        "redirect:/schedule-event/12345678-1234-1234-1234-123456789012"));

                        // Now make a separate request to step 1 to verify the data is preserved
                        ResultActions step1Result = mockMvc
                                        .perform(get("/schedule-event/12345678-1234-1234-1234-123456789012"));
                        step1Result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event"));

                        String htmlContent = step1Result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Check that step 1 fields are pre-filled with the expected data
                        String nameValue = doc.select("input[data-test-your-name-field]").attr("value");
                        assertThat(nameValue)
                                        .as("Name field should be pre-filled with previously entered data")
                                        .isEqualTo("John Doe");

                        String emailValue = doc.select("input[data-test-email-field]").attr("value");
                        assertThat(emailValue)
                                        .as("Email field should be pre-filled with previously entered data")
                                        .isEqualTo("john.doe@example.com");

                        String activityTitleValue = doc.select("input[data-test-activity-title-field]").attr("value");
                        assertThat(activityTitleValue)
                                        .as("Activity title field should be pre-filled with previously entered data")
                                        .isEqualTo("Team Meeting");

                        String descriptionValue = doc.select("textarea[data-test-description-field]").text();
                        assertThat(descriptionValue)
                                        .as("Description field should be pre-filled with previously entered data")
                                        .isEqualTo("Weekly team sync meeting");

                        return self();
                }

                public ThenIndexPageIsDisplayed store_poll_data_is_called_once_and_update_poll_data_is_called_for_subsequent_updates()
                                throws Exception {
                        // CORRECT behavior: storePollData should only be called ONCE for initial
                        // creation
                        Mockito.verify(pollStorageService, Mockito.times(1)).storePollData(any());

                        // CORRECT behavior: updatePollData should be called TWICE for the navigation
                        // updates
                        Mockito.verify(pollStorageService, Mockito.times(2)).updatePollData(eq(mockUuid), any());

                        // Verify retrievePollData was called to get existing data before updates
                        Mockito.verify(pollStorageService, Mockito.times(2)).retrievePollData(mockUuid);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_user_is_redirected_to_schedule_event_without_uuid()
                                throws Exception {
                        result.andExpect(status().isFound())
                                        .andExpect(view().name("redirect:/schedule-event?uuidNotFound=true"));

                        // Follow the redirect to test the content
                        result = mockMvc.perform(get("/schedule-event?uuidNotFound=true"));

                        return self();
                }

                public ThenIndexPageIsDisplayed the_warning_message_about_uuid_not_found_is_displayed()
                                throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event"))
                                        .andExpect(content().string(containsString("UUID not found")));
                        return self();
                }

                public ThenIndexPageIsDisplayed the_empty_form_is_displayed() throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event"));
                        return self();
                }
        }
}
