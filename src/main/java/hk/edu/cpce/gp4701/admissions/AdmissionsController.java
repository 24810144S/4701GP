package hk.edu.cpce.gp4701.admissions;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdmissionsController {
    @GetMapping("/admin/admissions")
    public String list() {
        return "admissions/list";
    }
}
