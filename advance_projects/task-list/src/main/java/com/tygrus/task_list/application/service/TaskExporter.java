package com.tygrus.task_list.application.service;

import com.tygrus.task_list.application.dto.ExportFormat;
import com.tygrus.task_list.domain.model.Task;

import java.util.List;

/**
 * 任務匯出器介面
 * 
 * 定義任務匯出的契約，支援多種格式
 */
public interface TaskExporter {
    
    /**
     * 檢查是否支援指定的匯出格式
     * 
     * @param format 匯出格式
     * @return 是否支援
     */
    boolean supports(ExportFormat format);
    
    /**
     * 將任務清單匯出為指定格式的檔案內容
     * 
     * @param tasks 任務清單
     * @param fileName 檔案名稱
     * @return 檔案內容的位元組陣列
     * @throws Exception 匯出過程中的任何錯誤
     */
    byte[] export(List<Task> tasks, String fileName) throws Exception;
    
    /**
     * 取得匯出格式的 MIME 類型
     * 
     * @return MIME 類型
     */
    String getMimeType();
}