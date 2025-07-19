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
    public String scheduleEvent(@RequestParam(value = "uuidNotFound", required = false) String uuidNotFound,
            Model model) {
        if ("true".equals(uuidNotFound)) {
            model.addAttribute("warningMessage", "UUID not found");
        }
        return "schedule-event";
    }

    @GetMapping("/schedule-event/{uuid}")
    public String scheduleEventWithUuid(@PathVariable String uuid, Model model) {
        // Retrieve existing poll data
        Map<String, String> pollData = pollStorageService.retrievePollData(uuid);

        // If UUID not found, redirect to schedule-event with warning
        if (pollData == null) {
            return "redirect:/schedule-event?uuidNotFound=true";
        }

        // Add data to model for the template
        model.addAttribute("pollData", pollData);
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

    @PostMapping("/schedule-event/{uuid}")
    public String submitScheduleEventWithUuid(
            @PathVariable String uuid,
            @RequestParam("yourName") String yourName,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("activityTitle") String activityTitle,
            @RequestParam("description") String description) {

        // Retrieve existing poll data
        Map<String, String> existingData = pollStorageService.retrievePollData(uuid);

        // Create updated form data object, preserving existing data
        Map<String, String> updatedData = new HashMap<>();
        if (existingData != null) {
            updatedData.putAll(existingData);
        }

        // Update step 1 data
        updatedData.put("name", yourName);
        updatedData.put("email", emailAddress);
        updatedData.put("activityTitle", activityTitle);
        updatedData.put("description", description);

        // Update existing data with same UUID
        pollStorageService.updatePollData(uuid, updatedData);

        // Redirect to step 2 with UUID in URL
        return "redirect:/schedule-event-step2/" + uuid;
    }

    @GetMapping("/schedule-event-step2/")
    public String scheduleEventStep2WithoutUuid() {
        return "redirect:/schedule-event?uuidNotFound=true";
    }

    @GetMapping("/schedule-event-step2/{uuid}")
    public String scheduleEventStep2(@PathVariable String uuid, Model model) {
        // Retrieve existing poll data
        Map<String, String> pollData = pollStorageService.retrievePollData(uuid);

        // If UUID not found, redirect to schedule-event with warning
        if (pollData == null) {
            return "redirect:/schedule-event?uuidNotFound=true";
        }

        // Add data to model for the template
        model.addAttribute("pollData", pollData);
        model.addAttribute("uuid", uuid);

        return "schedule-event-step2";
    }

    @PostMapping("/schedule-event-step2/{uuid}")
    public String submitScheduleEventStep2(
            @PathVariable String uuid,
            @RequestParam(value = "eventDate", required = false) String eventDate,
            @RequestParam(value = "timeSlot1", required = false) String timeSlot1,
            @RequestParam(value = "timeSlot2", required = false) String timeSlot2,
            @RequestParam(value = "timeSlot3", required = false) String timeSlot3,
            @RequestParam(value = "timeSlot4", required = false) String timeSlot4,
            @RequestParam(value = "action", required = false) String action) {

        // Retrieve existing poll data
        Map<String, String> existingData = pollStorageService.retrievePollData(uuid);

        // Merge existing data with new date/time data
        Map<String, String> updatedData = new HashMap<>();
        if (existingData != null) {
            updatedData.putAll(existingData);
        }

        // Add date/time data
        if (eventDate != null && !eventDate.isEmpty()) {
            updatedData.put("eventDate", eventDate);
        }
        if (timeSlot1 != null && !timeSlot1.isEmpty()) {
            updatedData.put("timeSlot1", timeSlot1);
        }
        if (timeSlot2 != null && !timeSlot2.isEmpty()) {
            updatedData.put("timeSlot2", timeSlot2);
        }
        if (timeSlot3 != null && !timeSlot3.isEmpty()) {
            updatedData.put("timeSlot3", timeSlot3);
        }
        if (timeSlot4 != null && !timeSlot4.isEmpty()) {
            updatedData.put("timeSlot4", timeSlot4);
        }

        // Update existing data with same UUID
        pollStorageService.updatePollData(uuid, updatedData);

        // If back button was clicked, redirect to step 1
        if ("back".equals(action)) {
            return "redirect:/schedule-event/" + uuid;
        }

        // For now, redirect back to step 2 (in future this might go to step 3)
        return "redirect:/schedule-event-step2/" + uuid;
    }
}