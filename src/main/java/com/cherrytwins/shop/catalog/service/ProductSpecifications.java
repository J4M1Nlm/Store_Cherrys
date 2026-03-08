package com.cherrytwins.shop.catalog.service;

import com.cherrytwins.shop.catalog.domain.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {
    private ProductSpecifications() {}

    public static Specification<Product> activeOnly(Boolean activeOnly) {
        if (activeOnly == null) return null;
        return (root, query, cb) -> cb.equal(root.get("active"), activeOnly);
    }

    public static Specification<Product> nameOrSlugContains(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("slug")), like)
        );
    }

    public static Specification<Product> byArtistId(Long artistId) {
        if (artistId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("artistId"), artistId);
    }

    public static Specification<Product> byCategoryId(Long categoryId) {
        if (categoryId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("categoryId"), categoryId);
    }

    public static Specification<Product> priceBetween(Integer minCents, Integer maxCents) {
        if (minCents == null && maxCents == null) return null;
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();
            if (minCents != null) p = cb.and(p, cb.greaterThanOrEqualTo(root.get("basePriceCents"), minCents));
            if (maxCents != null) p = cb.and(p, cb.lessThanOrEqualTo(root.get("basePriceCents"), maxCents));
            return p;
        };
    }
}