package de.bas.bodo.woodle;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import java.time.LocalDate;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
@Tag("e2e")
public class E2ECreateEventTest {

    @LocalServerPort
    int port;

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("target/e2e-videos")));
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
    }

    @AfterAll
    static void teardown() {
        if (context != null) {
            try {
                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get("target/trace.zip")));
            } catch (RuntimeException ignored) {
            }
            context.close();
        }
        if (browser != null)
            browser.close();
        if (playwright != null)
            playwright.close();
    }

    @Test
    void happy_path_creates_event_and_displays_summary() {
        String baseUrl = "http://localhost:" + port;
        Page page = context.newPage();

        page.navigate(baseUrl + "/schedule-event");
        assertThat(page.title()).contains("Schedule Event");

        page.locator("[data-test-your-name-field]").fill("Alice Agent");
        page.locator("[data-test-email-field]").fill("alice@example.com");
        page.locator("[data-test-activity-title-field]").fill("Planning Session");
        page.locator("[data-test-description-field]").fill("Discuss Q3 milestones");
        page.locator("[data-test-next-button]").click();
        page.waitForURL("**/schedule-event-step2/**");

        // Step 2: fill required date
        String eventDate = LocalDate.now().plusDays(1).toString();
        page.locator("[data-test=\"date-field\"]").fill(eventDate);
        page.locator("[data-test-next-button]").click();
        page.waitForURL("**/schedule-event-step3/**");
        // Persist snapshot for diagnostics
        try {
            String step3Html = page.content();
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("target"));
            java.nio.file.Files.writeString(java.nio.file.Paths.get("target/e2e-step3.html"), step3Html);
            page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                    .setPath(java.nio.file.Paths.get("target/e2e-step3.png"))
                    .setFullPage(true));
        } catch (Exception ignored) {
        }
        // Step 3: create poll (expiry date may be pre-filled)
        try {
            page.locator("[data-test-create-poll-button]").waitFor();
        } catch (RuntimeException ex) {
            System.out.println("DEBUG step3 URL: " + page.url());
            String html = page.content();
            System.out.println("DEBUG step3 HTML snippet:\n" + html.substring(0, Math.min(1200, html.length())));
            throw ex;
        }
        page.locator("[data-test-create-poll-button]").click();
        page.waitForURL("**/event/**");

        // Arrived at /event/{uuid}
        assertThat(page.url()).contains("/event/");

        String content = page.content();
        assertThat(content).contains("Alice Agent");
        assertThat(content).contains("alice@example.com");
        assertThat(content).contains("Planning Session");
        assertThat(content).contains("Discuss Q3 milestones");

        page.close();
    }
}
