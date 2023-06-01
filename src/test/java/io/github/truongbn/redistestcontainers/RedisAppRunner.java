package io.github.truongbn.redistestcontainers;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import io.github.truongbn.redistestcontainers.config.LocalRedisInitializer;

@SpringBootTest
@ComponentScan(basePackages = "io.github.truongbn.redistestcontainers")
@ConfigurationPropertiesScan(basePackages = "io.github.truongbn.redistestcontainers")
class RedisAppRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RedisAppRunner.class).initializers(new LocalRedisInitializer())
                .run(args);
    }
}
