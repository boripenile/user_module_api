package app.config;

import org.javalite.activeweb.AbstractDBConfig;
import org.javalite.activeweb.AppContext;
import static org.javalite.app_config.AppConfig.p;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class DbConfig extends AbstractDBConfig {

    public void init(AppContext context) {
    	String configLoc = p("config_location");
    	configFile(configLoc + "/database.properties");
    	environment("development");
    }
}
