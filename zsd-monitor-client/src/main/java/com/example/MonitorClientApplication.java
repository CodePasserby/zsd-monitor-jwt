package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类，应用程序的入口点
 */
@SpringBootApplication // 标记该类为Spring Boot应用程序的启动类
public class MonitorClientApplication {

	/**
	 * 应用程序的主入口方法
	 *
	 * @param args 启动时传递的命令行参数
	 */
	public static void main(String[] args) {
		// 启动Spring Boot应用程序
		SpringApplication.run(MonitorClientApplication.class, args);
	}

}
