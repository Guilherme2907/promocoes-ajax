package com.guilherme.ofertas.ajax.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guilherme.ofertas.ajax.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
