package com.grupoagil.proyectoagil.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupoagil.proyectoagil.model.Categoria;
import com.grupoagil.proyectoagil.model.Inventario;
import com.grupoagil.proyectoagil.model.Producto;
import com.grupoagil.proyectoagil.repository.CategoriaRepository;
import com.grupoagil.proyectoagil.repository.InventarioRepository;
import com.grupoagil.proyectoagil.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Obtener todos los productos
    public List<ProductoResponse> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(p -> {
                    Inventario inv = inventarioRepository.findById(p.getIdProducto())
                            .orElse(new Inventario(p.getIdProducto(), 0));
                    return new ProductoResponse(p, inv.getCantDispo());
                })
                .collect(Collectors.toList());
    }

    public Optional<Producto> findByNombre(String nombre) {
        return productoRepository.findAll().stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    public Inventario getInventario(Long idProducto) {
        return inventarioRepository.findById(idProducto)
                .orElse(new Inventario(idProducto, 0));
    }


    
    public Producto createProducto(String nombre, String categoriaNombre, Double precio,
                                   String descripcion, Integer cantidadDisponible, String imagenUrl) {

        Categoria categoria = categoriaRepository.findAll().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(categoriaNombre))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);
        producto.setImagenUrl(imagenUrl);

        Producto newProducto = productoRepository.save(producto);

        Inventario inventario = new Inventario();
        inventario.setIdProducto(newProducto.getIdProducto());
        inventario.setCantDispo(cantidadDisponible);
        inventarioRepository.save(inventario);

        return newProducto;
    }

    
    public Producto editProducto(String nombreExistente, String nuevoNombre, String categoriaNombre, 
                                Double precio, String descripcion, Integer cantidadDisponible, String nuevaImagenUrl) {
        Producto producto = findByNombre(nombreExistente)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + nombreExistente));
        
        producto.setNombre(nuevoNombre);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);

        // Actualizar imagen solo si se proporcionó una nueva
        if (nuevaImagenUrl != null) {
            producto.setImagenUrl(nuevaImagenUrl);
        }

        Categoria categoria = categoriaRepository.findByNombre(categoriaNombre)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + categoriaNombre));
        producto.setCategoria(categoria);

        Producto productoActualizado = productoRepository.save(producto);

        Inventario inventario = inventarioRepository.findById(producto.getIdProducto())
                .orElse(new Inventario(producto.getIdProducto(), cantidadDisponible));
        inventario.setCantDispo(cantidadDisponible);
        inventarioRepository.save(inventario);

        return productoActualizado;
    }

    // Buscar por categoría
    public List<ProductoResponse> findByCategoria(String nombreCategoria) {
        return productoRepository.findAll().stream()
                .filter(p -> p.getCategoria().getNombre().equalsIgnoreCase(nombreCategoria))
                .map(p -> {
                    Inventario inv = inventarioRepository.findById(p.getIdProducto())
                            .orElse(new Inventario(p.getIdProducto(), 0));
                    return new ProductoResponse(p, inv.getCantDispo());
                })
                .collect(Collectors.toList());
    }


    public static class ProductoResponse {
        private final Long idProducto;
        private final String nombre;
        private final Categoria categoria;
        private final Double precio;
        private final String descripcion;
        private final String imagenUrl;
        private final Integer cantidad;
        private final String estado; // "Disponible" o "Agotado"

        public ProductoResponse(Producto p, int cantidad) {
            this.idProducto = p.getIdProducto();
            this.nombre = p.getNombre();
            this.categoria = p.getCategoria();
            this.precio = p.getPrecio();
            this.descripcion = p.getDescripcion();
            this.imagenUrl = p.getImagenUrl();
            this.cantidad = cantidad;
            this.estado = cantidad > 0 ? "Disponible" : "Agotado";
        }

        // --- Getters y Setters ---
        public Long getIdProducto() { return idProducto; }
        public String getNombre() { return nombre; }
        public Categoria getCategoria() { return categoria; }
        public Double getPrecio() { return precio; }
        public String getDescripcion() { return descripcion; }
        public String getImagenUrl() { return imagenUrl; }
        public Integer getCantidad() { return cantidad; }
        public String getEstado() { return estado; }
    }

    
}
