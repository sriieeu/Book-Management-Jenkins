package com.example.book_management;

import org.springframework.boot.SpringApplication;

public class TestBookManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(BookManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
