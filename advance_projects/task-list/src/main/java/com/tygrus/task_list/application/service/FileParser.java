package com.tygrus.task_list.application.service;

import com.tygrus.task_list.application.dto.CreateTaskRequest;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * 檔案解析器介面
 * 
 * 定義檔案解析的統一介面，支援不同格式的檔案解析
 */
public interface FileParser {
    
    /**
     * 解析檔案內容為 CreateTaskRequest 串流
     * 
     * @param inputStream 檔案輸入串流
     * @param fileName 檔案名稱
     * @return CreateTaskRequest 的串流
     * @throws com.tygrus.task_list.application.exception.InvalidFileFormatException 當檔案格式無效時
     */
    Stream<CreateTaskRequest> parse(InputStream inputStream, String fileName);
    
    /**
     * 檢查是否支援指定的檔案格式
     * 
     * @param fileName 檔案名稱
     * @return 是否支援該格式
     */
    boolean supports(String fileName);
    
    /**
     * 取得支援的檔案副檔名
     * 
     * @return 支援的副檔名陣列
     */
    String[] getSupportedExtensions();
}