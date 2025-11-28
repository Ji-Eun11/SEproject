package com.example.demo.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://52.79.197.189:3000", "http://localhost:3000") // AWS IP와 로컬 허용
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true)
    }
}