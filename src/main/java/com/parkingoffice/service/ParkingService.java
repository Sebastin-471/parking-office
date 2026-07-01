package com.parkingoffice.service;

import com.parkingoffice.model.*;
import com.parkingoffice.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingService {

    private final MovimientoRepository movimientoRepository;
    private final EspacioRepository espacioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final TarifaRepository tarifaRepository;

    public ParkingService() {
        this.movimientoRepository = new MovimientoRepository();
        this.espacioRepository = new EspacioRepository();
        this.vehiculoRepository = new VehiculoRepository();
        this.tarifaRepository = new TarifaRepository();
    }

    public Movimiento registrarEntrada(String placa, TipoVehiculo tipoVehiculo, Espacio espacio, Usuario usuario) throws Exception {
        // Find or create vehiculo
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa);
        if (vehiculo == null) {
            vehiculo = new Vehiculo();
            vehiculo.setPlaca(placa);
            vehiculo.setTipoVehiculo(tipoVehiculo);
            vehiculoRepository.save(vehiculo);
        } else if (movimientoRepository.findActivoByVehiculo(vehiculo.getId()) != null) {
            throw new Exception("El vehículo ya se encuentra en el estacionamiento.");
        }

        if (espacio.getOcupado()) {
            throw new Exception("El espacio seleccionado ya está ocupado.");
        }

        Tarifa tarifa = tarifaRepository.findActivaByTipoVehiculo(tipoVehiculo.getId());
        if (tarifa == null) {
            throw new Exception("No hay tarifa activa para este tipo de vehículo.");
        }

        espacio.setOcupado(true);
        espacioRepository.update(espacio);

        Movimiento mov = new Movimiento();
        mov.setVehiculo(vehiculo);
        mov.setEspacio(espacio);
        mov.setUsuarioIngreso(usuario);
        mov.setFechaIngreso(LocalDateTime.now());
        mov.setTarifaAplicada(tarifa);
        mov.setEstado("ACTIVO");

        movimientoRepository.save(mov);
        return mov;
    }

    public Movimiento registrarSalida(Movimiento movimiento, Usuario usuario) throws Exception {
        if (!"ACTIVO".equals(movimiento.getEstado())) {
            throw new Exception("El movimiento ya está finalizado.");
        }

        movimiento.setFechaSalida(LocalDateTime.now());
        movimiento.setUsuarioSalida(usuario);
        movimiento.setEstado("FINALIZADO");

        // Calculate time
        Duration duration = Duration.between(movimiento.getFechaIngreso(), movimiento.getFechaSalida());
        long minutes = duration.toMinutes();

        BigDecimal total = calcularMonto(minutes, movimiento.getTarifaAplicada());
        movimiento.setTotalPagar(total);

        movimientoRepository.update(movimiento);

        Espacio espacio = movimiento.getEspacio();
        espacio.setOcupado(false);
        espacioRepository.update(espacio);

        return movimiento;
    }

    private BigDecimal calcularMonto(long minutes, Tarifa tarifa) {
        if (minutes < 0) minutes = 0;
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        BigDecimal totalHours = tarifa.getMontoPorHora().multiply(BigDecimal.valueOf(hours));
        BigDecimal totalMinutes = tarifa.getMontoPorMinuto().multiply(BigDecimal.valueOf(remainingMinutes));

        return totalHours.add(totalMinutes).setScale(2, RoundingMode.HALF_UP);
    }
}
