package de.bas.bodo.woodle;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit5.ScenarioTest;

import de.bas.bodo.woodle.service.PollStorageService;
import gg.jte.springframework.boot.autoconfigure.JteAutoConfiguration;
import lombok.SneakyThrows;

@WebMvcTest(WoodleFormsController.class)
@Import(JteAutoConfiguration.class)
public class EventJGivenTest extends ScenarioTest<EventJGivenTest.Given, EventJGivenTest.When, EventJGivenTest.Then> {

    @Autowired
    private MockMvc mockMvc;

    // Mockito mock bean registered in Spring context
    @MockitoBean
    private PollStorageService pollStorageService;

    @ScenarioState
    private ResultActions resultActions;

    @ScenarioState
    private String uuid;

    @BeforeEach
    void wireStages() {
        // Pass Spring mock into JGiven stage
        given().setPollStorageService(pollStorageService);
        when().setMockMvc(mockMvc);
    }

    @Test
    @SneakyThrows
    public void should_display_event_summary_for_valid_uuid() {
        given().a_poll_with_uuid_exists();
        when().the_user_accesses_the_event_page();
        then().the_event_summary_is_displayed_with_the_correct_data();
    }

    @Test
    @SneakyThrows
    public void should_redirect_to_schedule_event_for_invalid_uuid() {
        given().a_poll_with_uuid_does_not_exist();
        when().the_user_accesses_the_event_page();
        then().the_user_is_redirected_to_the_schedule_event_page_with_an_error();
    }

    public static class Given extends Stage<Given> {
        private PollStorageService pollStorageService;

        public Given setPollStorageService(PollStorageService pollStorageService) {
            this.pollStorageService = pollStorageService;
            return self();
        }

        @ScenarioState
        private String uuid;

        public Given a_poll_with_uuid_exists() {
            uuid = UUID.randomUUID().toString();
            Map<String, String> pollData = Map.of(
                    "activityTitle", "My Test Event",
                    "description", "This is a test event.",
                    "name", "Bodo",
                    "email", "bodoteichmann@gmail.com",
                    "eventDate", "2025-08-15",
                    "timeSlot1", "10:00",
                    "timeSlot2", "11:00");
            org.mockito.Mockito.when(pollStorageService.retrievePollData(uuid)).thenReturn(pollData);
            return self();
        }

        public Given a_poll_with_uuid_does_not_exist() {
            uuid = UUID.randomUUID().toString();
            org.mockito.Mockito.when(pollStorageService.retrievePollData(uuid)).thenReturn(null);
            return self();
        }
    }

    public static class When extends Stage<When> {
        private MockMvc mockMvc;

        public When setMockMvc(MockMvc mockMvc) {
            this.mockMvc = mockMvc;
            return self();
        }

        @ScenarioState
        private String uuid;

        @ScenarioState
        private ResultActions resultActions;

        public When the_user_accesses_the_event_page() throws Exception {
            resultActions = mockMvc.perform(get("/event/{uuid}", uuid));
            return self();
        }
    }

    public static class Then extends Stage<Then> {
        @ScenarioState
        private ResultActions resultActions;

        @ScenarioState
        private String uuid;

        public Then the_event_summary_is_displayed_with_the_correct_data() throws Exception {
            resultActions.andExpect(status().isOk())
                    .andExpect(view().name("event-summary"))
                    .andExpect(model().attributeExists("pollData"))
                    .andExpect(model().attribute("uuid", uuid));
            return self();
        }

        public Then the_user_is_redirected_to_the_schedule_event_page_with_an_error() throws Exception {
            resultActions.andExpect(status().is3xxRedirection())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                            .redirectedUrl("/schedule-event?uuidNotFound=true"));
            return self();
        }
    }
}
