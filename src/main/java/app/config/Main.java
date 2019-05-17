package app.config;

import static org.javalite.app_config.AppConfig.p;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import app.utils.HashPassword;

public class Main {
	static Properties properties = null;
	private static void loadSmsSettings() {
		System.out.println("Config location: " + p("config_location"));
		File configFile = new File(p("config_location") + "/sms.properties");
		try {
			Reader reader = new InputStreamReader(new FileInputStream(configFile));
			properties = new Properties();
			properties.load(reader);
			
			for (Object key: properties.keySet()) {
				System.out.println("Key: " + key + ", Value: " + properties.getProperty(key.toString()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		try {
			System.out.println(HashPassword.hashPassword("password01"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
