/**
 * Java JSON 和 XML 處理示範
 * 展示資料解析、生成和處理的各種技術
 * 注意：本示範使用 Java 內建功能，不依賴外部函式庫
 */

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// XML 處理相關 import
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

// ================================
// 學生資料模型類別
// ================================

/**
 * 學生類別 - 用於 JSON/XML 序列化示範
 */
class Student {
    private String id;
    private String name;
    private int age;
    private String email;
    private List<Course> courses;
    private Address address;
    
    // 建構子
    public Student() {
        this.courses = new ArrayList<>();
    }
    
    public Student(String id, String name, int age, String email) {
        this();
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    // Getter 和 Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public void addCourse(Course course) {
        this.courses.add(course);
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", courses=" + courses.size() +
                ", address=" + address +
                '}';
    }
}

/**
 * 課程類別
 */
class Course {
    private String courseId;
    private String courseName;
    private int credits;
    private double score;
    
    public Course() {}
    
    public Course(String courseId, String courseName, int credits, double score) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.score = score;
    }
    
    // Getter 和 Setter
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", score=" + score +
                '}';
    }
}

/**
 * 地址類別
 */
class Address {
    private String street;
    private String city;
    private String zipCode;
    private String country;
    
    public Address() {}
    
    public Address(String street, String city, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }
    
    // Getter 和 Setter
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}

// ================================
// 簡易 JSON 處理器
// ================================

/**
 * 簡易 JSON 解析器（不依賴外部函式庫）
 */
class SimpleJsonParser {
    
    /**
     * 將學生物件轉換為 JSON 字串
     */
    public static String studentToJson(Student student) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"id\": \"").append(escapeJson(student.getId())).append("\",\n");
        json.append("  \"name\": \"").append(escapeJson(student.getName())).append("\",\n");
        json.append("  \"age\": ").append(student.getAge()).append(",\n");
        json.append("  \"email\": \"").append(escapeJson(student.getEmail())).append("\",\n");
        
        // 地址
        if (student.getAddress() != null) {
            json.append("  \"address\": ").append(addressToJson(student.getAddress())).append(",\n");
        }
        
        // 課程列表
        json.append("  \"courses\": [\n");
        for (int i = 0; i < student.getCourses().size(); i++) {
            json.append("    ").append(courseToJson(student.getCourses().get(i)));
            if (i < student.getCourses().size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ]\n");
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * 將地址物件轉換為 JSON 字串
     */
    private static String addressToJson(Address address) {
        return "{\n" +
                "    \"street\": \"" + escapeJson(address.getStreet()) + "\",\n" +
                "    \"city\": \"" + escapeJson(address.getCity()) + "\",\n" +
                "    \"zipCode\": \"" + escapeJson(address.getZipCode()) + "\",\n" +
                "    \"country\": \"" + escapeJson(address.getCountry()) + "\"\n" +
                "  }";
    }
    
    /**
     * 將課程物件轉換為 JSON 字串
     */
    private static String courseToJson(Course course) {
        return "{\n" +
                "      \"courseId\": \"" + escapeJson(course.getCourseId()) + "\",\n" +
                "      \"courseName\": \"" + escapeJson(course.getCourseName()) + "\",\n" +
                "      \"credits\": " + course.getCredits() + ",\n" +
                "      \"score\": " + course.getScore() + "\n" +
                "    }";
    }
    
    /**
     * 簡易 JSON 解析（從 JSON 字串解析學生資料）
     */
    public static Student parseStudentFromJson(String json) {
        Student student = new Student();
        
        // 解析基本欄位
        student.setId(extractStringValue(json, "id"));
        student.setName(extractStringValue(json, "name"));
        student.setAge(extractIntValue(json, "age"));
        student.setEmail(extractStringValue(json, "email"));
        
        // 解析地址
        String addressJson = extractObjectValue(json, "address");
        if (addressJson != null && !addressJson.trim().isEmpty()) {
            Address address = new Address();
            address.setStreet(extractStringValue(addressJson, "street"));
            address.setCity(extractStringValue(addressJson, "city"));
            address.setZipCode(extractStringValue(addressJson, "zipCode"));
            address.setCountry(extractStringValue(addressJson, "country"));
            student.setAddress(address);
        }
        
        // 解析課程列表
        String coursesJson = extractArrayValue(json, "courses");
        if (coursesJson != null) {
            List<String> courseObjects = splitJsonArray(coursesJson);
            for (String courseJson : courseObjects) {
                Course course = new Course();
                course.setCourseId(extractStringValue(courseJson, "courseId"));
                course.setCourseName(extractStringValue(courseJson, "courseName"));
                course.setCredits(extractIntValue(courseJson, "credits"));
                course.setScore(extractDoubleValue(courseJson, "score"));
                student.addCourse(course);
            }
        }
        
        return student;
    }
    
    // 輔助方法：字串轉義
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    // 輔助方法：提取字串值
    public static String extractStringValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*?)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    // 輔助方法：提取整數值
    private static int extractIntValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }
    
    // 輔助方法：提取雙精度值
    private static double extractDoubleValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }
    
    // 輔助方法：提取物件值
    private static String extractObjectValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\{[^}]*\\})");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    // 輔助方法：提取陣列值
    private static String extractArrayValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\[[^\\]]*\\])");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    // 輔助方法：分割 JSON 陣列
    private static List<String> splitJsonArray(String arrayJson) {
        List<String> objects = new ArrayList<>();
        if (arrayJson == null || arrayJson.trim().length() < 2) {
            return objects;
        }
        
        String content = arrayJson.trim().substring(1, arrayJson.length() - 1); // 移除 [ ]
        if (content.trim().isEmpty()) {
            return objects;
        }
        
        int depth = 0;
        StringBuilder current = new StringBuilder();
        
        for (char c : content.toCharArray()) {
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
            }
            
            if (c == ',' && depth == 0) {
                objects.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            objects.add(current.toString().trim());
        }
        
        return objects;
    }
}

