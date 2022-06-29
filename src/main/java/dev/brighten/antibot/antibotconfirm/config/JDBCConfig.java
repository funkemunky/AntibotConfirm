package dev.brighten.antibot.antibotconfirm.config;

import dev.brighten.antibot.antibotconfirm.AntibotConfirmApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class JDBCConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder builder = DataSourceBuilder.create();

        switch (AntibotConfirmApplication.CONFIG.getString("database.type").toLowerCase()) {
            case "h2":
            case "flatfile":
                builder.driverClassName("org.h2.Driver");
                builder.url("jdbc:h2:file:" + AntibotConfirmApplication.ROOT_DIR.getAbsolutePath()
                        + File.separator + "webDB");
                break;
            case "mysql":
            case "sql":
                builder.driverClassName("com.mysql.cj.jdbc.Driver");
                builder.url("jdbc:mysql://" + AntibotConfirmApplication.CONFIG.getString("database.ip")
                        + ":" + AntibotConfirmApplication.CONFIG.getInt("database.port")
                        + "/?useSSL=true&autoReconnect=true");
                break;
        }

        builder.username(AntibotConfirmApplication.CONFIG.getString("database.username"));
        builder.password(AntibotConfirmApplication.CONFIG.getString("database.password"));

        return builder.build();
    }
}
