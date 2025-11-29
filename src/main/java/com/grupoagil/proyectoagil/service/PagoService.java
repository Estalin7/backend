package com.grupoagil.proyectoagil.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupoagil.proyectoagil.dto.PagoHistorialDTO;
import com.grupoagil.proyectoagil.model.Comprobante;
import com.grupoagil.proyectoagil.model.MetodoPago;
import com.grupoagil.proyectoagil.model.Pago;
import com.grupoagil.proyectoagil.model.Pedido;
import com.grupoagil.proyectoagil.repository.PagoRepository;
import com.grupoagil.proyectoagil.repository.PedidoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PedidoService pedidoService;
        
    @Autowired
    private MetodoPagoService metodoPagoService;

    @Autowired
    private ComprobanteService comprobanteService; // NUEVO: Inyección del servicio de comprobante

    /**
     * Registrar pago con datos del cliente y generar comprobante automáticamente
     * @return Pago registrado (con comprobante generado)
     */
    @Transactional
    public Pago registrarPago(Long idPedido, Double montoRecibido, Long idMetodoPago,
                              String dni, String nombre, String apellido, String ruc, String razon) {
        
        // 1. Obtener el pedido
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + idPedido));

        // 2. Validar estado del pedido
        if (!"ATENDIDO".equalsIgnoreCase(pedido.getEstado())) {
            throw new RuntimeException("Solo se pueden pagar pedidos en estado 'ATENDIDO'. Estado actual: " + pedido.getEstado());
        }

        // 3. Calcular total
        Double totalAPagar = pedidoService.calcularTotalPedido(idPedido);

        // 4. Obtener método de pago
        MetodoPago metodoPago = metodoPagoService.getMetodoPagoById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + idMetodoPago));

        // 5. Validar monto recibido si es efectivo
        if ("efectivo".equalsIgnoreCase(metodoPago.getMetodo())) {
            if (montoRecibido == null || montoRecibido < totalAPagar) {
                throw new RuntimeException("El monto recibido (S/ " + String.format("%.2f", montoRecibido) + 
                                         ") es menor al total a pagar (S/ " + String.format("%.2f", totalAPagar) + ")");
            }
        }

        // 6. Validar datos del cliente
        validarDatosCliente(dni, nombre, apellido, ruc, razon);

        // 7. Crear y guardar el pago
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(metodoPago);
        pago.setPrecioFinal(BigDecimal.valueOf(totalAPagar));

        pago.setDni(dni);
        pago.setNombre(nombre);
        pago.setApellido(apellido);
        pago.setRuc(ruc);
        pago.setRazon(razon);

        Pago pagoGuardado = pagoRepository.save(pago);

        // 8. Actualizar estado del pedido a "PAGADO"
        pedido.setEstado("PAGADO");
        pedidoRepository.save(pedido);

        // 9. NUEVO: Generar comprobante automáticamente
        Comprobante comprobante = comprobanteService.generarComprobante(pagoGuardado);
        
        // Opcional: Puedes agregar el ID del comprobante al pago si necesitas retornarlo
        // Por ahora el comprobante se puede obtener por separado con GET /api/comprobantes/pago/{idPago}

        return pagoGuardado;
    }

    private void validarDatosCliente(String dni, String nombre, String apellido, String ruc, String razon) {
        boolean esNatural = dni != null && !dni.trim().isEmpty();
        boolean esJuridico = ruc != null && !ruc.trim().isEmpty();

        if (!esNatural && !esJuridico) {
            throw new RuntimeException("Debe proporcionar DNI (persona natural) o RUC (persona jurídica)");
        }

        if (esNatural && esJuridico) {
            throw new RuntimeException("No puede proporcionar DNI y RUC al mismo tiempo");
        }

        // Validar persona natural
        if (esNatural) {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new RuntimeException("El nombre es obligatorio para persona natural");
            }
            if (apellido == null || apellido.trim().isEmpty()) {
                throw new RuntimeException("El apellido es obligatorio para persona natural");
            }
            if (dni.length() != 8) {
                throw new RuntimeException("El DNI debe tener 8 dígitos");
            }
        }

        // Validar persona jurídica
        if (esJuridico) {
            if (razon == null || razon.trim().isEmpty()) {
                throw new RuntimeException("La razón social es obligatoria para persona jurídica");
            }
            if (ruc.length() != 11) {
                throw new RuntimeException("El RUC debe tener 11 dígitos");
            }
        }
    }

    public List<PagoHistorialDTO> obtenerHistorial(
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Long idMetodoPago) {

        List<Pago> pagos = pagoRepository.buscarHistorialFiltrado(
                fechaInicio, fechaFin, idMetodoPago
        );

        return pagos.stream().map(p -> new PagoHistorialDTO(
                p.getIdPago(),
                p.getFecha(),
                p.getPrecioFinal(),
                p.getNombreCliente(),
                p.getMetodoPago().getMetodo(),
                p.getPedido().getIdPedido()
        )).toList();
    }
}