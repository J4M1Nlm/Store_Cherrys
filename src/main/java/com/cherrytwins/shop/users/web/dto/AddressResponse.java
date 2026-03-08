package com.cherrytwins.shop.users.web.dto;

import java.time.OffsetDateTime;

public class AddressResponse {
    private Long id;
    private String label;
    private String recipientName;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
    private OffsetDateTime createdAt;

    public AddressResponse(Long id, String label, String recipientName, String line1, String line2,
                           String city, String state, String postalCode, String country,
                           boolean isDefault, OffsetDateTime createdAt) {
        this.id = id;
        this.label = label;
        this.recipientName = recipientName;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getLabel() { return label; }
    public String getRecipientName() { return recipientName; }
    public String getLine1() { return line1; }
    public String getLine2() { return line2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public boolean isDefault() { return isDefault; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
