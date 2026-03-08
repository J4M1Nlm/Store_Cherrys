package com.cherrytwins.shop.cart.service;

import com.cherrytwins.shop.cart.domain.Cart;
import com.cherrytwins.shop.cart.domain.CartItem;
import com.cherrytwins.shop.cart.domain.CartStatus;
import com.cherrytwins.shop.cart.repository.CartItemRepository;
import com.cherrytwins.shop.cart.repository.CartRepository;
import com.cherrytwins.shop.cart.web.dto.*;
import com.cherrytwins.shop.catalog.domain.Product;
import com.cherrytwins.shop.catalog.domain.ProductImage;
import com.cherrytwins.shop.catalog.domain.ProductVariant;
import com.cherrytwins.shop.catalog.repository.ProductImageRepository;
import com.cherrytwins.shop.catalog.repository.ProductRepository;
import com.cherrytwins.shop.catalog.repository.ProductVariantRepository;
import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductVariantRepository variantRepository,
                       ProductRepository productRepository,
                       ProductImageRepository imageRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public CartResponse getOrCreateMyCart(Long userId) {
        Cart cart = cartRepository.findFirstByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(c);
                });

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest req) {
        if (req.getQuantity() == null || req.getQuantity() < 1) {
            throw new BadRequestException("Quantity must be >= 1");
        }

        Cart cart = cartRepository.findFirstByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(c);
                });

        ProductVariant variant = variantRepository.findById(req.getVariantId())
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        validateVariantBuyable(variant);

        int newQty;
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            newQty = item.getQuantity() + req.getQuantity();
            validateStock(variant, newQty);
            item.setQuantity(newQty);
        } else {
            validateStock(variant, req.getQuantity());
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setVariantId(variant.getId());
            item.setQuantity(req.getQuantity());
            cartItemRepository.save(item);
        }

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemQuantityRequest req) {
        if (req.getQuantity() == null || req.getQuantity() < 1) {
            throw new BadRequestException("Quantity must be >= 1");
        }

        Cart cart = cartRepository.findFirstByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active cart not found"));

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        ProductVariant variant = variantRepository.findById(item.getVariantId())
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        validateVariantBuyable(variant);
        validateStock(variant, req.getQuantity());

        item.setQuantity(req.getQuantity());

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findFirstByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active cart not found"));

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        cartItemRepository.delete(item);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = cartRepository.findFirstByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active cart not found"));

        cartItemRepository.deleteAllByCartId(cart.getId());
        return buildCartResponse(cart);
    }

    @Transactional
    public void abandonCart(Long userId) {
        Cart cart = cartRepository.findFirstByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active cart not found"));
        cart.setStatus(CartStatus.ABANDONED);
    }

    // ----------------- Builders/Validation -----------------

    private void validateVariantBuyable(ProductVariant v) {
        if (!v.isActive()) throw new BadRequestException("Variant is not active");
        if (v.getStockOnHand() < 0) throw new BadRequestException("Variant stock invalid");
    }

    private void validateStock(ProductVariant v, int requestedQty) {
        if (requestedQty > v.getStockOnHand()) {
            throw new BadRequestException("Insufficient stock. Requested=" + requestedQty + ", available=" + v.getStockOnHand());
        }
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findAllByCartIdOrderByIdAsc(cart.getId());

        if (items.isEmpty()) {
            return new CartResponse(cart.getId(), cart.getStatus().name(), null, 0, 0, List.of());
        }

        List<Long> variantIds = items.stream().map(CartItem::getVariantId).distinct().toList();
        Map<Long, ProductVariant> variants = variantRepository.findAllById(variantIds)
                .stream().collect(Collectors.toMap(ProductVariant::getId, v -> v));

        List<Long> productIds = variants.values().stream().map(ProductVariant::getProductId).distinct().toList();
        Map<Long, Product> products = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        // main image per product (simple: first by sort_order)
        Map<Long, String> mainImage = new HashMap<>();
        for (Long pid : productIds) {
            List<ProductImage> imgs = imageRepository.findAllByProductIdOrderBySortOrderAscIdAsc(pid);
            mainImage.put(pid, imgs.isEmpty() ? null : imgs.get(0).getUrl());
        }

        // currency (asumimos consistente; si no, usamos la del primer variant)
        String currency = variants.values().stream().findFirst().map(ProductVariant::getCurrency).orElse(null);

        int subtotal = 0;
        int itemsCount = 0;

        List<CartItemResponse> dto = new ArrayList<>();
        for (CartItem it : items) {
            ProductVariant v = variants.get(it.getVariantId());
            if (v == null) {
                // Si se borró el variant, lo marcamos como no activo y precio 0
                dto.add(new CartItemResponse(
                        it.getId(), it.getVariantId(), null,
                        null, null,
                        null, null, Map.of(),
                        it.getQuantity(), 0, 0, null,
                        0, false, null
                ));
                continue;
            }

            Product p = products.get(v.getProductId());

            int unit = v.getPriceCents();
            int line = unit * it.getQuantity();

            subtotal += line;
            itemsCount += it.getQuantity();

            dto.add(new CartItemResponse(
                    it.getId(),
                    v.getId(),
                    v.getProductId(),
                    p == null ? null : p.getName(),
                    p == null ? null : p.getSlug(),
                    v.getSku(),
                    v.getVariantName(),
                    v.getAttributes(),
                    it.getQuantity(),
                    unit,
                    line,
                    v.getCurrency(),
                    v.getStockOnHand(),
                    v.isActive(),
                    p == null ? null : mainImage.get(p.getId())
            ));
        }

        return new CartResponse(cart.getId(), cart.getStatus().name(), currency, itemsCount, subtotal, dto);
    }
}
