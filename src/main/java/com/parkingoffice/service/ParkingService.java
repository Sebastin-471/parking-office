package com.parkingoffice.service;

import com.parkingoffice.model.*;
import com.parkingoffice.repository.*;

import com.parkingoffice.exception.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingService {

    private final MovimientoRepository movimientoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final TarifaRepository tarifaRepository;
    private final EspacioConfigRepository espacioConfigRepository;

    public ParkingService() {
        this.movimientoRepository = new MovimientoRepository();
        this.vehiculoRepository = new VehiculoRepository();
        this.tarifaRepository = new TarifaRepository();
        this.espacioConfigRepository = new EspacioConfigRepository();
    }

    public int getEspaciosDisponibles() {
        EspacioConfig config = espacioConfigRepository.findActivo();
        int capacidad = (config != null) ? config.getCapacidadMaxima() : 0;
        int ocupados = movimientoRepository.findActivos().size();
        return Math.max(0, capacidad - ocupados);
    }

    public int getCapacidadMaxima() {
        EspacioConfig config = espacioConfigRepository.findActivo();
        return (config != null) ? config.getCapacidadMaxima() : 0;
    }

    public boolean verificarEspacioDisponible() {
        EspacioConfig config = espacioConfigRepository.findActivo();
        if (config == null || config.getCapacidadMaxima() <= 0) {
            return true;
        }
        int ocupados = movimientoRepository.findActivos().size();
        return ocupados < config.getCapacidadMaxima();
    }

    public Movimiento registrarEntrada(String placa, TipoVehiculo tipoVehiculo, Usuario usuario) throws ParkingException {
        if (!verificarEspacioDisponible()) {
            throw new ParkingFullException("El estacionamiento está lleno. No hay espacios disponibles.");
        }

        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa);
        if (vehiculo == null) {
            vehiculo = new Vehiculo();
            vehiculo.setPlaca(placa);
            vehiculo.setTipoVehiculo(tipoVehiculo);
            vehiculoRepository.save(vehiculo);
        } else if (movimientoRepository.findActivoByVehiculo(vehiculo.getId()) != null) {
            throw new VehicleAlreadyParkedException("El vehículo ya se encuentra en el estacionamiento.");
        }

        Tarifa tarifa = tarifaRepository.findActivaByTipoVehiculo(tipoVehiculo.getId());
        if (tarifa == null) {
            throw new NoActiveRateException("No hay tarifa activa para este tipo de vehículo.");
        }

        Movimiento mov = new Movimiento();
        mov.setVehiculo(vehiculo);
        mov.setUsuarioIngreso(usuario);
        mov.setFechaIngreso(LocalDateTime.now());
        mov.setTarifaAplicada(tarifa);
        mov.setEstado("ACTIVO");

        movimientoRepository.save(mov);
        return mov;
    }

    public Movimiento registrarSalida(Movimiento movimiento, Usuario usuario) throws ParkingException {
        if (!"ACTIVO".equals(movimiento.getEstado())) {
            throw new ParkingException("El movimiento ya está finalizado.");
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
