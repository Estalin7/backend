package com.grupoagil.proyectoagil.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupoagil.proyectoagil.dto.ComprobanteDTO;
import com.grupoagil.proyectoagil.model.Comprobante;
import com.grupoagil.proyectoagil.model.Pago;
import com.grupoagil.proyectoagil.model.Pedido;
import com.grupoagil.proyectoagil.service.ComprobanteService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteController {

    @Autowired
    private ComprobanteService comprobanteService;

    /**
     * Obtener comprobante por ID para impresión
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerComprobante(@PathVariable Long id) {
        try {
            Comprobante comprobante = comprobanteService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
            
            ComprobanteDTO dto = convertirADTO(comprobante);
            return ResponseEntity.ok(dto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener comprobante por ID de pago
     */
    @GetMapping("/pago/{idPago}")
    public ResponseEntity<?> obtenerComprobantePorPago(@PathVariable Long idPago) {
        try {
            Comprobante comprobante = comprobanteService.obtenerPorIdPago(idPago)
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado para el pago: " + idPago));
            
            ComprobanteDTO dto = convertirADTO(comprobante);
            return ResponseEntity.ok(dto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Método auxiliar para convertir Comprobante a DTO
     */
    private ComprobanteDTO convertirADTO(Comprobante comprobante) {
        ComprobanteDTO dto = new ComprobanteDTO();
        Pago pago = comprobante.getPago();
        Pedido pedido = pago.getPedido();

        // Datos del comprobante
        dto.setIdComprobante(comprobante.getIdComprobante());
        dto.setTipoComprobante(comprobante.getTipoComprobante());
        dto.setFechaEmision(comprobante.getFechaEmision());
        dto.setTotal(comprobante.getTotal());

        // Datos del cliente
        dto.setDni(pago.getDni());
        dto.setNombre(pago.getNombre());
        dto.setApellido(pago.getApellido());
        dto.setRuc(pago.getRuc());
        dto.setRazonSocial(pago.getRazon());

        // Datos del pedido
        dto.setIdPedido(pedido.getIdPedido());
        dto.setTipoPedido(pedido.getTipo());
        
        if (pedido.getMesa() != null) {
            dto.setMesa(pedido.getMesa().getIdMesa() == 0 ? "Para Llevar" : "Mesa " + pedido.getMesa().getIdMesa());
        }

        // Items del pedido
        dto.setItems(pedido.getDetalles().stream()
                .map(detalle -> new ComprobanteDTO.DetalleItemDTO(
                        detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto",
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario()
                ))
                .collect(Collectors.toList()));

        // Datos del pago
        dto.setIdPago(pago.getIdPago());
        dto.setMetodoPago(pago.getMetodoPago() != null ? pago.getMetodoPago().getMetodo() : "Efectivo");

        // Calcular subtotal e IGV
        Double subtotal = comprobante.getTotal() / 1.18;
        dto.setSubtotal(Math.round(subtotal * 100.0) / 100.0);
        dto.setIgv(Math.round((comprobante.getTotal() - subtotal) * 100.0) / 100.0);

        return dto;
    }
}