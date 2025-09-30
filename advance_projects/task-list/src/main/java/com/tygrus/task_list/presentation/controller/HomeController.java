package com.tygrus.task_list.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主頁控制器
 * 
 * 處理根路徑訪問，重定向到任務列表頁面
 */
@Controller
public class HomeController {
    
    /**
     * 首頁重定向到任務列表
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/tasks";
    }
    
    /**
     * 根據需要提供其他首頁路由
     */
    @GetMapping("/index")
    public String index() {
        return "redirect:/tasks";
    }
}