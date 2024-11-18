package al_hiro.com.Mkoba.Management.System;

import al_hiro.com.Mkoba.Management.System.utils.SpringContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MkobaAppApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(MkobaAppApplication.class, args);
		SpringContext springContext = context.getBean(SpringContext.class);
		springContext.log("\n\n**** Mkoba Management System is Up and Running ****\n\n");
	}

}