// ================================
// XML 處理器
// ================================

/**
 * XML 處理器類別
 */
class XmlProcessor {
    
    /**
     * 將學生資料轉換為 XML 文檔
     */
    public static Document studentToXml(Student student) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        // 根元素
        Element root = doc.createElement("student");
        root.setAttribute("id", student.getId());
        doc.appendChild(root);
        
        // 基本資訊
        Element name = doc.createElement("name");
        name.setTextContent(student.getName());
        root.appendChild(name);
        
        Element age = doc.createElement("age");
        age.setTextContent(String.valueOf(student.getAge()));
        root.appendChild(age);
        
        Element email = doc.createElement("email");
        email.setTextContent(student.getEmail());
        root.appendChild(email);
        
        // 地址
        if (student.getAddress() != null) {
            Element addressElement = addressToXml(doc, student.getAddress());
            root.appendChild(addressElement);
        }
        
        // 課程列表
        Element courses = doc.createElement("courses");
        for (Course course : student.getCourses()) {
            Element courseElement = courseToXml(doc, course);
            courses.appendChild(courseElement);
        }
        root.appendChild(courses);
        
        return doc;
    }
    
    /**
     * 將地址轉換為 XML 元素
     */
    private static Element addressToXml(Document doc, Address address) {
        Element addressElement = doc.createElement("address");
        
        Element street = doc.createElement("street");
        street.setTextContent(address.getStreet());
        addressElement.appendChild(street);
        
        Element city = doc.createElement("city");
        city.setTextContent(address.getCity());
        addressElement.appendChild(city);
        
        Element zipCode = doc.createElement("zipCode");
        zipCode.setTextContent(address.getZipCode());
        addressElement.appendChild(zipCode);
        
        Element country = doc.createElement("country");
        country.setTextContent(address.getCountry());
        addressElement.appendChild(country);
        
        return addressElement;
    }
    
    /**
     * 將課程轉換為 XML 元素
     */
    private static Element courseToXml(Document doc, Course course) {
        Element courseElement = doc.createElement("course");
        courseElement.setAttribute("id", course.getCourseId());
        
        Element name = doc.createElement("name");
        name.setTextContent(course.getCourseName());
        courseElement.appendChild(name);
        
        Element credits = doc.createElement("credits");
        credits.setTextContent(String.valueOf(course.getCredits()));
        courseElement.appendChild(credits);
        
        Element score = doc.createElement("score");
        score.setTextContent(String.valueOf(course.getScore()));
        courseElement.appendChild(score);
        
        return courseElement;
    }
    
    /**
     * 從 XML 文檔解析學生資料
     */
    public static Student parseStudentFromXml(Document doc) {
        Student student = new Student();
        
        Element root = doc.getDocumentElement();
        student.setId(root.getAttribute("id"));
        
        // 解析基本資訊
        NodeList nameNodes = root.getElementsByTagName("name");
        if (nameNodes.getLength() > 0) {
            student.setName(nameNodes.item(0).getTextContent());
        }
        
        NodeList ageNodes = root.getElementsByTagName("age");
        if (ageNodes.getLength() > 0) {
            student.setAge(Integer.parseInt(ageNodes.item(0).getTextContent()));
        }
        
        NodeList emailNodes = root.getElementsByTagName("email");
        if (emailNodes.getLength() > 0) {
            student.setEmail(emailNodes.item(0).getTextContent());
        }
        
        // 解析地址
        NodeList addressNodes = root.getElementsByTagName("address");
        if (addressNodes.getLength() > 0) {
            Element addressElement = (Element) addressNodes.item(0);
            Address address = parseAddressFromXml(addressElement);
            student.setAddress(address);
        }
        
        // 解析課程
        NodeList courseNodes = root.getElementsByTagName("course");
        for (int i = 0; i < courseNodes.getLength(); i++) {
            Element courseElement = (Element) courseNodes.item(i);
            Course course = parseCourseFromXml(courseElement);
            student.addCourse(course);
        }
        
        return student;
    }
    
    /**
     * 從 XML 元素解析地址
     */
    private static Address parseAddressFromXml(Element addressElement) {
        Address address = new Address();
        
        NodeList streetNodes = addressElement.getElementsByTagName("street");
        if (streetNodes.getLength() > 0) {
            address.setStreet(streetNodes.item(0).getTextContent());
        }
        
        NodeList cityNodes = addressElement.getElementsByTagName("city");
        if (cityNodes.getLength() > 0) {
            address.setCity(cityNodes.item(0).getTextContent());
        }
        
        NodeList zipCodeNodes = addressElement.getElementsByTagName("zipCode");
        if (zipCodeNodes.getLength() > 0) {
            address.setZipCode(zipCodeNodes.item(0).getTextContent());
        }
        
        NodeList countryNodes = addressElement.getElementsByTagName("country");
        if (countryNodes.getLength() > 0) {
            address.setCountry(countryNodes.item(0).getTextContent());
        }
        
        return address;
    }
    
    /**
     * 從 XML 元素解析課程
     */
    private static Course parseCourseFromXml(Element courseElement) {
        Course course = new Course();
        course.setCourseId(courseElement.getAttribute("id"));
        
        NodeList nameNodes = courseElement.getElementsByTagName("name");
        if (nameNodes.getLength() > 0) {
            course.setCourseName(nameNodes.item(0).getTextContent());
        }
        
        NodeList creditNodes = courseElement.getElementsByTagName("credits");
        if (creditNodes.getLength() > 0) {
            course.setCredits(Integer.parseInt(creditNodes.item(0).getTextContent()));
        }
        
        NodeList scoreNodes = courseElement.getElementsByTagName("score");
        if (scoreNodes.getLength() > 0) {
            course.setScore(Double.parseDouble(scoreNodes.item(0).getTextContent()));
        }
        
        return course;
    }
    
    /**
     * 將 XML 文檔轉換為字串
     */
    public static String xmlToString(Document doc) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
    
    /**
     * 從字串載入 XML 文檔
     */
    public static Document loadXmlFromString(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new java.io.ByteArrayInputStream(xmlString.getBytes()));
    }
}

