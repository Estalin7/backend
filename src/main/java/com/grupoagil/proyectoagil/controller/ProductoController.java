package com.grupoagil.proyectoagil.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.grupoagil.proyectoagil.model.Inventario;
import com.grupoagil.proyectoagil.model.Producto;
import com.grupoagil.proyectoagil.service.FileStorageService;
import com.grupoagil.proyectoagil.service.ProductoService;
import com.grupoagil.proyectoagil.service.ProductoService.ProductoResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private FileStorageService fileStorageService;

    /** 
     * Obtener todos los productos
     */
    @GetMapping
    public List<ProductoResponse> getAllProductos() {
        return productoService.getAllProductos();
    }

    /**
     * Crear un nuevo producto
     */
    @PostMapping
    public ResponseEntity<?> createProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("categoria") String categoria,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("cantidadDisponible") Integer cantidadDisponible,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            String imagenUrl = null;
            
            // Si se subió una imagen, guardarla
            if (imagen != null && !imagen.isEmpty()) {
                String fileName = fileStorageService.storeFile(imagen);
                imagenUrl = "/uploads/productos/" + fileName;
            }

            Producto producto = productoService.createProducto(nombre, categoria, precio, descripcion, cantidadDisponible, imagenUrl);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Editar un producto por su nombre
     */
    @PutMapping("/editar/{nombreExistente}")
    public ResponseEntity<?> editProducto(
            @PathVariable String nombreExistente,
            @RequestParam("nombre") String nuevoNombre,
            @RequestParam("categoria") String categoria,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("cantidadDisponible") Integer cantidadDisponible,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            String imagenUrl = null;
            
            // Si se subió una nueva imagen, guardarla
            if (imagen != null && !imagen.isEmpty()) {
                // Eliminar la imagen anterior si existe
                Optional<Producto> productoExistente = productoService.findByNombre(nombreExistente);
                if (productoExistente.isPresent() && productoExistente.get().getImagenUrl() != null) {
                    String oldFileName = productoExistente.get().getImagenUrl().replace("/uploads/productos/", "");
                    fileStorageService.deleteFile(oldFileName);
                }
                
                String fileName = fileStorageService.storeFile(imagen);
                imagenUrl = "/uploads/productos/" + fileName;
            }

            Producto productoActualizado = productoService.editProducto(
                    nombreExistente,
                    nuevoNombre,
                    categoria,
                    precio,
                    descripcion,
                    cantidadDisponible,
                    imagenUrl
            );

            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Buscar un producto por su nombre
     */
    
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<?> buscarProducto(@PathVariable String nombre) {
        Optional<Producto> productoOpt = productoService.findByNombre(nombre);

        if (productoOpt.isPresent()) {
            Producto p = productoOpt.get();
            Inventario inv = productoService.getInventario(p.getIdProducto());
            ProductoResponse resp = new ProductoResponse(p, inv.getCantDispo());
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Producto no encontrado"));
        }
    }

    @GetMapping("/categoria/{nombreCategoria}")
    public ResponseEntity<?> buscarPorCategoria(@PathVariable String nombreCategoria) {
        try {
            List<ProductoResponse> productos = productoService.findByCategoria(nombreCategoria);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
