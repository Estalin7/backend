package com.grupoagil.proyectoagil.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupoagil.proyectoagil.model.Pago;
import com.grupoagil.proyectoagil.model.Pedido;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
        Optional<Pago> findByPedido(Pedido pedido);
        @Query("""
        SELECT p FROM Pago p
        WHERE (:inicio IS NULL OR p.fecha >= :inicio)
        AND (:fin IS NULL OR p.fecha <= :fin)
        AND (:idMetodo IS NULL OR p.metodoPago.idMetodoPago = :idMetodo)
        ORDER BY p.fecha DESC
        """)
        List<Pago> buscarHistorialFiltrado(
                @Param("inicio") LocalDateTime inicio,
                @Param("fin") LocalDateTime fin,
                @Param("idMetodo") Long idMetodo
        );
}