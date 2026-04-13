package com.jiubredeemer.magic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class SpellSanitizeExecutorConfig {

    @Bean(name = "spellSanitizeExecutor")
    public Executor spellSanitizeExecutor() {
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "spell-sanitize");
            t.setDaemon(false);
            return t;
        };
        return Executors.newSingleThreadExecutor(factory);
    }
}
