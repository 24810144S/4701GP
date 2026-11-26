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
        teachers.add(createPerson("Dr. Emily Chan", "校長", "教育管理博士"));
        teachers.add(createPerson("Mr. David Wong", "副校長", "國際課程專家"));
        teachers.add(createPerson("Ms. Sarah Lee", "英語科主任", "TESOL 碩士"));
        teachers.add(createPerson("Dr. Michael Cheung", "科學科主任", "化學博士"));
        return teachers;
    }

    public List<Map<String, String>> getFacilities() {
        List<Map<String, String>> facilities = new ArrayList<>();
        facilities.add(createFacility("科學實驗室", "配備現代化實驗設備，支援物理、化學、生物實驗"));
        facilities.add(createFacility("電腦室", "高速網絡與最新軟件，支援 STEM 教育"));
        facilities.add(createFacility("圖書館", "藏書豐富，提供安靜自習空間"));
        facilities.add(createFacility("體育館", "多功能室內運動場，支援各項體育活動"));
        facilities.add(createFacility("藝術工作室", "繪畫、音樂與戲劇創作空間"));
        return facilities;
    }

    public List<Map<String, String>> getProgrammes() {
        List<Map<String, String>> programmes = new ArrayList<>();
        programmes.add(createProgramme("DSE 課程", "香港中學文憑考試課程，適合本地升學路徑"));
        programmes.add(createProgramme("IB 課程", "國際文憑課程，強調全人教育與國際視野"));
        return programmes;
    }

    public List<Map<String, String>> getCourseDescriptions() {
        List<Map<String, String>> courses = new ArrayList<>();
        courses.add(createCourse("中一至中三", "基礎核心科目：中文、英文、數學、科學、人文"));
        courses.add(createCourse("中四至中六 (DSE)", "選修科目包括物理、化學、生物、經濟、歷史等"));
        courses.add(createCourse("中四至中六 (IB)", "IB Diploma Programme，包含 TOK、EE、CAS"));
        return courses;
    }

    public List<Map<String, Object>> getAdmissionRequirements() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createAdmission("DSE 課程", "2026-12-31", "良好操行、面試、學業成績"));
        list.add(createAdmission("IB 課程", "2026-12-15", "英語能力、面試、推薦信"));
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
