package com.grupoagil.proyectoagil.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable // Indica que esta clase se puede "empotrar" en otra entidad
public class DetallePedidoId implements Serializable {

    @Column(name = "id_pedido") // Nombre de la columna en la BD
    private Long idPedido;

    @Column(name = "id_producto") // Nombre de la columna en la BD
    private Long idProducto;

    // Constructor vacío (requerido por JPA)
    public DetallePedidoId() {}

    // Constructor con parámetros
    public DetallePedidoId(Long idPedido, Long idProducto) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
    }

    // Getters y Setters (necesarios)
    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    // Métodos equals() y hashCode() (¡Esenciales para claves compuestas!)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetallePedidoId that = (DetallePedidoId) o;
        return Objects.equals(idPedido, that.idPedido) &&
               Objects.equals(idProducto, that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido, idProducto);
    }
}