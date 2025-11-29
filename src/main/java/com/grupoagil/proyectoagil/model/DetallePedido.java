package com.grupoagil.proyectoagil.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column; // Asegúrate de tener las importaciones correctas
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @EmbeddedId // <-- USA LA CLAVE COMPUESTA
    private DetallePedidoId id; // Contiene idPedido y idProducto

    // --- Relaciones ---
    // Mapea la parte 'idPedido' de la clave compuesta a la entidad Pedido
    @MapsId("idPedido") // Indica que este campo mapea 'idPedido' del @EmbeddedId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", insertable = false, updatable = false) // No duplicar columna
    @JsonBackReference
    private Pedido pedido;

    // Mapea la parte 'idProducto' de la clave compuesta a la entidad Producto
    @MapsId("idProducto") // Indica que este campo mapea 'idProducto' del @EmbeddedId
    @ManyToOne(fetch = FetchType.EAGER) // O LAZY si prefieres
    @JoinColumn(name = "id_producto", insertable = false, updatable = false) // No duplicar columna
    @JsonIgnoreProperties({"categoria", "descripcion", "imagenUrl"})
    private Producto producto;

    // --- Otros campos (igual que antes) ---
    @Column(name = "cant", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "nota", length = 50)
    private String notas;

    @Column(name = "es_nuevo", nullable = false)
    private Boolean esNuevo = false;

    @Transient
    private Double subtotal;

    // --- Constructores ---
    public DetallePedido() {}

    // --- Getters y Setters ---
    
    // Getter y Setter para la clave compuesta
    public DetallePedidoId getId() { return id; }
    public void setId(DetallePedidoId id) { this.id = id; }

    // Getters y Setters para las relaciones y otros campos (igual que antes)
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Boolean getEsNuevo() { return esNuevo; }
    public void setEsNuevo(Boolean esNuevo) { this.esNuevo = esNuevo; }
    public Double getSubtotal() { 
        if (this.cantidad != null && this.precioUnitario != null) {
            return this.cantidad * this.precioUnitario;
        }
        return 0.0;
    }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    // --- Método útil para establecer la clave compuesta al crear ---
    public void setPedidoProducto(Pedido pedido, Producto producto) {
        this.pedido = pedido;
        this.producto = producto;
        // Crea e inicializa el ID compuesto
        this.id = new DetallePedidoId(pedido.getIdPedido(), producto.getIdProducto());
    }
}