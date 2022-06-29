package dev.brighten.antibot.antibotconfirm;

import dev.brighten.antibot.antibotconfirm.utils.MiscUtils;
import dev.brighten.antibot.antibotconfirm.utils.config.Configuration;
import dev.brighten.antibot.antibotconfirm.utils.config.ConfigurationProvider;
import dev.brighten.antibot.antibotconfirm.utils.config.YamlConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class AntibotConfirmApplication {

    public static Configuration CONFIG;

    public static File ROOT_DIR;

    public static boolean USING_CLOUDFLARE;

    public static void main(String[] args) throws IOException {
        ROOT_DIR = new File(System.getProperty("user.dir"));

        // Setting up config stuffs
        File configFile = new File(ROOT_DIR, "config.yml");
        if(!configFile.exists()){
            configFile.getParentFile().mkdirs();
            MiscUtils.copy(AntibotConfirmApplication.class.getResourceAsStream( "config.yml"), configFile);
        }
        CONFIG = ConfigurationProvider.getProvider(YamlConfiguration.class)
                .load(configFile);

        // Gathering global configuration settings
        USING_CLOUDFLARE = CONFIG.getBoolean("cloudflare");

        // Loading Spring
        SpringApplication.run(AntibotConfirmApplication.class, args);
    }

}
