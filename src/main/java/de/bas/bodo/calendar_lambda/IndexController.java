package de.bas.bodo.calendar_lambda;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String index() {
        return "<!DOCTYPE html><html><head><title>Calendar Lambda</title></head><body><h1>Hello World</h1></body></html>";
    }
}