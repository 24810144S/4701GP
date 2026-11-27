package hk.edu.cpce.gp4701.school.controller;

import hk.edu.cpce.gp4701.school.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/school/overview")
    public String overview(Model model) {
        model.addAttribute("pageTitle", "School Overview");
        model.addAttribute("teachers", schoolService.getTeachers());
        return "school/overview";
    }

    @GetMapping("/school/facilities")
    public String facilities(Model model) {
        model.addAttribute("pageTitle", "School Facilities");
        model.addAttribute("facilities", schoolService.getFacilities());
        return "school/facilities";
    }

    @GetMapping("/school/programmes")
    public String programmes(Model model) {
        model.addAttribute("pageTitle", "Programmes");
        model.addAttribute("programmes", schoolService.getProgrammes());
        return "school/programmes";
    }

    @GetMapping("/school/courses")
    public String courses(Model model) {
        model.addAttribute("pageTitle", "Course Descriptions");
        model.addAttribute("courses", schoolService.getCourseDescriptions());
        return "school/courses";
    }

    @GetMapping("/school/admission")
    public String admission(Model model) {
        model.addAttribute("pageTitle", "Admission Requirements");
        model.addAttribute("admissionInfo", schoolService.getAdmissionRequirements());
        model.addAttribute("seatTable", schoolService.getSeatTable());
        return "school/admission";
    }
}
