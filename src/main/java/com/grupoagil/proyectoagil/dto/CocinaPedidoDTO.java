package com.grupoagil.proyectoagil.dto;

import java.util.List;

/**
 * DTO para la vista de cocina
 * Incluye información de modificación para alertas visuales
 */
public class CocinaPedidoDTO {
    private Long idPedido;
    private String estado;
    private String tipoMesa;
    private Boolean modificado; // NUEVO: Indica si el pedido fue modificado
    private List<CocinaDetalleDTO> detalles;

    // Constructor vacío
    public CocinaPedidoDTO() {}

    // Constructor completo
    public CocinaPedidoDTO(Long idPedido, String estado, String tipoMesa, Boolean modificado, List<CocinaDetalleDTO> detalles) {
        this.idPedido = idPedido;
        this.estado = estado;
        this.tipoMesa = tipoMesa;
        this.modificado = modificado;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipoMesa() { return tipoMesa; }
    public void setTipoMesa(String tipoMesa) { this.tipoMesa = tipoMesa; }

    public Boolean getModificado() { return modificado; }
    public void setModificado(Boolean modificado) { this.modificado = modificado; }

    public List<CocinaDetalleDTO> getDetalles() { return detalles; }
    public void setDetalles(List<CocinaDetalleDTO> detalles) { this.detalles = detalles; }

    /**
     * Clase interna para los detalles
     * Incluye campo esNuevo para resaltar productos agregados
     */
    public static class CocinaDetalleDTO {
        private Long idProducto; // NUEVO: ID del producto para modificaciones
        private String nombreProducto;
        private Integer cantidad;
        private String notas;
        private Boolean esNuevo;

        public CocinaDetalleDTO() {}

        public CocinaDetalleDTO(Long idProducto, String nombreProducto, Integer cantidad, String notas, Boolean esNuevo) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.notas = notas;
            this.esNuevo = esNuevo;
        }

        // Getters y Setters
        public Long getIdProducto() { return idProducto; }
        public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public String getNotas() { return notas; }
        public void setNotas(String notas) { this.notas = notas; }

        public Boolean getEsNuevo() { return esNuevo; }
        public void setEsNuevo(Boolean esNuevo) { this.esNuevo = esNuevo; }
    }
}