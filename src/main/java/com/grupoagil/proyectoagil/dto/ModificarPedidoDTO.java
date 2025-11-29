package com.grupoagil.proyectoagil.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para modificar un pedido existente.
 * Permite:
 *  - Agregar productos
 *  - Quitar productos (solo si estado = ORDENADO)
 *  - Actualizar cantidad de productos existentes (AUMENTAR o REDUCIR)
 */
public class ModificarPedidoDTO {
    
    private String idUser; 
    
    private List<ProductoModificacion> productosAgregar = new ArrayList<>();
    private List<ProductoModificacion> productosQuitar = new ArrayList<>();
    
    // ⭐ Nuevo: permite actualizar la cantidad de productos existentes
    private List<ProductoModificacion> productosActualizar = new ArrayList<>();

    // Constructor vacío
    public ModificarPedidoDTO() {}

    // Getters y Setters
    public String getIdUser() { 
        return idUser; 
    }

    public void setIdUser(String idUser) { 
        this.idUser = idUser; 
    }

    public List<ProductoModificacion> getProductosAgregar() { 
        return productosAgregar; 
    }

    public void setProductosAgregar(List<ProductoModificacion> productosAgregar) { 
        this.productosAgregar = productosAgregar; 
    }

    public List<ProductoModificacion> getProductosQuitar() { 
        return productosQuitar; 
    }

    public void setProductosQuitar(List<ProductoModificacion> productosQuitar) { 
        this.productosQuitar = productosQuitar; 
    }

    public List<ProductoModificacion> getProductosActualizar() {
        return productosActualizar;
    }

    public void setProductosActualizar(List<ProductoModificacion> productosActualizar) {
        this.productosActualizar = productosActualizar;
    }

    /**
     * Representa una modificación de producto:
     * - idProducto → ID del producto
     * - cantidad → para agregar/quitar o nuevaCantidad si es actualizar
     * - notas → para productos agregados
     */
    public static class ProductoModificacion {

        private Long idProducto;
        private Integer cantidad; 
        private String notas;

        // Constructor vacío
        public ProductoModificacion() {}

        // Constructor con parámetros
        public ProductoModificacion(Long idProducto, Integer cantidad, String notas) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
            this.notas = notas;
        }

        public Long getIdProducto() { 
            return idProducto; 
        }

        public void setIdProducto(Long idProducto) { 
            this.idProducto = idProducto; 
        }

        public Integer getCantidad() { 
            return cantidad; 
        }

        public void setCantidad(Integer cantidad) { 
            this.cantidad = cantidad; 
        }

        public String getNotas() { 
            return notas; 
        }

        public void setNotas(String notas) { 
            this.notas = notas; 
        }
    }
}
