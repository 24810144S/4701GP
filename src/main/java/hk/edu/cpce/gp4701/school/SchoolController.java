package hk.edu.cpce.gp4701.school;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import hk.edu.cpce.gp4701.model.Programme;
import hk.edu.cpce.gp4701.model.ProgrammeLevel;

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
        List<ProgrammeLevel> allLevels = programmeLevels.findAll();
        allLevels.sort(Comparator.comparing((ProgrammeLevel pl) -> pl.getProgramme().getId())
                                 .thenComparing(ProgrammeLevel::getLevel));

        // 依課程分組：Map<Programme, List<ProgrammeLevel>>
        Map<Programme, List<ProgrammeLevel>> programmeMap = new LinkedHashMap<>();
        for (ProgrammeLevel pl : allLevels) {
            programmeMap.computeIfAbsent(pl.getProgramme(), k -> new ArrayList<>()).add(pl);
        }

        model.addAttribute("programmeMap", programmeMap);
        model.addAttribute("nowHkt", LocalDateTime.now(ZoneId.of("Asia/Hong_Kong")));
        return "school/admission-requirements";
    }
}
