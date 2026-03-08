package com.cherrytwins.shop.users.web;

import com.cherrytwins.shop.security.CurrentUser;
import com.cherrytwins.shop.security.UserPrincipal;
import com.cherrytwins.shop.users.service.UserService;
import com.cherrytwins.shop.users.web.dto.UpdateProfileRequest;
import com.cherrytwins.shop.users.web.dto.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Perfil del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @Operation(summary = "Mi perfil", description = "Devuelve el perfil del usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me")
    public UserResponse me(@CurrentUser UserPrincipal principal) {
        return userService.getMe(principal.getId());
    }

    @Operation(summary = "Actualizar mi perfil", description = "Actualiza fullName y phone del usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado"),
            @ApiResponse(responseCode = "400", description = "Validación"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PutMapping("/me")
    public UserResponse updateMe(@CurrentUser UserPrincipal principal,
                                 @Valid @RequestBody UpdateProfileRequest req) {
        return userService.updateProfile(principal.getId(), req);
    }
}
