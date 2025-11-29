package com.grupoagil.proyectoagil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupoagil.proyectoagil.model.Comprobante;
import com.grupoagil.proyectoagil.model.Pago;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    
    // Buscar comprobante por pago
    Optional<Comprobante> findByPago(Pago pago);
    
    // Buscar comprobante por ID de pago
    Optional<Comprobante> findByPagoIdPago(Long idPago);
}