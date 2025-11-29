package com.grupoagil.proyectoagil.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupoagil.proyectoagil.dto.CocinaPedidoDTO;
import com.grupoagil.proyectoagil.dto.ModificarPedidoDTO;
import com.grupoagil.proyectoagil.dto.PedidoDTO;
import com.grupoagil.proyectoagil.model.Pedido;
import com.grupoagil.proyectoagil.service.PedidoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
    
    // Crear un nuevo pedido
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        try {
            Pedido pedidoCreado = pedidoService.crearPedido(pedidoDTO);
            return new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener todos los pedidos (para administración)
    @GetMapping("/admin")
    public ResponseEntity<List<Pedido>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    // Obtener un pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener pedidos para la vista de cocina (Ordenado, EN_PREPARACION)
    @GetMapping("/cocina")
    public ResponseEntity<List<CocinaPedidoDTO>> obtenerPedidosCocina() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosCocina());
    }
    
    // Actualizar estado del pedido
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El estado no puede estar vacío"));
        }
        
        try {
            return pedidoService.actualizarEstado(id, estado.toUpperCase())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener pedidos ATENDIDOS para pagos (devuelve Pedido completo)
    @GetMapping("/parapago")
    public ResponseEntity<List<Pedido>> obtenerPedidosParaPago() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosParaPago());
    }

    // Obtener pedidos ATENDIDOS como DTO para meseros
    @GetMapping("/atendidos")
    public ResponseEntity<List<CocinaPedidoDTO>> obtenerPedidosAtendidos() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosAtendidos());
    }

    // Obtener pedidos ORDENADOS del mesero (para cancelar)
    @GetMapping("/mis-pedidos/{idUser}")
    public ResponseEntity<List<CocinaPedidoDTO>> obtenerMisPedidosOrdenados(@PathVariable String idUser) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosOrdenadosMesero(idUser));
    }

    // Obtener pedidos MODIFICABLES del mesero
    @GetMapping("/modificables/{idUser}")
    public ResponseEntity<?> obtenerPedidosModificables(@PathVariable String idUser) {
        try {
            return ResponseEntity.ok(pedidoService.obtenerPedidosModificablesMesero(idUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // MODIFICAR PEDIDO
    @PutMapping("/{id}/modificar")
    public ResponseEntity<?> modificarPedido(
            @PathVariable Long id, 
            @RequestBody ModificarPedidoDTO modificacionDTO) {
        try {
            Pedido pedidoModificado = pedidoService.modificarPedido(id, modificacionDTO);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido #" + id + " modificado exitosamente",
                "idPedido", pedidoModificado.getIdPedido(),
                "estado", pedidoModificado.getEstado(),
                "modificado", pedidoModificado.getModificado()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Cancelar pedido
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id, @RequestParam String idUser) {
        try {
            Pedido pedidoCancelado = pedidoService.cancelarPedido(id, idUser);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido #" + id + " cancelado exitosamente",
                "pedido", pedidoCancelado.getIdPedido(),
                "estado", pedidoCancelado.getEstado()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}