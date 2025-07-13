package de.bas.bodo.woodle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

                        // Test for time input fields
                        assertThat(doc.select("input[type='time'][data-test-time-field]").size())
                                        .as("Time input fields should be present")
                                        .isGreaterThan(0);

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
        }
}
