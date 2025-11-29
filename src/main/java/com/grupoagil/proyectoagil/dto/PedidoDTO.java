package com.grupoagil.proyectoagil.dto;

import java.util.List;

// DTO para recibir la solicitud de creación de pedido
public class PedidoDTO {
    
    private String idUser; // ID del usuario que hace el pedido
    private Long idMesa;   // ID de la mesa
    private List<DetalleRequest> detalles; // Lista de productos pedidos

    // Getters y Setters
    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }

    public List<DetalleRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleRequest> detalles) { this.detalles = detalles; }

    
    // Clase interna para representar cada detalle del pedido en la solicitud
    public static class DetalleRequest {
        private Long idProducto;
        private Integer cantidad;
        private String notas;

        // Getters y Setters
        public Long getIdProducto() { return idProducto; }
        public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public String getNotas() { return notas; }
        public void setNotas(String notas) { this.notas = notas; }
    }
}