/**
 * 基礎物件導向概念示範
 * 展示封裝、建構子、方法設計等基本概念
 */
public class StudentDemo {
    // 私有屬性 - 封裝
    private String name;
    private int score;
    private String studentId;
    private String major;
    
    // 預設建構子
    public StudentDemo() {
        this("未知學生", 0, "000000", "未定科系");
    }
    
    // 帶參數建構子
    public StudentDemo(String name, int score) {
        this(name, score, "000000", "未定科系");
    }
    
    // 完整建構子
    public StudentDemo(String name, int score, String studentId, String major) {
        this.name = name;
        setScore(score);  // 使用setter進行驗證
        this.studentId = studentId;
        this.major = major;
    }
    
    // Getter方法
    public String getName() {
        return name;
    }
    
    public int getScore() {
        return score;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public String getMajor() {
        return major;
    }
    
    // Setter方法（包含驗證）
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        } else {
            System.out.println("姓名不能為空");
        }
    }
    
    public void setScore(int score) {
        if (score >= 0 && score <= 100) {
            this.score = score;
        } else {
            System.out.println("分數必須在0-100之間，輸入的分數: " + score);
            this.score = 0;  // 設定為預設值
        }
    }
    
    public void setStudentId(String studentId) {
        if (studentId != null && studentId.length() == 6) {
            this.studentId = studentId;
        } else {
            System.out.println("學號必須是6位數字");
        }
    }
    
    public void setMajor(String major) {
        if (major != null && !major.trim().isEmpty()) {
            this.major = major;
        }
    }
    
    // 業務方法
    public String getGrade() {
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }
    
    public boolean isPass() {
        return score >= 60;
    }
    
    public void study(int hours) {
        System.out.println(name + " 讀書了 " + hours + " 小時");
        // 簡單的分數提升邏輯
        int improvement = Math.min(hours / 2, 10);  // 最多提升10分
        setScore(Math.min(score + improvement, 100));
    }
    
    public void displayInfo() {
        System.out.println("=== 學生資訊 ===");
        System.out.println("學號: " + studentId);
        System.out.println("姓名: " + name);
        System.out.println("科系: " + major);
        System.out.println("分數: " + score + " 分");
        System.out.println("等第: " + getGrade());
        System.out.println("是否及格: " + (isPass() ? "是" : "否"));
        System.out.println("================");
    }
    
    // 覆寫toString方法
    @Override
    public String toString() {
        return "StudentDemo{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", studentId='" + studentId + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
    
    // 測試主方法
    public static void main(String[] args) {
        System.out.println("=== 物件導向基礎概念示範 ===\n");
        
        // 使用不同建構子創建學生物件
        StudentDemo student1 = new StudentDemo();
        student1.setName("張三");
        student1.setScore(85);
        student1.setStudentId("123456");
        student1.setMajor("資訊工程");
        
        StudentDemo student2 = new StudentDemo("李四", 92);
        student2.setStudentId("234567");
        student2.setMajor("電機工程");
        
        StudentDemo student3 = new StudentDemo("王五", 78, "345678", "機械工程");
        
        // 顯示學生資訊
        student1.displayInfo();
        student2.displayInfo();
        student3.displayInfo();
        
        // 測試業務邏輯
        System.out.println("=== 測試學習功能 ===");
        System.out.println("王五學習前分數: " + student3.getScore());
        student3.study(8);  // 學習8小時
        System.out.println("王五學習後分數: " + student3.getScore());
        
        // 測試驗證邏輯
        System.out.println("\n=== 測試驗證邏輯 ===");
        StudentDemo student4 = new StudentDemo();
        student4.setScore(150);  // 無效分數
        student4.setScore(75);   // 有效分數
        student4.setName("");    // 無效姓名
        student4.setName("趙六"); // 有效姓名
        student4.setStudentId("12345");  // 無效學號（太短）
        student4.setStudentId("456789"); // 有效學號
        
        student4.displayInfo();
        
        // 測試toString方法
        System.out.println("\n=== toString方法測試 ===");
        System.out.println(student1.toString());
    }
}