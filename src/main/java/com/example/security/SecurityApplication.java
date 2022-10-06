package com.example.security;

import com.example.security.entity.AppUser;
import com.example.security.entity.Role;
import com.example.security.service.ServiceUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityApplication implements CommandLineRunner {
  private ServiceUser serviceUser;

	public SecurityApplication(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//serviceUser.addUser(new AppUser("diallo","racinediallo@gmail.com","1234"));

		serviceUser.addRole(new Role("ADMIN"));
		serviceUser.addRole(new Role("MANAGER"));
		serviceUser.addRole(new Role("USER"));
		serviceUser.addRole(new Role("SUPER-ADMIN"));

		serviceUser.addUser(new AppUser("DIALLO","RACINESQUAD","1234"));
		serviceUser.addUser(new AppUser("BARRY","RAIHANATOUBRAVE","1234"));
		serviceUser.addUser(new AppUser("SOW","BINTASOW90","1234"));
		serviceUser.addUser(new AppUser("BANGOURA","BIGFODE","1234"));

		serviceUser.addRoleToUser("RACINESQUAD","SUPER-ADMIN");
		serviceUser.addRoleToUser("RAIHANATOUBRAVE","MANAGER");
		serviceUser.addRoleToUser("BINTASOW90","USER");
		serviceUser.addRoleToUser("BIGFODE","ADMIN");
	}


}
