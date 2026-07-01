package com.parkingoffice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifas")
public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_vehiculo_id", nullable = false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "monto_por_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPorHora;

    @Column(name = "monto_por_minuto", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPorMinuto;

    @Column(name = "activa")
    private Boolean activa = true;

    public Tarifa() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public TipoVehiculo getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public BigDecimal getMontoPorHora() { return montoPorHora; }
    public void setMontoPorHora(BigDecimal montoPorHora) { this.montoPorHora = montoPorHora; }

    public BigDecimal getMontoPorMinuto() { return montoPorMinuto; }
    public void setMontoPorMinuto(BigDecimal montoPorMinuto) { this.montoPorMinuto = montoPorMinuto; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}