// ================================
// 設定檔管理器
// ================================

/**
 * 應用程式設定管理器
 */
class ConfigManager {
    private Map<String, String> properties;
    
    public ConfigManager() {
        this.properties = new HashMap<>();
        initializeDefaults();
    }
    
    private void initializeDefaults() {
        properties.put("app.name", "學生管理系統");
        properties.put("app.version", "1.0.0");
        properties.put("database.url", "jdbc:mysql://localhost:3306/school");
        properties.put("database.username", "admin");
        properties.put("max.students", "1000");
        properties.put("debug.enabled", "false");
    }
    
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    /**
     * 儲存設定為 JSON 格式
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        int count = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            json.append("  \"").append(entry.getKey()).append("\": \"")
                .append(entry.getValue()).append("\"");
            if (++count < properties.size()) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * 從 JSON 載入設定
     */
    public void fromJson(String json) {
        properties.clear();
        
        // 簡易 JSON 解析
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        
        while (matcher.find()) {
            properties.put(matcher.group(1), matcher.group(2));
        }
    }
    
    /**
     * 儲存設定為 XML 格式
     */
    public String toXml() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<configuration>\n");
        
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            xml.append("  <property name=\"").append(entry.getKey()).append("\" value=\"")
               .append(entry.getValue()).append("\" />\n");
        }
        
        xml.append("</configuration>");
        return xml.toString();
    }
}

