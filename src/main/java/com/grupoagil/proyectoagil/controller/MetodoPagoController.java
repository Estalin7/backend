package com.grupoagil.proyectoagil.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupoagil.proyectoagil.model.MetodoPago;
import com.grupoagil.proyectoagil.service.MetodoPagoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    /**
     * Obtener todos los métodos de pago disponibles
     * Para mostrar en el select del frontend
     */
    @GetMapping
    public ResponseEntity<List<MetodoPago>> getAllMetodosPago() {
        return ResponseEntity.ok(metodoPagoService.getAllMetodosPago());
    }
}