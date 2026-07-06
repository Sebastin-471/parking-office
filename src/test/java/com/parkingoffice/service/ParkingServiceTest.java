package com.parkingoffice.service;

import com.parkingoffice.exception.NoActiveRateException;
import com.parkingoffice.exception.ParkingFullException;
import com.parkingoffice.exception.ParkingException;
import com.parkingoffice.exception.VehicleAlreadyParkedException;
import com.parkingoffice.model.EspacioConfig;
import com.parkingoffice.model.Movimiento;
import com.parkingoffice.model.Tarifa;
import com.parkingoffice.model.TipoVehiculo;
import com.parkingoffice.model.Usuario;
import com.parkingoffice.model.Vehiculo;
import com.parkingoffice.repository.EspacioConfigRepository;
import com.parkingoffice.repository.MovimientoRepository;
import com.parkingoffice.repository.TarifaRepository;
import com.parkingoffice.repository.VehiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    MovimientoRepository movimientoRepository;
    @Mock
    VehiculoRepository vehiculoRepository;
    @Mock
    TarifaRepository tarifaRepository;
    @Mock
    EspacioConfigRepository espacioConfigRepository;

    @InjectMocks
    ParkingService parkingService;

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(1);
        u.setUsername("operator");
        return u;
    }

    private TipoVehiculo tipoVehiculo() {
        TipoVehiculo t = new TipoVehiculo();
        t.setId(1);
        t.setNombre("AUTO");
        return t;
    }

    private Tarifa tarifa(TipoVehiculo tipo) {
        Tarifa t = new Tarifa();
        t.setId(1);
        t.setTipoVehiculo(tipo);
        t.setMontoPorHora(new BigDecimal("10.00"));
        t.setMontoPorMinuto(new BigDecimal("0.50"));
        t.setActiva(true);
        return t;
    }

    @Test
    void registrarEntrada_success() throws ParkingException {
        TipoVehiculo tipo = tipoVehiculo();
        Usuario usuario = usuario();
        EspacioConfig config = new EspacioConfig(5);
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setTipoVehiculo(tipo);

        when(espacioConfigRepository.findActivo()).thenReturn(config);
        when(movimientoRepository.findActivos()).thenReturn(Collections.emptyList());
        when(vehiculoRepository.findByPlaca("ABC-123")).thenReturn(null);
        when(tarifaRepository.findActivaByTipoVehiculo(1)).thenReturn(tarifa(tipo));
        doNothing().when(vehiculoRepository).save(any(Vehiculo.class));
        doNothing().when(movimientoRepository).save(any(Movimiento.class));

        Movimiento mov = parkingService.registrarEntrada("ABC-123", tipo, usuario);

        assertNotNull(mov);
        assertEquals("ACTIVO", mov.getEstado());
        assertEquals(tarifa(tipo).getMontoPorHora(), mov.getTarifaAplicada().getMontoPorHora());
        verify(vehiculoRepository).save(any(Vehiculo.class));
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void registrarEntrada_vehicleAlreadyParked_throwsException() {
        TipoVehiculo tipo = tipoVehiculo();
        Usuario usuario = usuario();
        EspacioConfig config = new EspacioConfig(5);
        Vehiculo existing = new Vehiculo();
        existing.setId(1);
        existing.setPlaca("ABC-123");
        existing.setTipoVehiculo(tipo);

        when(espacioConfigRepository.findActivo()).thenReturn(config);
        when(movimientoRepository.findActivos()).thenReturn(Collections.emptyList());
        when(vehiculoRepository.findByPlaca("ABC-123")).thenReturn(existing);
        when(movimientoRepository.findActivoByVehiculo(1)).thenReturn(new Movimiento());

        assertThrows(VehicleAlreadyParkedException.class,
                () -> parkingService.registrarEntrada("ABC-123", tipo, usuario));
    }

    @Test
    void registrarEntrada_parkingFull_throwsException() {
        TipoVehiculo tipo = tipoVehiculo();
        Usuario usuario = usuario();
        EspacioConfig config = new EspacioConfig(5);
        EspacioConfig fullConfig = new EspacioConfig(5);
        EspacioConfig configNull = new EspacioConfig(0);

        when(espacioConfigRepository.findActivo())
                .thenReturn(config)
                .thenReturn(fullConfig)
                .thenReturn(configNull);

        when(movimientoRepository.findActivos()).thenReturn(
                List.of(new Movimiento(), new Movimiento(), new Movimiento(), new Movimiento(), new Movimiento()));

        assertThrows(ParkingFullException.class, () -> parkingService.registrarEntrada("ABC-123", tipo, usuario));
    }

    @Test
    void registrarEntrada_noActiveRate_throwsException() {
        TipoVehiculo tipo = tipoVehiculo();
        Usuario usuario = usuario();
        EspacioConfig config = new EspacioConfig(5);
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setTipoVehiculo(tipo);

        when(espacioConfigRepository.findActivo()).thenReturn(config);
        when(movimientoRepository.findActivos()).thenReturn(Collections.emptyList());
        when(vehiculoRepository.findByPlaca("ABC-123")).thenReturn(null);
        when(tarifaRepository.findActivaByTipoVehiculo(1)).thenReturn(null);

        assertThrows(NoActiveRateException.class, () -> parkingService.registrarEntrada("ABC-123", tipo, usuario));
    }

    @Test
    void registrarSalida_success() throws ParkingException {
        TipoVehiculo tipo = tipoVehiculo();
        Usuario usuario = usuario();
        Tarifa t = tarifa(tipo);
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setTipoVehiculo(tipo);

        Movimiento mov = new Movimiento();
        mov.setId(1);
        mov.setVehiculo(vehiculo);
        mov.setUsuarioIngreso(usuario);
        mov.setFechaIngreso(LocalDateTime.now().minusHours(2));
        mov.setTarifaAplicada(t);
        mov.setEstado("ACTIVO");

        Movimiento result = parkingService.registrarSalida(mov, usuario);

        assertEquals("FINALIZADO", result.getEstado());
        assertNotNull(result.getFechaSalida());
        assertNotNull(result.getTotalPagar());
        assertTrue(result.getTotalPagar().compareTo(BigDecimal.ZERO) > 0);
        verify(movimientoRepository).update(result);
    }

    @Test
    void registrarSalida_movementAlreadyFinalized_throwsException() {
        TipoVehiculo tipo = tipoVehiculo();
        Usuario usuario = usuario();
        Tarifa t = tarifa(tipo);
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1);
        vehiculo.setPlaca("ABC-123");

        Movimiento mov = new Movimiento();
        mov.setId(1);
        mov.setVehiculo(vehiculo);
        mov.setTarifaAplicada(t);
        mov.setEstado("FINALIZADO");

        assertThrows(com.parkingoffice.exception.ParkingException.class,
                () -> parkingService.registrarSalida(mov, usuario));
    }

    @Test
    void calcularMonto_basicCalculation() throws Exception {
        java.lang.reflect.Method method = ParkingService.class.getDeclaredMethod("calcularMonto", long.class,
                Tarifa.class);
        method.setAccessible(true);

        TipoVehiculo tipo = tipoVehiculo();
        Tarifa t = tarifa(tipo);

        BigDecimal result = (BigDecimal) method.invoke(parkingService, 90, t);
        assertEquals(new BigDecimal("25.00"), result);
    }

    @Test
    void calcularMonto_zeroMinutes() throws Exception {
        java.lang.reflect.Method method = ParkingService.class.getDeclaredMethod("calcularMonto", long.class,
                Tarifa.class);
        method.setAccessible(true);

        TipoVehiculo tipo = tipoVehiculo();
        Tarifa t = tarifa(tipo);

        BigDecimal result = (BigDecimal) method.invoke(parkingService, 0, t);
        assertEquals(new BigDecimal("0.00"), result);
    }
}