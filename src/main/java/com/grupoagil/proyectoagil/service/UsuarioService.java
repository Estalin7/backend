package com.grupoagil.proyectoagil.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.grupoagil.proyectoagil.model.Usuario;
import com.grupoagil.proyectoagil.model.Rol; // <--- NUEVO: Importamos el modelo Rol
import com.grupoagil.proyectoagil.repository.UsuarioRepository;
import com.grupoagil.proyectoagil.repository.RolRepository; // <--- NUEVO: Importamos el repositorio Rol

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository; // <--- NUEVO: Declaramos el repositorio

    // <--- NUEVO: Actualizamos el constructor para recibir RolRepository
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    // Método para validar inicio de sesión
    public Optional<Usuario> iniciarSesion(String user, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByUserIgnoreCase(user);

        if (usuario.isPresent()) {
            Usuario u = usuario.get();

            // 1. Validar estado
            if (!u.getEstado().equalsIgnoreCase("activo")) {
                throw new RuntimeException("No puede iniciar sesión. Contacte al administrador.");
            }

            // 2. Validar contraseña
            if (u.getPassword().equals(password)) {
                return usuario;
            }
        }

        return Optional.empty();
    }


    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    

    // Crear un nuevo usuario
    public Usuario createUsuario(Usuario usuario) {
        // 1. Validar campos obligatorios
        if (isBlank(usuario.getUser()) ||
            isBlank(usuario.getPassword()) ||
            isBlank(usuario.getNombre()) ||
            isBlank(usuario.getApellido()) ||
            isBlank(usuario.getDni()) ||
            isBlank(usuario.getEmail()) ||
            usuario.getRol() == null) {
            throw new RuntimeException("Todos los campos obligatorios deben estar completos");
        }

        // 2. Validar DNI: 8 dígitos numéricos
        if (!usuario.getDni().matches("\\d{8}")) {
            throw new RuntimeException("El DNI debe tener exactamente 8 dígitos numéricos");
        }

        // 3. Validar formato de contraseña (8-12, 1 mayúscula, al menos 1 número, sin símbolos)
        validarFormatoPassword(usuario.getPassword());

        // 4. Validar duplicados de nombre de usuario
        if (usuarioRepository.findByUserIgnoreCase(usuario.getUser()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está registrado");
        }

        // 5. Validar duplicados de correo
        if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // 6. Validar duplicados de DNI
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        // 7. Estado por defecto: ACTIVO
        if (isBlank(usuario.getEstado())) {
            usuario.setEstado("activo");
        }
        
        // --- INICIO DE LA CORRECCIÓN CLAVE ---
        // Buscamos el rol real en la BD para asegurarnos de que existe y Hibernate lo reconozca
        Long rolId = usuario.getRol().getIdRol();
        Rol rolReal = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("El Rol seleccionado no existe en la base de datos"));
        
        usuario.setRol(rolReal); // Asignamos el rol "vivo" de la base de datos
        // --- FIN DE LA CORRECCIÓN CLAVE ---

        // 8. Generar idUser tipo U1, U2, ...
        String nuevoId = generarSiguienteIdUsuario();
        usuario.setIdUser(nuevoId);

        // 9. Guardar
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> getUsuarioById(String idUser) {
        return usuarioRepository.findById(idUser);
    }

    // ========== NUEVO: CAMBIO DE CONTRASEÑA ==========
    
    /**
     * Cambia la contraseña de un usuario con validaciones completas
     * @param idUser ID del usuario
     * @param passwordActual Contraseña actual para verificar
     * @param nuevaPassword Nueva contraseña a establecer
     * @return Usuario actualizado
     * @throws RuntimeException si hay errores de validación
     */
    public Usuario cambiarPassword(String idUser, String passwordActual, String nuevaPassword) {
        
        // 1. Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificar que la contraseña actual es correcta
        if (!usuario.getPassword().equals(passwordActual)) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // 3. Verificar que la nueva contraseña es diferente
        if (usuario.getPassword().equals(nuevaPassword)) {
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }

        // 4. Validar formato de nueva contraseña (8-12 caracteres, 1 mayúscula, sin símbolos)
        validarFormatoPassword(nuevaPassword);

        // 5. Actualizar contraseña
        usuario.setPassword(nuevaPassword);
        return usuarioRepository.save(usuario);
    }

    /**
     * Valida el formato de la contraseña según criterios:
     * - Longitud: 8-12 caracteres
     * - Al menos 1 mayúscula
     * - Resto minúsculas y/o números
     * - Sin símbolos especiales
     */
    private void validarFormatoPassword(String password) {
        
        // Validar longitud
        if (password.length() < 8 || password.length() > 12) {
            throw new RuntimeException("La contraseña debe tener entre 8 y 12 caracteres");
        }

        // Validar que tenga al menos una mayúscula
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("La contraseña debe contener al menos una letra mayúscula");
        }

        // Validar que NO tenga símbolos especiales (solo letras y números)
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            throw new RuntimeException("La contraseña solo puede contener letras y números (sin símbolos especiales)");
        }
    }

    private String generarSiguienteIdUsuario() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        int max = 0;
        for (Usuario u : usuarios) {
            String id = u.getIdUser();
            if (id != null && id.startsWith("U")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "U" + (max + 1);  // Si tienes hasta U5, el siguiente será U6
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public Usuario updateUsuario(String idUser, Usuario updatedData) {

        Usuario usuario = usuarioRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // --- Validar campos obligatorios ---
        if (isBlank(updatedData.getUser()) ||
            isBlank(updatedData.getNombre()) ||
            isBlank(updatedData.getApellido()) ||
            isBlank(updatedData.getDni()) ||
            isBlank(updatedData.getEmail()) ||
            updatedData.getRol() == null) {
            throw new RuntimeException("Todos los campos obligatorios deben estar completos");
        }

        // --- Validar DNI ---
        if (!updatedData.getDni().matches("\\d{8}")) {
            throw new RuntimeException("El DNI debe tener exactamente 8 dígitos numéricos");
        }

        // --- Validar formato email ---
        if (!updatedData.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("El correo electrónico no es válido");
        }

        // --- Validar duplicado username (si cambió) ---
        if (!usuario.getUser().equalsIgnoreCase(updatedData.getUser())) {
            if (usuarioRepository.findByUserIgnoreCase(updatedData.getUser()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya está registrado");
            }
        }

        // --- Validar duplicado email ---
        if (!usuario.getEmail().equalsIgnoreCase(updatedData.getEmail()) &&
                usuarioRepository.existsByEmailIgnoreCase(updatedData.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // --- Validar duplicado DNI ---
        if (!usuario.getDni().equals(updatedData.getDni()) &&
                usuarioRepository.existsByDni(updatedData.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }
        
        // --- CORRECCIÓN TAMBIÉN AQUÍ PARA ACTUALIZAR ---
        Long rolId = updatedData.getRol().getIdRol();
        Rol rolReal = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("El Rol seleccionado no existe"));
        usuario.setRol(rolReal);
        // ----------------------------------------------

        // === Aplicar los cambios (estado NO se toca) ===
        usuario.setUser(updatedData.getUser());
        usuario.setNombre(updatedData.getNombre());
        usuario.setApellido(updatedData.getApellido());
        usuario.setEmail(updatedData.getEmail());
        usuario.setDni(updatedData.getDni());
        // El rol ya se actualizó arriba

        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarEstado(String idUser, String nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado(nuevoEstado); // "INACTIVO" o "ACTIVO"

        return usuarioRepository.save(usuario);
    }
}