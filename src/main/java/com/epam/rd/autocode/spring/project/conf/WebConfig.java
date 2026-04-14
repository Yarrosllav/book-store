package com.epam.rd.autocode.spring.project.conf;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToGenreConverter());
        registry.addConverter(new StringToAgeGroupConverter());
    }

    static class StringToGenreConverter implements Converter<String, Genre> {
        @Override
        public Genre convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            try {
                return Genre.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    static class StringToAgeGroupConverter implements Converter<String, AgeGroup> {
        @Override
        public AgeGroup convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            try {
                return AgeGroup.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
