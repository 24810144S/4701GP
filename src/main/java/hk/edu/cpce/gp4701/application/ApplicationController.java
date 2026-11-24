package hk.edu.cpce.gp4701.application;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ApplicationController {
    @GetMapping("/applications/new")
    public String newApplication() {
        return "application/new";
    }

    @GetMapping("/applications")
    public String list() {
        return "application/list";
    }

    @GetMapping("/applications/{id}")
    public String detail(@PathVariable Long id) {
        return "application/detail";
    }
}
