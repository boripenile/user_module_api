package app.config;

import static org.javalite.app_config.AppConfig.p;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.javalite.activeweb.AppContext;
import org.javalite.activeweb.Bootstrap;

public class AppBootstrap extends Bootstrap {
	
	private void loadSmsSettings() {
		System.out.println("Config location: " + p("config_location"));
		File configFile = new File(p("config_location"+ "/sms.properties"));
		try {
			Reader reader = new InputStreamReader(new FileInputStream(configFile));
			Properties properties = new Properties();
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
    public void init(AppContext context) {     
    	loadSmsSettings();
    }
}