// ================================
// API 回應處理器
// ================================

/**
 * REST API 回應處理器
 */
class ApiResponseHandler {
    
    /**
     * 模擬 API 回應類別
     */
    static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private String timestamp;
        
        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        
        // Getter 方法
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
        public String getTimestamp() { return timestamp; }
    }
    
    /**
     * 建立成功回應的 JSON
     */
    public static String createSuccessResponse(String message, Object data) {
        return "{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"" + message + "\",\n" +
                "  \"timestamp\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\",\n" +
                "  \"data\": " + (data != null ? data.toString() : "null") + "\n" +
                "}";
    }
    
    /**
     * 建立錯誤回應的 JSON
     */
    public static String createErrorResponse(String message, String errorCode) {
        return "{\n" +
                "  \"success\": false,\n" +
                "  \"message\": \"" + message + "\",\n" +
                "  \"errorCode\": \"" + errorCode + "\",\n" +
                "  \"timestamp\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"\n" +
                "}";
    }
    
    /**
     * 解析 API 回應
     */
    public static void parseApiResponse(String jsonResponse) {
        System.out.println("解析 API 回應:");
        
        boolean success = jsonResponse.contains("\"success\": true");
        String message = SimpleJsonParser.extractStringValue(jsonResponse, "message");
        String timestamp = SimpleJsonParser.extractStringValue(jsonResponse, "timestamp");
        
        System.out.println("  成功: " + success);
        System.out.println("  訊息: " + message);
        System.out.println("  時間戳記: " + timestamp);
        
        if (!success) {
            String errorCode = SimpleJsonParser.extractStringValue(jsonResponse, "errorCode");
            System.out.println("  錯誤代碼: " + errorCode);
        }
    }
}

// ================================
// 主要示範類別
// ================================

public class JsonXmlProcessingDemo {
    
