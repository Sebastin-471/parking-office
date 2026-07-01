package com.parkingoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "espacios")
public class Espacio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "ocupado")
    private Boolean ocupado = false;

    public Espacio() {}

    public Espacio(String codigo) {
        this.codigo = codigo;
        this.ocupado = false;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Boolean getOcupado() { return ocupado; }
    public void setOcupado(Boolean ocupado) { this.ocupado = ocupado; }
}
