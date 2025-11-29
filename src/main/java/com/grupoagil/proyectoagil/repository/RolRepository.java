package com.grupoagil.proyectoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupoagil.proyectoagil.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
}