    /**
     * 建立示範學生資料
     */
    private static Student createSampleStudent() {
        Student student = new Student("S001", "張小明", 20, "ming@example.com");
        
        // 設定地址
        Address address = new Address("中山路123號", "台北市", "10048", "台灣");
        student.setAddress(address);
        
        // 新增課程
        student.addCourse(new Course("CS101", "計算機概論", 3, 85.5));
        student.addCourse(new Course("MATH201", "微積分", 4, 92.0));
        student.addCourse(new Course("ENG101", "英文", 2, 78.5));
        
        return student;
    }
    
    /**
     * 示範 JSON 處理
     */
    private static void demonstrateJsonProcessing() {
        System.out.println("=== JSON 處理示範 ===");
        
        // 建立學生資料
        Student originalStudent = createSampleStudent();
        System.out.println("原始學生資料: " + originalStudent);
        
        // 轉換為 JSON
        String json = SimpleJsonParser.studentToJson(originalStudent);
        System.out.println("\n轉換為 JSON:");
        System.out.println(json);
        
        // 從 JSON 解析
        Student parsedStudent = SimpleJsonParser.parseStudentFromJson(json);
        System.out.println("\n從 JSON 解析的學生資料: " + parsedStudent);
        
        // 驗證資料正確性
        boolean isCorrect = originalStudent.getName().equals(parsedStudent.getName()) &&
                           originalStudent.getAge() == parsedStudent.getAge() &&
                           originalStudent.getCourses().size() == parsedStudent.getCourses().size();
        
        System.out.println("資料解析正確性: " + (isCorrect ? "正確" : "錯誤"));
        
        // 儲存到檔案
        try {
            Files.write(Paths.get("student.json"), json.getBytes());
            System.out.println("JSON 已儲存到 student.json");
        } catch (IOException e) {
            System.err.println("儲存 JSON 檔案失敗: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * 示範 XML 處理
     */
    private static void demonstrateXmlProcessing() {
        System.out.println("=== XML 處理示範 ===");
        
        try {
            // 建立學生資料
            Student originalStudent = createSampleStudent();
            System.out.println("原始學生資料: " + originalStudent);
            
            // 轉換為 XML
            Document xmlDoc = XmlProcessor.studentToXml(originalStudent);
            String xmlString = XmlProcessor.xmlToString(xmlDoc);
            System.out.println("\n轉換為 XML:");
            System.out.println(xmlString);
            
            // 從 XML 解析
            Document parsedDoc = XmlProcessor.loadXmlFromString(xmlString);
            Student parsedStudent = XmlProcessor.parseStudentFromXml(parsedDoc);
            System.out.println("從 XML 解析的學生資料: " + parsedStudent);
            
            // 驗證資料正確性
            boolean isCorrect = originalStudent.getName().equals(parsedStudent.getName()) &&
                               originalStudent.getAge() == parsedStudent.getAge() &&
                               originalStudent.getCourses().size() == parsedStudent.getCourses().size();
            
            System.out.println("資料解析正確性: " + (isCorrect ? "正確" : "錯誤"));
            
            // 儲存到檔案
            Files.write(Paths.get("student.xml"), xmlString.getBytes());
            System.out.println("XML 已儲存到 student.xml");
            
        } catch (Exception e) {
            System.err.println("XML 處理錯誤: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * 示範設定檔處理
     */
    private static void demonstrateConfigManagement() {
        System.out.println("=== 設定檔管理示範 ===");
        
        ConfigManager config = new ConfigManager();
        
        // 顯示預設設定
        System.out.println("預設設定:");
        System.out.println("  應用程式名稱: " + config.getProperty("app.name"));
        System.out.println("  版本: " + config.getProperty("app.version"));
        System.out.println("  最大學生數: " + config.getProperty("max.students"));
        
        // 修改設定
        config.setProperty("max.students", "2000");
        config.setProperty("app.theme", "dark");
        
        // 轉換為 JSON 格式
        String jsonConfig = config.toJson();
        System.out.println("\nJSON 格式設定:");
        System.out.println(jsonConfig);
        
        // 轉換為 XML 格式
        String xmlConfig = config.toXml();
        System.out.println("\nXML 格式設定:");
        System.out.println(xmlConfig);
        
        // 儲存設定檔
        try {
            Files.write(Paths.get("config.json"), jsonConfig.getBytes());
            Files.write(Paths.get("config.xml"), xmlConfig.getBytes());
            System.out.println("\n設定檔已儲存");
        } catch (IOException e) {
            System.err.println("儲存設定檔失敗: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * 示範 API 回應處理
     */
    private static void demonstrateApiResponseHandling() {
        System.out.println("=== API 回應處理示範 ===");
        
        // 模擬成功的 API 回應
        String successResponse = ApiResponseHandler.createSuccessResponse(
            "學生資料更新成功", 
            "{\"studentId\": \"S001\", \"updated\": true}"
        );
        
        System.out.println("成功回應 JSON:");
        System.out.println(successResponse);
        System.out.println();
        
        ApiResponseHandler.parseApiResponse(successResponse);
        System.out.println();
        
        // 模擬錯誤的 API 回應
        String errorResponse = ApiResponseHandler.createErrorResponse(
            "學生 ID 不存在", 
            "STUDENT_NOT_FOUND"
        );
        
        System.out.println("錯誤回應 JSON:");
        System.out.println(errorResponse);
        System.out.println();
        
        ApiResponseHandler.parseApiResponse(errorResponse);
        System.out.println();
    }
    
    /**
     * 示範批次資料處理
     */
    private static void demonstrateBatchProcessing() {
        System.out.println("=== 批次資料處理示範 ===");
        
        // 建立多個學生資料
        List<Student> students = Arrays.asList(
            new Student("S001", "張小明", 20, "ming@example.com"),
            new Student("S002", "李小華", 21, "hua@example.com"),
            new Student("S003", "王小美", 19, "mei@example.com")
        );
        
        // 為學生新增課程
        for (Student student : students) {
            student.addCourse(new Course("CS101", "計算機概論", 3, 80 + Math.random() * 20));
            student.addCourse(new Course("MATH201", "微積分", 4, 75 + Math.random() * 25));
        }
        
        // 批次轉換為 JSON
        System.out.println("批次 JSON 轉換:");
        StringBuilder batchJson = new StringBuilder();
        batchJson.append("{\n  \"students\": [\n");
        
        for (int i = 0; i < students.size(); i++) {
            String studentJson = SimpleJsonParser.studentToJson(students.get(i));
            // 縮排調整
            studentJson = studentJson.replaceAll("(?m)^", "    ");
            batchJson.append(studentJson);
            if (i < students.size() - 1) {
                batchJson.append(",");
            }
            batchJson.append("\n");
        }
        
        batchJson.append("  ],\n");
        batchJson.append("  \"total\": ").append(students.size()).append(",\n");
        batchJson.append("  \"timestamp\": \"").append(
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ).append("\"\n");
        batchJson.append("}");
        
        System.out.println(batchJson.toString());
        
        // 儲存批次資料
        try {
            Files.write(Paths.get("students_batch.json"), batchJson.toString().getBytes());
            System.out.println("\n批次資料已儲存到 students_batch.json");
        } catch (IOException e) {
            System.err.println("儲存批次資料失敗: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    public static void main(String[] args) {
        System.out.println("=== Java JSON 和 XML 處理示範 ===\n");
        
        demonstrateJsonProcessing();
        demonstrateXmlProcessing();
        demonstrateConfigManagement();
        demonstrateApiResponseHandling();
        demonstrateBatchProcessing();
        
        System.out.println("=== JSON/XML 處理示範完成 ===");
        System.out.println("\n注意：此示範使用 Java 內建功能，實際專案建議使用成熟的函式庫如：");
        System.out.println("- JSON: Jackson, Gson, JSON-B");
        System.out.println("- XML: JAXB, Jackson XML, Dom4j");
    }
}