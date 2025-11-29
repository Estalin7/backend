package com.grupoagil.proyectoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupoagil.proyectoagil.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    // Aquí puedes agregar métodos adicionales si es necesario, como búsqueda por estado
}