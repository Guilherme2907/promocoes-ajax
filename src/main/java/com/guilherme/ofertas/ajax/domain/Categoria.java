package com.guilherme.ofertas.ajax.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "categorias")
public class Categoria extends AbstractEntity<Long>{

    @Column(name = "titulo", nullable = false, unique = true)
    private String titulo;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Promocao> promocoes;


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Promocao> getPromocoes() {
        return promocoes;
    }

    public void setPromocoes(List<Promocao> promocoes) {
        this.promocoes = promocoes;
    }

    @Override
    public String toString() {
        return "Categoria [id=" + id + ", titulo=" + titulo + "]";
    }
}
