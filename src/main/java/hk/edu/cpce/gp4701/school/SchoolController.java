package hk.edu.cpce.gp4701.school;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SchoolController {
    private final ProgrammeLevelRepository programmeLevels;

    public SchoolController(ProgrammeLevelRepository programmeLevels) {
        this.programmeLevels = programmeLevels;
    }

    @GetMapping("/school/about")
    public String about() {
        return "school/about";
    }

    @GetMapping("/school/facilities")
    public String facilities() {
        return "school/facilities";
    }

    @GetMapping("/school/programmes")
    public String programmes() {
        return "school/programmes";
    }

    @GetMapping("/school/courses")
    public String courses() {
        return "school/courses";
    }

    @GetMapping("/school/admission-requirements")
    public String admissionRequirements(Model model) {
        programmeLevels.findFirstByOrderByIdAsc()
                .ifPresent(level -> model.addAttribute("sampleLevel", level));
        return "school/admission-requirements";
    }
}
