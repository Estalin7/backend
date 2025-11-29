package com.grupoagil.proyectoagil.model;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido") // Coincide con la BD
    private Long idPedido;

    @Column(name = "tipo", nullable = false, length = 12) // 
    private String tipo;

    @Column(name = "fecha") // Coincide con la BD (en tu modelo decía "Fecha")
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "estado", nullable = false, length = 50)
    private String estado = "Ordenado";

    @Column(name = "modificado", nullable = false)
    private Boolean modificado = false;
   
    // --- CORRECCIÓN IMPORTANTE ---
    // Debe ser una relación @ManyToOne con la entidad Mesa
    @ManyToOne(fetch = FetchType.EAGER) // EAGER o LAZY dependiendo de tu necesidad
    @JoinColumn(name = "id_mesa", nullable = false) // Coincide con la FK en la BD
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Mesa mesa;
    
    // --- CORRECCIÓN IMPORTANTE ---
    // Debe ser una relación @ManyToOne con la entidad Usuario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", nullable = false) // Coincide con la FK en la BD
    private Usuario usuario;

    // Relación con DetallePedido
    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;

    // Constructores
    public Pedido() {}

    // Getters y Setters
    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getTipo() { return tipo; } 
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Boolean getModificado() { return modificado; }
    public void setModificado(Boolean modificado) { this.modificado = modificado; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
}