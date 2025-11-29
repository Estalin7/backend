package com.grupoagil.proyectoagil.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para devolver información completa del comprobante
 * Incluye datos del pedido, cliente y pago para impresión
 */
public class ComprobanteDTO {
    
    // Datos del comprobante
    private Long idComprobante;
    private String tipoComprobante; // "BOLETA" o "FACTURA"
    private LocalDateTime fechaEmision;
    private Double total;
    
    // Datos del cliente
    private String dni;
    private String nombre;
    private String apellido;
    private String ruc;
    private String razonSocial;
    
    // Datos del pedido
    private Long idPedido;
    private String tipoPedido; // "Para Mesa" o "Para Llevar"
    private String mesa;
    private List<DetalleItemDTO> items;
    
    // Datos del pago
    private Long idPago;
    private String metodoPago;
    private Double subtotal;
    private Double igv;
    
    // Constructor vacío
    public ComprobanteDTO() {}

    // Getters y Setters
    public Long getIdComprobante() { return idComprobante; }
    public void setIdComprobante(Long idComprobante) { this.idComprobante = idComprobante; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getTipoPedido() { return tipoPedido; }
    public void setTipoPedido(String tipoPedido) { this.tipoPedido = tipoPedido; }

    public String getMesa() { return mesa; }
    public void setMesa(String mesa) { this.mesa = mesa; }

    public List<DetalleItemDTO> getItems() { return items; }
    public void setItems(List<DetalleItemDTO> items) { this.items = items; }

    public Long getIdPago() { return idPago; }
    public void setIdPago(Long idPago) { this.idPago = idPago; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getIgv() { return igv; }
    public void setIgv(Double igv) { this.igv = igv; }

    /**
     * Clase interna para los items del pedido
     */
    public static class DetalleItemDTO {
        private String nombreProducto;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;

        public DetalleItemDTO() {}

        public DetalleItemDTO(String nombreProducto, Integer cantidad, Double precioUnitario) {
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = cantidad * precioUnitario;
        }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public Double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    }
}