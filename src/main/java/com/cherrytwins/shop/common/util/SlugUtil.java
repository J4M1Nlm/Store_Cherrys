package com.cherrytwins.shop.common.util;

import java.text.Normalizer;

public final class SlugUtil {
    private SlugUtil() {}

    public static String toSlug(String input) {
        if (input == null) return null;
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        slug = slug.replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase();
        slug = slug.replaceAll("\\-+", "-");
        slug = slug.replaceAll("(^-+|-+$)", "");
        return slug;
    }
}