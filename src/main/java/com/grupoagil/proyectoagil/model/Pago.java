package com.grupoagil.proyectoagil.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "precio_final", nullable = false, precision=5, scale =2)
    private BigDecimal precioFinal; // El total pagado

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "apellido", length = 45)
    private String apellido;

    @Column(name = "ruc", length = 11)
    private String ruc;

    @Column(name = "razon", length = 255)
    private String razon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    public Pago() {}

    public Pago(Long idPago, LocalDateTime fecha, BigDecimal precioFinal, String dni, String nombre, String apellido, String ruc, String razon, Pedido pedido, MetodoPago metodoPago) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.precioFinal = precioFinal;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.ruc = ruc;
        this.razon = razon;
        this.pedido = pedido;
        this.metodoPago = metodoPago;
    }

    public Long getIdPago() { return idPago; }
    public void setIdPago(Long idPago) { this.idPago = idPago; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public BigDecimal getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(BigDecimal precioFinal) { this.precioFinal = precioFinal; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getRazon() { return razon; }
    public void setRazon(String razon) { this.razon = razon; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public String getNombreCliente() {
        if (dni != null && nombre != null && apellido != null) {
            return nombre + " " + apellido;
        } else if (ruc != null && razon != null) {
            return razon;
        }
        return "Cliente sin nombre";
    }
}