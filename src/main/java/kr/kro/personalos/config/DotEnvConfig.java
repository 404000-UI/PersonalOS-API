package kr.kro.personalos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DotEnvConfig {

    private final Dotenv dotenv;

    public DotEnvConfig() {
        this.dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
    }

    @Bean
    public Dotenv dotenv() {
        return this.dotenv;
    }

}
