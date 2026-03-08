package com.cherrytwins.shop.users.service;

import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.users.domain.Address;
import com.cherrytwins.shop.users.repository.AddressRepository;
import com.cherrytwins.shop.users.web.dto.AddressRequest;
import com.cherrytwins.shop.users.web.dto.AddressResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    private AddressResponse toResponse(Address a) {
        return new AddressResponse(
                a.getId(),
                a.getLabel(),
                a.getRecipientName(),
                a.getLine1(),
                a.getLine2(),
                a.getCity(),
                a.getState(),
                a.getPostalCode(),
                a.getCountry(),
                a.isDefault(),
                a.getCreatedAt()
        );
    }

    public List<AddressResponse> list(Long userId) {
        return addressRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public AddressResponse get(Long userId, Long addressId) {
        Address a = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));
        return toResponse(a);
    }

    @Transactional
    public AddressResponse create(Long userId, AddressRequest req) {
        Address a = new Address();
        a.setUserId(userId);
        a.setLabel(req.getLabel());
        a.setRecipientName(req.getRecipientName());
        a.setLine1(req.getLine1());
        a.setLine2(req.getLine2());
        a.setCity(req.getCity());
        a.setState(req.getState());
        a.setPostalCode(req.getPostalCode());
        a.setCountry(req.getCountry().toUpperCase());

        boolean first = addressRepository.countByUserId(userId) == 0;
        boolean makeDefault = Boolean.TRUE.equals(req.getMakeDefault()) || first;

        if (makeDefault) {
            unsetDefault(userId);
            a.setDefault(true);
        }

        return toResponse(addressRepository.save(a));
    }

    @Transactional
    public AddressResponse update(Long userId, Long addressId, AddressRequest req) {
        Address a = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        a.setLabel(req.getLabel());
        a.setRecipientName(req.getRecipientName());
        a.setLine1(req.getLine1());
        a.setLine2(req.getLine2());
        a.setCity(req.getCity());
        a.setState(req.getState());
        a.setPostalCode(req.getPostalCode());
        a.setCountry(req.getCountry().toUpperCase());

        if (Boolean.TRUE.equals(req.getMakeDefault())) {
            unsetDefault(userId);
            a.setDefault(true);
        }

        return toResponse(a);
    }

    @Transactional
    public AddressResponse setDefault(Long userId, Long addressId) {
        Address a = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        unsetDefault(userId);
        a.setDefault(true);
        return toResponse(a);
    }

    @Transactional
    public void delete(Long userId, Long addressId) {
        Address a = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));
        addressRepository.delete(a);
    }

    private void unsetDefault(Long userId) {
        List<Address> defaults = addressRepository.findAllByUserIdAndIsDefaultTrue(userId);
        for (Address d : defaults) {
            d.setDefault(false);
        }
    }
}
