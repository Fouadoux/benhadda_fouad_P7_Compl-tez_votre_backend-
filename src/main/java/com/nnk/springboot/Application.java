package com.nnk.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		/*BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "password123";
		String hashedPassword = encoder.encode(rawPassword);
		System.out.println("Haché : " + hashedPassword);
*/

		SpringApplication.run(Application.class, args);
	}
}
