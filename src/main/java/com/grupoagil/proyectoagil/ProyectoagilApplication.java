package com.grupoagil.proyectoagil;

import java.util.TimeZone; // <--- IMPORTAR ESTO
import jakarta.annotation.PostConstruct; // <--- IMPORTAR ESTO

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProyectoagilApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoagilApplication.class, args);
	}

    // ========== AGREGAR ESTE BLOQUE ==========
	@PostConstruct
	public void init() {
		// Establecer la zona horaria por defecto a Lima (UTC-5)
		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
	}
    // =========================================
}
