package com.grupoagil.proyectoagil.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupoagil.proyectoagil.dto.PagoHistorialDTO;
import com.grupoagil.proyectoagil.model.Comprobante;
import com.grupoagil.proyectoagil.model.Pago;
import com.grupoagil.proyectoagil.service.ComprobanteService;
import com.grupoagil.proyectoagil.service.PagoService;
import com.grupoagil.proyectoagil.service.PdfService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ComprobanteService comprobanteService;

    @Autowired
    private PdfService pdfService;

    /**
     * Registrar pago con datos de cliente incluidos
     * Retorna el ID del comprobante generado para poder emitirlo
     */
    @PostMapping
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequest request) {
        try {
            Pago pago = pagoService.registrarPago(
                    request.getIdPedido(),
                    request.getMontoRecibido(),
                    request.getIdMetodoPago(),
                    request.getDni(),
                    request.getNombre(),
                    request.getApellido(),
                    request.getRuc(),
                    request.getRazon()
            );

            // Obtener el comprobante generado automáticamente
            Comprobante comprobante = comprobanteService.obtenerPorIdPago(pago.getIdPago())
                    .orElse(null);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pago registrado exitosamente",
                    "idPago", pago.getIdPago(),
                    "precioFinal", pago.getPrecioFinal(),
                    "metodoPago", pago.getMetodoPago().getMetodo(),
                    "cliente", pago.getNombreCliente(),
                    "idComprobante", comprobante != null ? comprobante.getIdComprobante() : null,
                    "tipoComprobante", comprobante != null ? comprobante.getTipoComprobante() : "BOLETA"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al procesar el pago."));
        }
    }

    // ========== DTO INTERNO ==========
    
    public static class PagoRequest {
        private Long idPedido;
        private Double montoRecibido;
        private Long idMetodoPago;
        private String dni;
        private String nombre;
        private String apellido;
        private String ruc;
        private String razon;

        // Getters y Setters
        public Long getIdPedido() { return idPedido; }
        public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }
        
        public Double getMontoRecibido() { return montoRecibido; }
        public void setMontoRecibido(Double montoRecibido) { this.montoRecibido = montoRecibido; }
        
        public Long getIdMetodoPago() { return idMetodoPago; }
        public void setIdMetodoPago(Long idMetodoPago) { this.idMetodoPago = idMetodoPago; }
        
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
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String metodo) {

        try {
            LocalDateTime inicio = (fechaInicio != null && !fechaInicio.isEmpty()) 
                ? LocalDateTime.parse(fechaInicio + "T00:00:00") 
                : null;
            
            LocalDateTime fin = (fechaFin != null && !fechaFin.isEmpty()) 
                ? LocalDateTime.parse(fechaFin + "T23:59:59") 
                : null;

            // Convertir nombre de método a ID
            Long metodoPagoId = null;
            if (metodo != null && !metodo.isEmpty()) {
                metodoPagoId = "efectivo".equalsIgnoreCase(metodo) ? 1L : 2L;
            }

            List<PagoHistorialDTO> historial = pagoService.obtenerHistorial(
                    inicio, fin, metodoPagoId
            );

            BigDecimal totalAcumulado = historial.stream()
                    .map(PagoHistorialDTO::getPrecioFinal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return ResponseEntity.ok(Map.of(
                    "pagos", historial,
                    "total", totalAcumulado
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error procesando filtros: " + e.getMessage()));
        }
    }

    @GetMapping("/historial/pdf")
    public ResponseEntity<?> exportarHistorialPDF(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String metodo) {

        try {
            LocalDateTime inicio = (fechaInicio != null && !fechaInicio.isEmpty()) 
                ? LocalDateTime.parse(fechaInicio + "T00:00:00") 
                : null;
            
            LocalDateTime fin = (fechaFin != null && !fechaFin.isEmpty()) 
                ? LocalDateTime.parse(fechaFin + "T23:59:59") 
                : null;

            // Convertir nombre de método a ID
            Long metodoPagoId = null;
            if (metodo != null && !metodo.isEmpty()) {
                metodoPagoId = "efectivo".equalsIgnoreCase(metodo) ? 1L : 2L;
            }

            List<PagoHistorialDTO> historial = pagoService.obtenerHistorial(
                    inicio, fin, metodoPagoId
            );

            byte[] pdfBytes = pdfService.generarHistorialPagosPDF(historial);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=historial_pagos.pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}