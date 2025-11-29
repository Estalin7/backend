package com.grupoagil.proyectoagil.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comprobante")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long idComprobante;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pago", nullable = false, unique = true)
    private Pago pago;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Column(name = "total", nullable = false)
    private Double total;

    // Constructor vacío
    public Comprobante() {}

    // Constructor con parámetros
    public Comprobante(Pago pago, Double total) {
        this.pago = pago;
        this.total = total;
        this.fechaEmision = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdComprobante() { return idComprobante; }
    public void setIdComprobante(Long idComprobante) { this.idComprobante = idComprobante; }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    // Método auxiliar para determinar tipo de comprobante
    public String getTipoComprobante() {
        if (pago != null) {
            // Si tiene RUC es Factura, si tiene DNI es Boleta
            if (pago.getRuc() != null && !pago.getRuc().isEmpty()) {
                return "FACTURA";
            }
        }
        return "BOLETA";
    }
}