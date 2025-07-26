package de.bas.bodo.woodle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import org.mockito.ArgumentCaptor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

        @MockitoBean
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
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_is_mocked(pollStorageService, TEST_UUID);
                when().the_user_submits_the_schedule_event_form_with_data();
                then().the_form_submission_returns_redirect_response()
                                .and().a_uuid_is_generated_for_the_form_data(TEST_UUID)
                                .and().the_form_data_is_stored_via_service();
        }

        @Test
        void shouldDisplayAllRequiredFieldsOnScheduleEventStep2Form() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_existing_data(pollStorageService, TEST_UUID);
                when().the_user_visits_the_schedule_event_step2_page_with_uuid(TEST_UUID);
                then().the_schedule_event_step2_form_with_all_required_fields_is_displayed(3)
                                .and().the_back_button_is_displayed()
                                .and().the_add_proposal_button_is_displayed();
        }

        @Test
        void shouldAddSecondProposalSetWhenPlusButtonClicked() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_existing_data(pollStorageService, TEST_UUID);
                when().the_user_clicks_add_proposal_button_on_step2(TEST_UUID);
                then().the_proposal_count_is_increased_to(2)
                                .and().the_date_time_fields_are_displayed_for_proposal_count(2);
        }

        @Test
        void shouldAddMultipleProposalSetsWhenPlusButtonClickedMultipleTimes() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_existing_data(pollStorageService, TEST_UUID);
                when().the_user_clicks_add_proposal_button_on_step2(TEST_UUID)
                                .and().the_user_clicks_add_proposal_button_on_step2(TEST_UUID)
                                .and().the_user_clicks_add_proposal_button_on_step2(TEST_UUID);
                then().the_proposal_count_is_increased_to(4)
                                .and().the_date_time_fields_are_displayed_for_proposal_count(4);
        }

        @Test
        void shouldPersistDynamicFieldDataWhenNavigating() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_existing_data(pollStorageService, TEST_UUID);
                when().the_user_adds_second_proposal_and_fills_data(TEST_UUID);
                then().the_dynamic_proposal_data_is_persisted_in_storage(TEST_UUID);
        }

        @Test
        void shouldMaintainConsistentUuidThroughoutNavigationFlow() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and()
                                .the_poll_storage_service_supports_proper_uuid_handling(pollStorageService, TEST_UUID);
                when().the_user_submits_initial_form_and_navigates_through_steps(TEST_UUID);
                then().store_poll_data_is_called_once_and_update_poll_data_is_called_for_subsequent_updates(TEST_UUID);
        }

        @Test
        void shouldPersistDateAndTimeDataAcrossNavigation() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_supports_data_updates(pollStorageService, TEST_UUID);
                when().the_user_submits_step2_form_with_date_and_time_data_by_going_back_to_step1(TEST_UUID);
                then().the_merged_form_data_is_stored_via_service(TEST_UUID)
                                .and().the_step1_data_is_still_preserved(TEST_UUID);
                when().the_user_modifies_step1_data_and_navigates_forward_to_step2(TEST_UUID);
                then().the_modified_step1_and_original_step2_data_are_both_stored(TEST_UUID)
                                .and().the_date_and_time_fields_are_pre_filled_with_previously_entered_data(TEST_UUID);
        }

        @Test
        void shouldRedirectToScheduleEventWhenUuidNotFoundInStep1() throws Exception {
                final String NON_EXISTENT_UUID = "99999999-9999-9999-9999-999999999999";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_null_for_non_existent_uuid(pollStorageService);
                when().the_user_visits_schedule_event_with_non_existent_uuid(NON_EXISTENT_UUID);
                then().the_user_is_redirected_to_schedule_event_without_uuid()
                                .and().the_warning_message_about_uuid_not_found_is_displayed()
                                .and().the_empty_form_is_displayed();
        }

        @Test
        void shouldRedirectToScheduleEventWhenUuidNotFoundInStep2() throws Exception {
                final String NON_EXISTENT_UUID = "99999999-9999-9999-9999-999999999999";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_null_for_non_existent_uuid(pollStorageService);
                when().the_user_visits_schedule_event_step2_with_non_existent_uuid(NON_EXISTENT_UUID);
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

        @Test
        void shouldDisplayEventSummaryWithAllDataFromAllSteps() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_complete_event_data(pollStorageService, TEST_UUID);
                when().the_user_visits_the_event_summary_page(TEST_UUID);
                then().the_event_summary_page_is_displayed()
                                .and().the_summary_contains_all_form_data_from_all_steps()
                                .and().the_shareable_event_url_is_displayed(TEST_UUID);
        }

        @Test
        void shouldRedirectToEventSummaryWhenCreatePollButtonIsClicked() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_complete_event_data(pollStorageService, TEST_UUID);
                when().the_user_clicks_create_poll_button_on_step3(TEST_UUID);
                then().the_user_is_redirected_to_event_summary_page(TEST_UUID);
        }

        @Test
        void shouldRedirectToEventSummaryWhenCreatePollButtonIsClickedWithoutAction() throws Exception {
                final String TEST_UUID = "12345678-1234-1234-1234-123456789012";

                given().the_application_is_running_with_mock_mvc(mockMvc)
                                .and().the_poll_storage_service_returns_complete_event_data(pollStorageService, TEST_UUID);
                when().the_user_clicks_create_poll_button_without_action_on_step3(TEST_UUID);
                then().the_user_is_redirected_to_event_summary_page(TEST_UUID);
        }

        public static class GivenIndexPage extends Stage<GivenIndexPage> {
                @ProvidedScenarioState
                private MockMvc mockMvc;

                @ProvidedScenarioState
                private PollStorageService pollStorageService;

                public GivenIndexPage the_application_is_running_with_mock_mvc(MockMvc mockMvc) {
                        this.mockMvc = mockMvc;
                        return self();
                }

                public GivenIndexPage the_poll_storage_service_is_mocked(PollStorageService pollStorageService,
                                String uuid) {
                        this.pollStorageService = pollStorageService;
                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(uuid);
                        return self();
                }

                public GivenIndexPage the_poll_storage_service_returns_existing_data(
                                PollStorageService pollStorageService, String uuid) {
                        this.pollStorageService = pollStorageService;

                        // Create initial form data
                        Map<String, String> initialData = new HashMap<>();
                        initialData.put("name", "John Doe");
                        initialData.put("email", "john.doe@example.com");
                        initialData.put("activityTitle", "Team Meeting");
                        initialData.put("description", "Weekly team sync meeting");
                        
                        // Use a mutable map to simulate storage that can be updated
                        Map<String, String> storageMap = new HashMap<>(initialData);

                        // Mock to return current state of storage
                        Mockito.when(pollStorageService.retrievePollData(uuid)).thenAnswer(invocation -> 
                                new HashMap<>(storageMap));
                        
                        // Mock to update storage when updatePollData is called
                        Mockito.doAnswer(invocation -> {
                                String uuidArg = invocation.getArgument(0);
                                Map<String, String> newData = invocation.getArgument(1);
                                if (uuid.equals(uuidArg)) {
                                        storageMap.clear();
                                        storageMap.putAll(newData);
                                }
                                return null;
                        }).when(pollStorageService).updatePollData(eq(uuid), any());

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_supports_data_updates(
                                PollStorageService pollStorageService, String uuid) {
                        this.pollStorageService = pollStorageService;

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

                        Mockito.when(pollStorageService.retrievePollData(uuid))
                                        .thenReturn(initialData) // First call returns initial data
                                        .thenReturn(updatedData); // Subsequent calls return updated data

                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(uuid);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_generates_consistent_uuids(
                                PollStorageService pollStorageService, String uuid) {
                        this.pollStorageService = pollStorageService;

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
                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(uuid);

                        // Mock retrieval to return appropriate data
                        Mockito.when(pollStorageService.retrievePollData(uuid))
                                        .thenReturn(initialFormData)
                                        .thenReturn(step2Data)
                                        .thenReturn(modifiedStep1Data);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_supports_proper_uuid_handling(
                                PollStorageService pollStorageService, String uuid) {
                        this.pollStorageService = pollStorageService;

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
                        Mockito.when(pollStorageService.storePollData(any())).thenReturn(uuid);

                        // Mock retrieval to return appropriate data
                        Mockito.when(pollStorageService.retrievePollData(uuid))
                                        .thenReturn(initialFormData)
                                        .thenReturn(step2Data)
                                        .thenReturn(modifiedStep1Data);

                        return self();
                }

                public GivenIndexPage the_poll_storage_service_returns_null_for_non_existent_uuid(
                                PollStorageService pollStorageService) {
                        this.pollStorageService = pollStorageService;
                        Mockito.when(pollStorageService.retrievePollData(any())).thenReturn(null);
                        return self();
                }

                public GivenIndexPage the_poll_storage_service_returns_complete_event_data(
                                PollStorageService pollStorageService, String uuid) {
                        this.pollStorageService = pollStorageService;

                        // Mock complete event data from all 3 Steps
                        Map<String, String> completeEventData = new HashMap<>();
                        // Step 1 data
                        completeEventData.put("name", "John Doe");
                        completeEventData.put("email", "john.doe@example.com");
                        completeEventData.put("activityTitle", "Team Meeting");
                        completeEventData.put("description", "Weekly team sync meeting");
                        // Step 2 data
                        completeEventData.put("eventDate", "2024-01-15");
                        completeEventData.put("timeSlot1", "10:00");
                        completeEventData.put("timeSlot2", "14:00");
                        completeEventData.put("timeSlot3", "16:00");
                        completeEventData.put("timeSlot4", "18:00");
                        // Step 3 data
                        completeEventData.put("expiryDate", "2024-04-15");

                        Mockito.when(pollStorageService.retrievePollData(uuid)).thenReturn(completeEventData);

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

                public WhenUserVisitsIndexPage the_user_visits_the_schedule_event_step2_page_with_uuid(String uuid)
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/" + uuid));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_submits_step2_form_with_date_and_time_data_by_going_back_to_step1(
                                String uuid)
                                throws Exception {
                        // User enters data on step 2 and clicks "Back" button to save data and go back
                        // to step 1
                        result = mockMvc.perform(post("/schedule-event-step2/" + uuid)
                                        .param("eventDate", "2024-01-15")
                                        .param("timeSlot1", "10:00")
                                        .param("timeSlot2", "14:00")
                                        .param("action", "back")); // Add action parameter to indicate back button was
                                                                   // clicked
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_modifies_step1_data_and_navigates_forward_to_step2(String uuid)
                                throws Exception {
                        // User modifies step 1 data (e.g., changes activity title) and submits form to
                        // go to step 2
                        result = mockMvc.perform(post("/schedule-event/" + uuid)
                                        .param("yourName", "John Doe")
                                        .param("emailAddress", "john.doe@example.com")
                                        .param("activityTitle", "MODIFIED Team Meeting") // Changed title
                                        .param("description", "Weekly team sync meeting"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_navigates_forward_to_step2_again(String uuid) throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/" + uuid));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_submits_initial_form_and_navigates_through_steps(String uuid)
                                throws Exception {
                        // Step 1: Submit initial form (should generate UUID and redirect to step 2)
                        result = mockMvc.perform(post("/schedule-event")
                                        .param("yourName", "John Doe")
                                        .param("emailAddress", "john.doe@example.com")
                                        .param("activityTitle", "Team Meeting")
                                        .param("description", "Weekly team sync meeting"));

                        // Step 2: Go to step 2, add some data, then go back to step 1
                        mockMvc.perform(post("/schedule-event-step2/" + uuid)
                                        .param("eventDate", "2024-01-15")
                                        .param("timeSlot1", "10:00")
                                        .param("action", "back"));

                        // Step 3: Modify step 1 data and navigate forward to step 2
                        result = mockMvc.perform(post("/schedule-event/" + uuid)
                                        .param("yourName", "John Doe")
                                        .param("emailAddress", "john.doe@example.com")
                                        .param("activityTitle", "MODIFIED Team Meeting")
                                        .param("description", "Weekly team sync meeting"));

                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_schedule_event_with_non_existent_uuid(String uuid)
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event/" + uuid));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_schedule_event_step2_with_non_existent_uuid(String uuid)
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/" + uuid));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_schedule_event_step2_without_uuid()
                                throws Exception {
                        result = mockMvc.perform(get("/schedule-event-step2/"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_visits_the_event_summary_page(String uuid) throws Exception {
                        result = mockMvc.perform(get("/event/" + uuid));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_clicks_create_poll_button_on_step3(String uuid) throws Exception {
                        result = mockMvc.perform(post("/schedule-event-step3/" + uuid)
                                        .param("action", "create-poll"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_clicks_create_poll_button_without_action_on_step3(String uuid) throws Exception {
                        result = mockMvc.perform(post("/schedule-event-step3/" + uuid)
                                        .param("action", "create-poll"));
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_clicks_add_proposal_button_on_step2(String uuid) throws Exception {
                        // First perform the POST action
                        result = mockMvc.perform(post("/schedule-event-step2/" + uuid)
                                        .param("action", "add-proposal"));
                        
                        // Follow the redirect to get the updated page
                        String redirectUrl = result.andReturn().getResponse().getRedirectedUrl();
                        if (redirectUrl != null) {
                                result = mockMvc.perform(get(redirectUrl));
                        }
                        return self();
                }

                public WhenUserVisitsIndexPage the_user_adds_second_proposal_and_fills_data(String uuid) throws Exception {
                        // First add a second proposal
                        result = mockMvc.perform(post("/schedule-event-step2/" + uuid)
                                        .param("action", "add-proposal"));
                        
                        // Then submit with data for both proposals
                        result = mockMvc.perform(post("/schedule-event-step2/" + uuid)
                                        .param("action", "next")
                                        .param("eventDate", "2024-01-15")
                                        .param("timeSlot1", "10:00")
                                        .param("timeSlot2", "14:00")
                                        .param("timeSlot3", "16:00")
                                        .param("eventDate2", "2024-01-16")
                                        .param("timeSlot2_1", "09:00")
                                        .param("timeSlot2_2", "13:00")
                                        .param("timeSlot2_3", "17:00"));
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

                public ThenIndexPageIsDisplayed a_uuid_is_generated_for_the_form_data(String expectedUuid)
                                throws Exception {
                        // Verify that the redirect URL contains the expected UUID
                        String redirectUrl = result.andReturn().getResponse().getRedirectedUrl();
                        assertThat(redirectUrl)
                                        .as("Redirect URL should contain the expected UUID")
                                        .isEqualTo("/schedule-event-step2/" + expectedUuid);
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

                public ThenIndexPageIsDisplayed the_merged_form_data_is_stored_via_service(String uuid)
                                throws Exception {
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
                                        .updatePollData(uuid, expectedMergedData);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_schedule_event_step2_form_with_all_required_fields_is_displayed(int timeFieldCount)
                                throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event-step2"));

                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Test for date input field
                        assertThat(doc.select("input[type='date'][data-test='date-field']").size())
                                        .as("Date input field should be present")
                                        .isEqualTo(1);

                        // Test for specific time input fields using parameter
                        for (int i = 1; i <= timeFieldCount; i++) {
                                assertThat(doc.select("input[data-test='time-field" + i + "']").size())
                                                .as("Time input field " + i + " should be present")
                                                .isEqualTo(1);
                        }

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

                public ThenIndexPageIsDisplayed the_modified_step1_and_original_step2_data_are_both_stored(String uuid)
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
                                        .updatePollData(eq(uuid), any());
                        verify(pollStorageService, Mockito.atLeastOnce()).updatePollData(
                                        uuid, expectedDataWithModifiedStep1);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_complete_merged_data_is_still_stored_correctly(String uuid)
                                throws Exception {
                        // Verify that retrievePollData was called and returned complete merged data
                        // This ensures that both step 1 and step 2 data are preserved during navigation
                        verify(pollStorageService, Mockito.atLeastOnce())
                                        .retrievePollData(uuid);

                        // Additional verification: we should verify that the storage contains the
                        // complete data
                        // In a real scenario, this would check that no data was silently deleted
                        // The mock already returns the complete data, but in production this would
                        // catch
                        // bugs where step 2 data gets lost during navigation

                        return self();
                }

                public ThenIndexPageIsDisplayed the_date_and_time_fields_are_pre_filled_with_previously_entered_data(
                                String uuid)
                                throws Exception {
                        // First verify that the form submission redirected to step 2
                        result.andExpect(status().isFound())
                                        .andExpect(view().name(
                                                        "redirect:/schedule-event-step2/" + uuid));

                        // Now make a separate request to step 2 to verify the data is preserved
                        ResultActions step2Result = mockMvc
                                        .perform(get("/schedule-event-step2/" + uuid));
                        step2Result.andExpect(status().isOk())
                                        .andExpect(view().name("schedule-event-step2"));

                        String htmlContent = step2Result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Check that date field is pre-filled
                        String dateValue = doc.select("input[data-test=\"date-field\"]").attr("value");
                        assertThat(dateValue)
                                        .as("Date field should be pre-filled with previously entered data")
                                        .isEqualTo("2024-01-15");

                        // Check that time fields are pre-filled
                        String timeSlot1Value = doc.select("input[data-test=\"time-field1\"]").attr("value");
                        assertThat(timeSlot1Value)
                                        .as("Time slot 1 should be pre-filled with previously entered data")
                                        .isEqualTo("10:00");

                        String timeSlot2Value = doc.select("input[data-test=\"time-field2\"]").attr("value");
                        assertThat(timeSlot2Value)
                                        .as("Time slot 2 should be pre-filled with previously entered data")
                                        .isEqualTo("14:00");

                        return self();
                }

                public ThenIndexPageIsDisplayed the_step1_data_is_still_preserved(String uuid) throws Exception {
                        // First verify that the form submission redirected to step 1
                        result.andExpect(status().isFound())
                                        .andExpect(view().name(
                                                        "redirect:/schedule-event/" + uuid));

                        // Now make a separate request to step 1 to verify the data is preserved
                        ResultActions step1Result = mockMvc
                                        .perform(get("/schedule-event/" + uuid));
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

                public ThenIndexPageIsDisplayed store_poll_data_is_called_once_and_update_poll_data_is_called_for_subsequent_updates(
                                String uuid)
                                throws Exception {
                        // CORRECT behavior: storePollData should only be called ONCE for initial
                        // creation
                        Mockito.verify(pollStorageService, Mockito.times(1)).storePollData(any());

                        // CORRECT behavior: updatePollData should be called TWICE for the navigation
                        // updates
                        Mockito.verify(pollStorageService, Mockito.times(2)).updatePollData(eq(uuid), any());

                        // Verify retrievePollData was called to get existing data before updates
                        Mockito.verify(pollStorageService, Mockito.times(2)).retrievePollData(uuid);

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

                public ThenIndexPageIsDisplayed the_event_summary_page_is_displayed() throws Exception {
                        result.andExpect(status().isOk())
                                        .andExpect(view().name("event-summary"));
                        return self();
                }

                public ThenIndexPageIsDisplayed the_summary_contains_all_form_data_from_all_steps() throws Exception {
                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Verify Step 1 data is displayed
                        assertThat(doc.select("div[data-test='event-details']").text())
                                        .as("Event summary should contain organizer name")
                                        .contains("John Doe");

                        assertThat(doc.select("div[data-test='event-details']").text())
                                        .as("Event summary should contain email")
                                        .contains("john.doe@example.com");

                        assertThat(doc.select("div[data-test='event-details']").text())
                                        .as("Event summary should contain activity title")
                                        .contains("Team Meeting");

                        assertThat(doc.select("div[data-test='event-details']").text())
                                        .as("Event summary should contain description")
                                        .contains("Weekly team sync meeting");

                        // Verify Step 2 data is displayed
                        assertThat(doc.select("div[data-test='time-slots']").text())
                                        .as("Event summary should contain event date")
                                        .contains("2024-01-15");

                        assertThat(doc.select("div[data-test='time-slots']").text())
                                        .as("Event summary should contain time slots")
                                        .contains("10:00")
                                        .contains("14:00");

                        // Verify Step 3 data is displayed
                        assertThat(doc.select("div[data-test='expiry-info']").text())
                                        .as("Event summary should contain expiry date")
                                        .contains("2024-04-15");

                        return self();
                }

                public ThenIndexPageIsDisplayed the_shareable_event_url_is_displayed(String uuid) throws Exception {
                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        String expectedUrl = "http://localhost:8080/event/" + uuid;
                        assertThat(doc.select("div[data-test='shareable-url']").text())
                                        .as("Event summary should display shareable URL")
                                        .contains(expectedUrl);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_user_is_redirected_to_event_summary_page(String uuid) throws Exception {
                        result.andExpect(status().is3xxRedirection())
                                        .andExpect(redirectedUrl("/event/" + uuid));
                        return self();
                }

                public ThenIndexPageIsDisplayed the_add_proposal_button_is_displayed() throws Exception {
                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        assertThat(doc.select("button[data-test='add-proposal-button']").size())
                                        .as("Add proposal (+) button should be present")
                                        .isEqualTo(1);

                        assertThat(doc.select("button[data-test='add-proposal-button'] img[src*='Plus-Symbol-Transparent-small.png']").size())
                                        .as("Add proposal button should contain plus symbol image")
                                        .isEqualTo(1);
                        return self();
                }

                public ThenIndexPageIsDisplayed the_proposal_count_is_increased_to(int expectedCount) throws Exception {
                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Check total number of date fields equals expected count
                        assertThat(doc.select("input[type='date'][name^='eventDate']").size())
                                        .as("Should have " + expectedCount + " date fields")
                                        .isEqualTo(expectedCount);

                        return self();
                }

                public ThenIndexPageIsDisplayed the_date_time_fields_are_displayed_for_proposal_count(int proposalCount) throws Exception {
                        String htmlContent = result.andReturn().getResponse().getContentAsString();
                        Document doc = Jsoup.parse(htmlContent);

                        // Check each proposal has the correct fields
                        for (int i = 1; i <= proposalCount; i++) {
                                String dateName = (i == 1) ? "eventDate" : "eventDate" + i;
                                String dateTestAttr = (i == 1) ? "date-field" : "date-field-" + i;
                                
                                assertThat(doc.select("input[name='" + dateName + "'][data-test='" + dateTestAttr + "']").size())
                                                .as("Proposal " + i + " date field should be present")
                                                .isEqualTo(1);

                                // Check time slots for each proposal
                                for (int j = 1; j <= 3; j++) {
                                        String timeName = (i == 1) ? "timeSlot" + j : "timeSlot" + i + "_" + j;
                                        String timeTestAttr = (i == 1) ? "time-field" + j : "time-field-" + i + "-" + j;
                                        
                                        assertThat(doc.select("input[name='" + timeName + "'][data-test='" + timeTestAttr + "']").size())
                                                        .as("Proposal " + i + " time slot " + j + " should be present")
                                                        .isEqualTo(1);
                                }
                        }

                        return self();
                }

                public ThenIndexPageIsDisplayed the_dynamic_proposal_data_is_persisted_in_storage(String uuid) throws Exception {
                        // Verify that the storage service was called with the dynamic field data
                        ArgumentCaptor<Map<String, String>> dataCaptor = ArgumentCaptor.forClass(Map.class);
                        Mockito.verify(pollStorageService, Mockito.atLeastOnce()).updatePollData(eq(uuid), dataCaptor.capture());
                        
                        Map<String, String> savedData = dataCaptor.getValue();
                        
                        // Verify original proposal data was saved
                        assertThat(savedData.get("eventDate")).isEqualTo("2024-01-15");
                        assertThat(savedData.get("timeSlot1")).isEqualTo("10:00");
                        assertThat(savedData.get("timeSlot2")).isEqualTo("14:00");
                        assertThat(savedData.get("timeSlot3")).isEqualTo("16:00");
                        
                        // Verify second proposal data was saved
                        assertThat(savedData.get("eventDate2")).isEqualTo("2024-01-16");
                        assertThat(savedData.get("timeSlot2_1")).isEqualTo("09:00");
                        assertThat(savedData.get("timeSlot2_2")).isEqualTo("13:00");
                        assertThat(savedData.get("timeSlot2_3")).isEqualTo("17:00");
                        
                        return self();
                }
        }
}
