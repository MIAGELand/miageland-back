package fr.miage.MIAGELand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
@SpringBootApplication

public class MiageLandApplication {

	private final Environment environment;

	public MiageLandApplication(Environment environment) {
		this.environment = environment;
	}

	public static void main(String[] args) throws UnknownHostException {
		SpringApplication app = new SpringApplication(MiageLandApplication.class);
		Environment environment = app.run(args).getEnvironment();
		String protocol = "http";
		String serverAddress = InetAddress.getLocalHost().getHostAddress();
		int serverPort = environment.getProperty("server.port", Integer.class, 8080);
		String contextPath = environment.getProperty("server.servlet.context-path", "");
		System.out.println("Application is running! Access URLs:");
		System.out.println("Local: " + protocol + "://" + serverAddress + ":" + serverPort + contextPath);
	}

}
