package com.cherrytwins.shop.users.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressRequest {

    @Size(max = 60)
    private String label;

    @Size(max = 200)
    private String recipientName;

    @NotBlank @Size(max = 255)
    private String line1;

    @Size(max = 255)
    private String line2;

    @NotBlank @Size(max = 120)
    private String city;

    @Size(max = 120)
    private String state;

    @Size(max = 30)
    private String postalCode;

    @NotBlank @Size(min = 2, max = 2)
    private String country;

    private Boolean makeDefault;

    public String getLabel() { return label; }
    public String getRecipientName() { return recipientName; }
    public String getLine1() { return line1; }
    public String getLine2() { return line2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public Boolean getMakeDefault() { return makeDefault; }

    public void setLabel(String label) { this.label = label; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public void setLine1(String line1) { this.line1 = line1; }
    public void setLine2(String line2) { this.line2 = line2; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setCountry(String country) { this.country = country; }
    public void setMakeDefault(Boolean makeDefault) { this.makeDefault = makeDefault; }
}
