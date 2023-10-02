package dropthebass.equipo4;

import dropthebass.equipo4.s3.S3Buckets;
import dropthebass.equipo4.s3.S3Service;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DropTheBassApplication {

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		SpringApplication.run(DropTheBassApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(S3Service s3Service, S3Buckets s3Buckets){
		return args -> {
			//testBucketUploadAndDownload( s3Service,  s3Buckets);
		};
	}

	private static void testBucketUploadAndDownload(S3Service s3Service, S3Buckets s3Buckets){
		s3Service.putObject(
				"foo",
				"hello world".getBytes()
		);
		byte[] obj = s3Service.getObject("foo");
		System.out.println("Hooray!"+new String(obj));

	}
	/*@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://g4-deploy-react-app.s3-website.us-east-2.amazonaws.com", "http://localhost:3000", "http://localhost:3000/" , "http://186.29.226.72:3000", "https://frontend-grupo4-integradora-girrrsxzt-devbru.vercel.app", "https://frontend-grupo4-integradora.vercel.app/")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}*/

}

//addMapping("/**").allowedOrigins()