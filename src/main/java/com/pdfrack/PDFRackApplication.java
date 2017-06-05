package com.pdfrack;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class PDFRackApplication {

	@Autowired
	private static Environment environment;

	public static void main(String[] args) {

		if(args.length == 0 || !args[0].startsWith("--media")) {
			System.err.println("Usage java -jar JARNAME --media=PathToMedia");
			System.exit(1);
		}

		SpringApplication.run(PDFRackApplication.class, args);

	}
}
