package de.bas.bodo.woodle;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.bas.bodo.woodle.service.PollStorageService;

@Controller
public class WoodleFormsController {

    private final PollStorageService pollStorageService;

    @Value("${aws.s3.endpoint}")
    private String s3Endpoint;

    @Value("${aws.s3.region}")
    private String s3Region;

    @Value("${aws.s3.access-key}")
    private String s3AccessKey;

    @Value("${aws.s3.secret-key}")
    private String s3SecretKey;

    @Value("${aws.s3.bucket-name}")
    private String s3BucketName;

    @Value("${aws.s3.force-path-style}")
    private String s3ForcePathStyle;

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

    @GetMapping("/debug-env")
    @ResponseBody
    public String debugEnvironment() {
        StringBuilder debug = new StringBuilder();
        debug.append("Environment Variables Debug:\n");
        debug.append("AWS_S3_ENDPOINT: ").append(System.getenv("AWS_S3_ENDPOINT")).append("\n");
        debug.append("AWS_S3_REGION: ").append(System.getenv("AWS_S3_REGION")).append("\n");
        debug.append("AWS_S3_ACCESS_KEY: ").append(System.getenv("AWS_S3_ACCESS_KEY")).append("\n");
        debug.append("AWS_S3_SECRET_KEY: ").append(System.getenv("AWS_S3_SECRET_KEY")).append("\n");
        debug.append("AWS_S3_BUCKET_NAME: ").append(System.getenv("AWS_S3_BUCKET_NAME")).append("\n");
        debug.append("AWS_S3_FORCE_PATH_STYLE: ").append(System.getenv("AWS_S3_FORCE_PATH_STYLE")).append("\n");
        debug.append("\n");
        debug.append("Spring Boot Properties (@Value) Debug:\n");
        debug.append("aws.s3.endpoint: ").append(s3Endpoint).append("\n");
        debug.append("aws.s3.region: ").append(s3Region).append("\n");
        debug.append("aws.s3.access-key: ").append(s3AccessKey).append("\n");
        debug.append("aws.s3.secret-key: ").append(s3SecretKey).append("\n");
        debug.append("aws.s3.bucket-name: ").append(s3BucketName).append("\n");
        debug.append("aws.s3.force-path-style: ").append(s3ForcePathStyle).append("\n");
        return debug.toString();
    }

    @GetMapping("/schedule-event")
    public String scheduleEvent(
            @RequestParam(value = "uuidNotFound", required = false) String uuidNotFound,
            @RequestParam(value = "uuidMissing", required = false) String uuidMissing,
            Model model) {
        if ("true".equals(uuidNotFound)) {
            model.addAttribute("warningMessage", "UUID not found / Poll not found");
        }
        if ("true".equals(uuidMissing)) {
            model.addAttribute("warningMessage", "Poll id missing");
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
        
        // Add proposal count for dynamic field rendering
        String proposalCountStr = pollData.get("proposalCount");
        int proposalCount = (proposalCountStr != null) ? Integer.parseInt(proposalCountStr) : 1;
        model.addAttribute("proposalCount", proposalCount);

        return "schedule-event-step2";
    }

    @PostMapping("/schedule-event-step2/{uuid}")
    public String submitScheduleEventStep2(
            @PathVariable String uuid,
            @RequestParam Map<String, String> allParams) {
        
        String action = allParams.get("action");

        // Retrieve existing poll data
        Map<String, String> existingData = pollStorageService.retrievePollData(uuid);

        // Merge existing data with new date/time data
        Map<String, String> updatedData = new HashMap<>();
        if (existingData != null) {
            updatedData.putAll(existingData);
        }

        // Add all form data (excluding action parameter)
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Skip the action parameter and only store non-empty values
            if (!"action".equals(key) && value != null && !value.isEmpty()) {
                updatedData.put(key, value);
            }
        }

        // Handle special actions before saving general form data
        if ("add-proposal".equals(action)) {
            // Increment proposal count to show additional fields
            String currentCountStr = updatedData.get("proposalCount");
            int currentCount = (currentCountStr != null) ? Integer.parseInt(currentCountStr) : 1;
            updatedData.put("proposalCount", String.valueOf(currentCount + 1));
            pollStorageService.updatePollData(uuid, updatedData);
            return "redirect:/schedule-event-step2/" + uuid;
        }

        // Update existing data with same UUID (for normal form submissions)
        pollStorageService.updatePollData(uuid, updatedData);

        // Navigation handling
        if ("back".equals(action)) {
            return "redirect:/schedule-event/" + uuid; // back to step 1
        }
        if ("next".equals(action)) {
            return "redirect:/schedule-event-step3/" + uuid; // forward to step 3
        }

        // default stay on step 2
        return "redirect:/schedule-event-step2/" + uuid;
    }

    /* ---------------------------- STEP 3 ---------------------------------- */

    @GetMapping("/schedule-event-step3")
    public String scheduleEventStep3WithoutUuid() {
        // Missing UUID
        return "redirect:/schedule-event?uuidMissing=true";
    }

    @GetMapping("/schedule-event-step3/{uuid}")
    public String scheduleEventStep3(@PathVariable String uuid, Model model) {
        Map<String, String> pollData = pollStorageService.retrievePollData(uuid);

        if (pollData == null) {
            // Unknown UUID
            return "redirect:/schedule-event?uuidNotFound=true";
        }

        // Default expiryDate if not already set but eventDate present
        if (!pollData.containsKey("expiryDate") && pollData.get("eventDate") != null) {
            try {
                java.time.LocalDate start = java.time.LocalDate.parse(pollData.get("eventDate"));
                java.time.LocalDate expiry = start.plusMonths(3);
                pollData.put("expiryDate", expiry.toString());
            } catch (java.time.format.DateTimeParseException ignored) {
                // ignore invalid date format
            }
        }

        model.addAttribute("pollData", pollData);
        model.addAttribute("uuid", uuid);

        return "schedule-event-step3";
    }

    @PostMapping("/schedule-event-step3/{uuid}")
    public String submitScheduleEventStep3(
            @PathVariable String uuid,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam(value = "action", required = false) String action) {

        if ("back".equals(action)) {
            return "redirect:/schedule-event-step2/" + uuid;
        }

        // Update data with expiry date
        Map<String, String> existingData = pollStorageService.retrievePollData(uuid);
        Map<String, String> updatedData = new java.util.HashMap<>();
        if (existingData != null) {
            updatedData.putAll(existingData);
        }
        if (expiryDate != null && !expiryDate.isEmpty()) {
            updatedData.put("expiryDate", expiryDate);
        }
        pollStorageService.updatePollData(uuid, updatedData);

        // Handle create-poll action - redirect to event summary
        if ("create-poll".equals(action)) {
            return "redirect:/event/" + uuid;
        }

        // Default: stay on step 3
        return "redirect:/schedule-event-step3/" + uuid;
    }

    @GetMapping("/event/{uuid}")
    public String eventSummary(@PathVariable String uuid, Model model) {
        // Retrieve poll data from storage
        Map<String, String> pollData = pollStorageService.retrievePollData(uuid);
        
        // If UUID not found, return 404 (will be handled by Spring)
        if (pollData == null) {
            return "redirect:/schedule-event?uuidNotFound=true";
        }
        
        // Add data to model for template
        model.addAttribute("pollData", pollData);
        model.addAttribute("uuid", uuid);
        
        return "event-summary";
    }
}