package io.github.autumnforest.boot.commons.log;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("aspect.request.log")
public class AspectLogProperties {
    private boolean enable;
}
