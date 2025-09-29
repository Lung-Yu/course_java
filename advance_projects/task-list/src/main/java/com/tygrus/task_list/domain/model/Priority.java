package com.tygrus.task_list.domain.model;

/**
 * 任務優先級枚舉
 */
public enum Priority {
    LOW(1, "低"),
    MEDIUM(2, "中"),
    HIGH(3, "高"),
    URGENT(4, "緊急");
    
    private final int level;
    private final String description;
    
    Priority(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
    
    // 為了向後兼容性添加 getDisplayName 方法
    public String getDisplayName() {
        return description;
    }
}