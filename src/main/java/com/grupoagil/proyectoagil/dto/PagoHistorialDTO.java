package com.grupoagil.proyectoagil.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoHistorialDTO {

    private final Long idPago;
    private final LocalDateTime fecha;
    private final BigDecimal precioFinal;
    private final String cliente;
    private final String metodoPago;
    private final Long idPedido;

    public PagoHistorialDTO(Long idPago, LocalDateTime fecha, BigDecimal precioFinal,
                            String cliente, String metodoPago, Long idPedido) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.precioFinal = precioFinal;
        this.cliente = cliente;
        this.metodoPago = metodoPago;
        this.idPedido = idPedido;
    }

    public Long getIdPago() { return idPago; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getPrecioFinal() { return precioFinal; }
    public String getCliente() { return cliente; }
    public String getMetodoPago() { return metodoPago; }
    public Long getIdPedido() { return idPedido; }
}
