package com.grupoagil.proyectoagil.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupoagil.proyectoagil.model.Usuario;
import com.grupoagil.proyectoagil.service.UsuarioService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> datosLogin) {
        String user = datosLogin.get("user");
        String password = datosLogin.get("password");

        Map<String, String> respuesta = new HashMap<>();

        try {
            Optional<Usuario> usuario = usuarioService.iniciarSesion(user, password);

            if (usuario.isPresent()) {
                Usuario usuarioObj = usuario.get();
                respuesta.put("rol", usuarioObj.getRol().getRol());
                respuesta.put("nombre", usuarioObj.getNombre());
                respuesta.put("userId", usuarioObj.getIdUser());
                respuesta.put("mensaje", "Inicio de sesión exitoso. Bienvenido " + usuarioObj.getNombre());
                return ResponseEntity.ok(respuesta);
            } else {
                respuesta.put("mensaje", "Credenciales inválidas");
                return ResponseEntity.badRequest().body(respuesta);
            }

        } catch (RuntimeException e) {
            // Errores como usuario inactivo
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }  // Devolvemos la respuesta con el mensaje
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();

        // filtrar administradores
        usuarios = usuarios.stream()
                .filter(u -> !u.getRol().getRol().equalsIgnoreCase("Administrador"))
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
    try {
        Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
        return ResponseEntity.status(201).body(nuevoUsuario);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }
}

    @GetMapping("/{idUser}")
    public ResponseEntity<?> getUsuarioById(@PathVariable String idUser) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(idUser);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }

    // ========== NUEVO: CAMBIAR CONTRASEÑA ==========
    @PutMapping("/{idUser}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable String idUser,
            @RequestBody CambioPasswordRequest request) {
        
        try {
            Usuario usuarioActualizado = usuarioService.cambiarPassword(
                idUser,
                request.getPasswordActual(),
                request.getNuevaPassword()
            );
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Contraseña actualizada exitosamente",
                "usuario", usuarioActualizado.getUser()
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // DTO interno para recibir la solicitud de cambio de contraseña
    public static class CambioPasswordRequest {
        private String passwordActual;
        private String nuevaPassword;

        // Getters y Setters
        public String getPasswordActual() { return passwordActual; }
        public void setPasswordActual(String passwordActual) { this.passwordActual = passwordActual; }
        
        public String getNuevaPassword() { return nuevaPassword; }
        public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable String idUser,
            @RequestBody Usuario usuarioData
    ) {
        try {
            Usuario actualizado = usuarioService.updateUsuario(idUser, usuarioData);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


// ================= CAMBIAR ESTADO =================
    @PutMapping("/{idUser}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable String idUser,
            @RequestBody Map<String, String> body) {

        try {
            String nuevoEstado = body.get("estado");  // ACTIVO o INACTIVO
            Usuario usuario = usuarioService.cambiarEstado(idUser, nuevoEstado);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Estado actualizado",
                    "estadoNuevo", usuario.getEstado()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
