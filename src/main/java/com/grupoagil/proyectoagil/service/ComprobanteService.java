package com.grupoagil.proyectoagil.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupoagil.proyectoagil.model.Comprobante;
import com.grupoagil.proyectoagil.model.Pago;
import com.grupoagil.proyectoagil.repository.ComprobanteRepository;

@Service
public class ComprobanteService {

    @Autowired
    private ComprobanteRepository comprobanteRepository;

    /**
     * Generar comprobante automáticamente al registrar un pago
     * @param pago El pago registrado
     * @return Comprobante generado
     */
    @Transactional
    public Comprobante generarComprobante(Pago pago) {
        // Verificar si ya existe un comprobante para este pago
        Optional<Comprobante> existente = comprobanteRepository.findByPago(pago);
        if (existente.isPresent()) {
            return existente.get();
        }

        // Crear nuevo comprobante
        Comprobante comprobante = new Comprobante();
        comprobante.setPago(pago);
        comprobante.setTotal(pago.getPrecioFinal().doubleValue());

        return comprobanteRepository.save(comprobante);
    }

    /**
     * Obtener comprobante por ID
     * @param idComprobante ID del comprobante
     * @return Comprobante encontrado
     */
    @Transactional(readOnly = true)
    public Optional<Comprobante> obtenerPorId(Long idComprobante) {
        return comprobanteRepository.findById(idComprobante);
    }

    /**
     * Obtener comprobante por ID de pago
     * @param idPago ID del pago
     * @return Comprobante encontrado
     */
    @Transactional(readOnly = true)
    public Optional<Comprobante> obtenerPorIdPago(Long idPago) {
        return comprobanteRepository.findByPagoIdPago(idPago);
    }
}