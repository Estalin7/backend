package com.grupoagil.proyectoagil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupoagil.proyectoagil.model.Mesa;
import com.grupoagil.proyectoagil.model.Pedido;
import com.grupoagil.proyectoagil.model.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Busca pedidos por estados (para la vista de cocina)
    List<Pedido> findByEstadoIn(List<String> estados);

    List<Pedido> findByMesaAndEstadoNotIn(Mesa mesa, List<String> estadosFinales);

    List<Pedido> findByUsuarioAndEstadoInOrderByIdPedidoDesc(Usuario usuario, List<String> estados);
}
