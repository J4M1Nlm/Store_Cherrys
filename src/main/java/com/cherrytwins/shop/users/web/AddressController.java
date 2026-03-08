package com.cherrytwins.shop.users.web;

import com.cherrytwins.shop.security.CurrentUser;
import com.cherrytwins.shop.security.UserPrincipal;
import com.cherrytwins.shop.users.service.AddressService;
import com.cherrytwins.shop.users.web.dto.AddressRequest;
import com.cherrytwins.shop.users.web.dto.AddressResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Addresses", description = "Direcciones del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users/addresses")
public class AddressController {

    private final AddressService addressService;
    public AddressController(AddressService addressService) { this.addressService = addressService; }

    @Operation(summary = "Listar mis direcciones")
    @GetMapping
    public List<AddressResponse> list(@CurrentUser UserPrincipal principal) {
        return addressService.list(principal.getId());
    }

    @Operation(summary = "Obtener una dirección por id")
    @GetMapping("/{id}")
    public AddressResponse get(@CurrentUser UserPrincipal principal, @PathVariable Long id) {
        return addressService.get(principal.getId(), id);
    }

    @Operation(summary = "Crear dirección")
    @PostMapping
    public AddressResponse create(@CurrentUser UserPrincipal principal, @Valid @RequestBody AddressRequest req) {
        return addressService.create(principal.getId(), req);
    }

    @Operation(summary = "Actualizar dirección")
    @PutMapping("/{id}")
    public AddressResponse update(@CurrentUser UserPrincipal principal,
                                  @PathVariable Long id,
                                  @Valid @RequestBody AddressRequest req) {
        return addressService.update(principal.getId(), id, req);
    }

    @Operation(summary = "Marcar como default")
    @PutMapping("/{id}/default")
    public AddressResponse setDefault(@CurrentUser UserPrincipal principal, @PathVariable Long id) {
        return addressService.setDefault(principal.getId(), id);
    }

    @Operation(summary = "Eliminar dirección")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUser UserPrincipal principal, @PathVariable Long id) {
        addressService.delete(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}