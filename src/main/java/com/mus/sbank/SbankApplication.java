package com.mus.sbank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Java Bank App",
				description = "Banked Rest APIs for sbank",
				version = "v1.0",
				contact = @Contact(
						name = "Shatha Alsallama",
						email =  "shathaalsallama2@gmail.com"
					//	url = "https://github.com/ShathaAlsallama/"
				),
				license = @License(
						name = "Novi School"
						//url = ""
				)

		),
		externalDocs = @ExternalDocumentation(
				description = "The Java Bank App Documentation"
				//url = ""


		)
)
public class SbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbankApplication.class, args);
	}

}
