package com.parkingoffice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_ingreso_id", nullable = false)
    private Usuario usuarioIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_salida_id")
    private Usuario usuarioSalida;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tarifa_aplicada_id", nullable = false)
    private Tarifa tarifaAplicada;

    @Column(name = "total_pagar", precision = 10, scale = 2)
    private BigDecimal totalPagar;

    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO"; // ACTIVO, FINALIZADO

    @Version
    @Column(name = "version")
    private Long version;

    public Movimiento() {}

    // Getters and Setters
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }


    public Usuario getUsuarioIngreso() { return usuarioIngreso; }
    public void setUsuarioIngreso(Usuario usuarioIngreso) { this.usuarioIngreso = usuarioIngreso; }

    public Usuario getUsuarioSalida() { return usuarioSalida; }
    public void setUsuarioSalida(Usuario usuarioSalida) { this.usuarioSalida = usuarioSalida; }

    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }

    public Tarifa getTarifaAplicada() { return tarifaAplicada; }
    public void setTarifaAplicada(Tarifa tarifaAplicada) { this.tarifaAplicada = tarifaAplicada; }

    public BigDecimal getTotalPagar() { return totalPagar; }
    public void setTotalPagar(BigDecimal totalPagar) { this.totalPagar = totalPagar; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
