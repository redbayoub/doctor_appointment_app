package com.pro0inter.HeyDocServer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@ComponentScan("com.pro0inter.HeyDocServer")
@SpringBootApplication
public class HeyDocServerApplication implements CommandLineRunner {

	@Autowired
	private FirebaseConstants firebaseConstants;

	public static void main(String[] args){

		SpringApplication.run(HeyDocServerApplication.class, args);
	}

	private void init_firabase() {

		FileInputStream serviceAccount =
				null;
		try {
			serviceAccount = new FileInputStream(firebaseConstants.configPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		FirebaseOptions options = null;
		try {
			options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl(firebaseConstants.databaseUrl)
					.build();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		FirebaseApp.initializeApp(options);
	}

	@Override
	public void run(String... args) throws Exception {
		init_firabase();
	}


	@Component
	@Primary
	private static final class FirebaseConstants {
		@Value("${com.pro0inter.HeyDocServer.firebase.config.path}")
		String configPath;
		@Value("${com.pro0inter.HeyDocServer.firebase.database.url}")
		String databaseUrl;
	}

}
