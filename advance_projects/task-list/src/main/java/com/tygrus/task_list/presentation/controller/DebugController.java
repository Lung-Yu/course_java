package com.tygrus.task_list.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/debug")
public class DebugController {
    
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Debug controller working";
    }
    
    @GetMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createTaskForm() {
        String html = """
            <!DOCTYPE html>
            <html>
            <head><title>新增任務</title></head>
            <body>
                <h1>新增任務</h1>
                <form action="/tasks/create" method="post">
                    <div>
                        <label>標題:</label>
                        <input type="text" name="title" required>
                    </div>
                    <div>
                        <label>描述:</label>
                        <textarea name="description"></textarea>
                    </div>
                    <div>
                        <label>優先級:</label>
                        <select name="priority">
                            <option value="LOW">低</option>
                            <option value="MEDIUM">中</option>
                            <option value="HIGH">高</option>
                            <option value="URGENT">緊急</option>
                        </select>
                    </div>
                    <button type="submit">創建任務</button>
                </form>
            </body>
            </html>
            """;
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(html);
    }
}