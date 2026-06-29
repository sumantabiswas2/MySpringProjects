package com.example.dualdb.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

//@Configuration
//@ConfigurationProperties(prefix = "database") // Maps directly to keys inside the path
public class VaultConfigProperties {
    
	//@Value("${mysql.username}")
	@Getter
	private String username;
	
	@Value("${mysql.password}")
	@Getter
    private String password;

   
    
}