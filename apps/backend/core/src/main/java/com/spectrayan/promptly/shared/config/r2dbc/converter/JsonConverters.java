package com.spectrayan.promptly.shared.config.r2dbc.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.*;

/**
 * R2DBC converters for JSON columns.
 * <p>
 * These converters use the Spring-managed {@link ObjectMapper} bean to handle
 * serialization between database JSON/JSONB strings and typed Java collections.
 * Registered in {@link com.spectrayan.promptly.shared.config.r2dbc.R2dbcConfig}.
 */
public final class JsonConverters {

    private JsonConverters() {}

    // ── Set<String> ─────────────────────────────────────────────────

    @ReadingConverter
    @RequiredArgsConstructor
    public static class StringToSetConverter implements Converter<String, Set<String>> {
        private final ObjectMapper objectMapper;

        @Override
        @SneakyThrows
        public Set<String> convert(String source) {
            return objectMapper.readValue(source, new TypeReference<>() {});
        }
    }

    @WritingConverter
    @RequiredArgsConstructor
    public static class SetToStringConverter implements Converter<Set<String>, String> {
        private final ObjectMapper objectMapper;

        @Override
        @SneakyThrows
        public String convert(Set<String> source) {
            return objectMapper.writeValueAsString(source);
        }
    }

    // ── List<String> ────────────────────────────────────────────────

    @ReadingConverter
    @RequiredArgsConstructor
    public static class StringToListConverter implements Converter<String, List<String>> {
        private final ObjectMapper objectMapper;

        @Override
        @SneakyThrows
        public List<String> convert(String source) {
            return objectMapper.readValue(source, new TypeReference<>() {});
        }
    }

    @WritingConverter
    @RequiredArgsConstructor
    public static class ListToStringConverter implements Converter<List<String>, String> {
        private final ObjectMapper objectMapper;

        @Override
        @SneakyThrows
        public String convert(List<String> source) {
            return objectMapper.writeValueAsString(source);
        }
    }

    // ── Map<String, Object> ─────────────────────────────────────────

    @ReadingConverter
    @RequiredArgsConstructor
    public static class StringToMapConverter implements Converter<String, Map<String, Object>> {
        private final ObjectMapper objectMapper;

        @Override
        @SneakyThrows
        public Map<String, Object> convert(String source) {
            return objectMapper.readValue(source, new TypeReference<>() {});
        }
    }

    @WritingConverter
    @RequiredArgsConstructor
    public static class MapToStringConverter implements Converter<Map<String, Object>, String> {
        private final ObjectMapper objectMapper;

        @Override
        @SneakyThrows
        public String convert(Map<String, Object> source) {
            return objectMapper.writeValueAsString(source);
        }
    }
}
