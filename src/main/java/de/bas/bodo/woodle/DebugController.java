package de.bas.bodo.woodle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @Autowired
    private Environment environment;

    @GetMapping("/debug-props")
    public String debugProperties() {
        String pathPrefix = environment.getProperty("spring.cloud.function.web.path-prefix");

        return "spring.cloud.function.web.path-prefix: " + pathPrefix;
    }
}