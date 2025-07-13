package de.bas.bodo.woodle;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.bas.bodo.woodle.service.PollStorageService;

@Controller
public class WoodleFormsController {

    private final PollStorageService pollStorageService;

    public WoodleFormsController(PollStorageService pollStorageService) {
        this.pollStorageService = pollStorageService;
    }

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index.html";
    }

    @GetMapping("/index.html")
    public String index() {
        return "index";
    }

    @GetMapping("/schedule-event")
    public String scheduleEvent() {
        return "schedule-event";
    }

    @GetMapping("/schedule-event/{uuid}")
    public String scheduleEventWithUuid(@PathVariable String uuid, Model model) {
        // Retrieve existing poll data
        Map<String, String> pollData = pollStorageService.retrievePollData(uuid);

        // Add data to model for the template
        if (pollData != null) {
            model.addAttribute("pollData", pollData);
        }
        model.addAttribute("uuid", uuid);

        return "schedule-event";
    }

    @PostMapping("/schedule-event")
    public String submitScheduleEvent(
            @RequestParam("yourName") String yourName,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("activityTitle") String activityTitle,
            @RequestParam("description") String description) {

        // Create form data object
        Map<String, String> formData = new HashMap<>();
        formData.put("name", yourName);
        formData.put("email", emailAddress);
        formData.put("activityTitle", activityTitle);
        formData.put("description", description);

        // Store poll data and get UUID
        String uuid = pollStorageService.storePollData(formData);

        // Redirect to step 2 with UUID in URL
        return "redirect:/schedule-event-step2/" + uuid;
    }

    @GetMapping("/schedule-event-step2/{uuid}")
    public String scheduleEventStep2(@PathVariable String uuid, Model model) {
        // Retrieve existing poll data
        Map<String, String> pollData = pollStorageService.retrievePollData(uuid);

        // Add data to model for the template
        if (pollData != null) {
            model.addAttribute("pollData", pollData);
        }
        model.addAttribute("uuid", uuid);

        return "schedule-event-step2";
    }
}