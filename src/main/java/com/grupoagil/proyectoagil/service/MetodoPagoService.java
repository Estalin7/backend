package com.grupoagil.proyectoagil.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupoagil.proyectoagil.model.MetodoPago;
import com.grupoagil.proyectoagil.repository.MetodoPagoRepository;

@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    public List<MetodoPago> getAllMetodosPago() {
        return metodoPagoRepository.findAll();
    }

    public Optional<MetodoPago> getMetodoPagoById(Long id) {
        return metodoPagoRepository.findById(id);
    }
    public MetodoPago getMetodoPagoByNombre(String metodo) {
        return metodoPagoRepository.findByMetodo(metodo)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + metodo));
    }
}