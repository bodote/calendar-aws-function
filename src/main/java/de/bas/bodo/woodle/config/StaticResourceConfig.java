package de.bas.bodo.woodle.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    @Bean
    public ResourceHttpRequestHandler staticResourceHttpRequestHandler() {
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        List<Resource> locations = List.of(new ClassPathResource("static/"));
        handler.setLocations(locations);
        handler.setCacheSeconds(31536000); // 1 year
        return handler;
    }
}
