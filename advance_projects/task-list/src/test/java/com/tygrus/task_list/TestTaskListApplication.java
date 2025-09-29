package com.tygrus.task_list;

import org.springframework.boot.SpringApplication;

public class TestTaskListApplication {

	public static void main(String[] args) {
		SpringApplication.from(TaskListApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
