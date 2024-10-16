package com.deerear.app.util;

import java.time.LocalDateTime;
import java.util.UUID;

public class KeyParser {
    public record BasicKey(LocalDateTime createdAt, UUID id) {
    }

    public static BasicKey parseKey(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        try {
            String[] parts = key.split("_");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid key format");
            }
            LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
            UUID id = UUID.fromString(parts[1]);
            return new BasicKey(createdAt, id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid key format", e);
        }
    }
}