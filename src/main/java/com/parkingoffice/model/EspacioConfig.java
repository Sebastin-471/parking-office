package com.parkingoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "espacio_config")
public class EspacioConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima = 0;

    @Column(name = "activo")
    private Boolean activo = true;

    public EspacioConfig() {}

    public EspacioConfig(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.activo = true;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}