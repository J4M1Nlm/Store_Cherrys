package com.cherrytwins.shop.users.repository;

import com.cherrytwins.shop.users.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserIdOrderByIdDesc(Long userId);
    Optional<Address> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
    List<Address> findAllByUserIdAndIsDefaultTrue(Long userId);
}