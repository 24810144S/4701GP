package hk.edu.cpce.gp4701.school.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchoolService {

    public List<Map<String, String>> getTeachers() {
        List<Map<String, String>> teachers = new ArrayList<>();
        teachers.add(createPerson("Dr. Emily Chan", "Principal", "Doctor of Education Management"));
        teachers.add(createPerson("Mr. David Wong", "Vice Principal", "International Curriculum Expert"));
        teachers.add(createPerson("Ms. Sarah Lee", "Head of English", "Master of TESOL"));
        teachers.add(createPerson("Dr. Michael Cheung", "Head of Science", "Doctor of Chemistry"));
        return teachers;
    }

    public List<Map<String, String>> getFacilities() {
        List<Map<String, String>> facilities = new ArrayList<>();
        facilities.add(createFacility("Science Laboratories", "Equipped with modern experimental equipment supporting Physics, Chemistry and Biology experiments"));
        facilities.add(createFacility("Computer Rooms", "High-speed network and latest software supporting STEM education"));
        facilities.add(createFacility("Library", "Extensive collection of books with quiet study spaces"));
        facilities.add(createFacility("Gymnasium", "Multi-purpose indoor sports hall supporting various physical activities"));
        facilities.add(createFacility("Art Studio", "Space for painting, music and drama creation"));
        return facilities;
    }

    public List<Map<String, String>> getProgrammes() {
        List<Map<String, String>> programmes = new ArrayList<>();
        programmes.add(createProgramme("DSE Programme", "Hong Kong Diploma of Secondary Education curriculum, suitable for local further study pathways"));
        programmes.add(createProgramme("IB Programme", "International Baccalaureate Diploma Programme, emphasising holistic education and global perspective"));
        return programmes;
    }

    public List<Map<String, String>> getCourseDescriptions() {
        List<Map<String, String>> courses = new ArrayList<>();
        courses.add(createCourse("S1 to S3", "Core foundation subjects: Chinese, English, Mathematics, Science, Humanities"));
        courses.add(createCourse("S4 to S6 (DSE)", "Elective subjects including Physics, Chemistry, Biology, Economics, History, etc."));
        courses.add(createCourse("S4 to S6 (IB)", "IB Diploma Programme including TOK, Extended Essay and CAS"));
        return courses;
    }

    public List<Map<String, Object>> getAdmissionRequirements() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createAdmission("DSE Programme", "2026-12-31", "Good conduct, interview, academic results"));
        list.add(createAdmission("IB Programme", "2026-12-15", "English proficiency, interview, recommendation letter"));
        return list;
    }

    public List<Map<String, Object>> getSeatTable() {
        List<Map<String, Object>> table = new ArrayList<>();
        String[] levels = {"S1", "S2", "S3", "S4", "S5", "S6"};
        int[] seats = {120, 120, 120, 100, 100, 90};
        for (int i = 0; i < levels.length; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("level", levels[i]);
            row.put("dseSeats", seats[i]);
            row.put("ibSeats", seats[i] - 20);
            table.add(row);
        }
        return table;
    }

    private Map<String, String> createPerson(String name, String title, String qualification) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("title", title);
        map.put("qualification", qualification);
        return map;
    }

    private Map<String, String> createFacility(String name, String description) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        return map;
    }

    private Map<String, String> createProgramme(String name, String description) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        return map;
    }

    private Map<String, String> createCourse(String level, String content) {
        Map<String, String> map = new HashMap<>();
        map.put("level", level);
        map.put("content", content);
        return map;
    }

    private Map<String, Object> createAdmission(String programme, String deadline, String requirements) {
        Map<String, Object> map = new HashMap<>();
        map.put("programme", programme);
        map.put("deadline", deadline);
        map.put("requirements", requirements);
        return map;
    }
}
