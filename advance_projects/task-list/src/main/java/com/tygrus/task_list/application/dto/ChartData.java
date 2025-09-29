package com.tygrus.task_list.application.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 圖表資料 DTO
 * 
 * 支援多種圖表類型的資料格式，包含餅圖、柱狀圖、折線圖等
 */
public class ChartData {
    
    /**
     * 圖表類型枚舉
     */
    public enum ChartType {
        PIE("pie", "餅圖"),
        BAR("bar", "柱狀圖"),
        LINE("line", "折線圖"),
        AREA("area", "面積圖"),
        DONUT("donut", "甜甜圈圖"),
        SCATTER("scatter", "散點圖");
        
        private final String code;
        private final String displayName;
        
        ChartType(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }
        
        public String getCode() { return code; }
        public String getDisplayName() { return displayName; }
    }
    
    private final String id;
    private final String title;
    private final String subtitle;
    private final ChartType type;
    private final List<DataPoint> dataPoints;
    private final Map<String, Object> config;
    private final String xAxisLabel;
    private final String yAxisLabel;
    
    private ChartData(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.subtitle = builder.subtitle;
        this.type = builder.type;
        this.dataPoints = List.copyOf(builder.dataPoints);
        this.config = Map.copyOf(builder.config);
        this.xAxisLabel = builder.xAxisLabel;
        this.yAxisLabel = builder.yAxisLabel;
    }
    
    /**
     * 資料點內部類別
     */
    public static class DataPoint {
        private final String label;
        private final double value;
        private final String category;
        private final Map<String, Object> metadata;
        
        public DataPoint(String label, double value) {
            this(label, value, null, Map.of());
        }
        
        public DataPoint(String label, double value, String category) {
            this(label, value, category, Map.of());
        }
        
        public DataPoint(String label, double value, String category, Map<String, Object> metadata) {
            this.label = label;
            this.value = value;
            this.category = category;
            this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
        }
        
        public String getLabel() { return label; }
        public double getValue() { return value; }
        public String getCategory() { return category; }
        public Map<String, Object> getMetadata() { return metadata; }
        
        @Override
        public String toString() {
            return String.format("DataPoint{label='%s', value=%.2f, category='%s'}", 
                label, value, category);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String title;
        private String subtitle;
        private ChartType type;
        private List<DataPoint> dataPoints = List.of();
        private Map<String, Object> config = Map.of();
        private String xAxisLabel;
        private String yAxisLabel;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }
        
        public Builder type(ChartType type) {
            this.type = type;
            return this;
        }
        
        public Builder dataPoints(List<DataPoint> dataPoints) {
            this.dataPoints = dataPoints;
            return this;
        }
        
        public Builder config(Map<String, Object> config) {
            this.config = config;
            return this;
        }
        
        public Builder xAxisLabel(String xAxisLabel) {
            this.xAxisLabel = xAxisLabel;
            return this;
        }
        
        public Builder yAxisLabel(String yAxisLabel) {
            this.yAxisLabel = yAxisLabel;
            return this;
        }
        
        public ChartData build() {
            Objects.requireNonNull(id, "Chart ID cannot be null");
            Objects.requireNonNull(title, "Chart title cannot be null");
            Objects.requireNonNull(type, "Chart type cannot be null");
            
            return new ChartData(this);
        }
    }
    
    /**
     * 為餅圖創建便利方法
     */
    public static ChartData createPieChart(String id, String title, List<DataPoint> dataPoints) {
        return builder()
            .id(id)
            .title(title)
            .type(ChartType.PIE)
            .dataPoints(dataPoints)
            .build();
    }
    
    /**
     * 為柱狀圖創建便利方法
     */
    public static ChartData createBarChart(String id, String title, List<DataPoint> dataPoints, 
                                         String xLabel, String yLabel) {
        return builder()
            .id(id)
            .title(title)
            .type(ChartType.BAR)
            .dataPoints(dataPoints)
            .xAxisLabel(xLabel)
            .yAxisLabel(yLabel)
            .build();
    }
    
    /**
     * 為折線圖創建便利方法
     */
    public static ChartData createLineChart(String id, String title, List<DataPoint> dataPoints,
                                          String xLabel, String yLabel) {
        return builder()
            .id(id)
            .title(title)
            .type(ChartType.LINE)
            .dataPoints(dataPoints)
            .xAxisLabel(xLabel)
            .yAxisLabel(yLabel)
            .build();
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public ChartType getType() { return type; }
    public List<DataPoint> getDataPoints() { return dataPoints; }
    public Map<String, Object> getConfig() { return config; }
    public String getXAxisLabel() { return xAxisLabel; }
    public String getYAxisLabel() { return yAxisLabel; }
    
    /**
     * 獲取資料點總數
     */
    public int getDataPointCount() {
        return dataPoints.size();
    }
    
    /**
     * 獲取資料總和
     */
    public double getTotalValue() {
        return dataPoints.stream()
            .mapToDouble(DataPoint::getValue)
            .sum();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChartData chartData = (ChartData) obj;
        return Objects.equals(id, chartData.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("ChartData{id='%s', title='%s', type=%s, dataPoints=%d}", 
            id, title, type, dataPoints.size());
    }
}