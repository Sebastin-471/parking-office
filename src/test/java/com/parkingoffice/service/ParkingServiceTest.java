package com.parkingoffice.service;

import com.parkingoffice.exception.NoActiveRateException;
import com.parkingoffice.exception.VehicleAlreadyParkedException;
import com.parkingoffice.model.Movimiento;
import com.parkingoffice.model.Tarifa;
import com.parkingoffice.model.TipoVehiculo;
import com.parkingoffice.model.Usuario;
import com.parkingoffice.model.Vehiculo;
import com.parkingoffice.repository.MovimientoRepository;
import com.parkingoffice.repository.TarifaRepository;
import com.parkingoffice.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    // Note: Due to standard structure where ParkingService initializes its own repos in constructor,
    // we would normally use dependency injection. For now, since it creates them with `new`,
    // testing it via Mockito @InjectMocks might require reflection or refactoring ParkingService
    // to accept repositories via constructor. 
    // Assuming ParkingService can be tested by injecting these mocks if we refactored it, 
    // or we are just testing the logic structure.
    
    // As a best practice, in Sprint 2 we will refactor ParkingService to use constructor injection.
    
    @Test
    public void testRegistrarEntrada_Success() {
        // This test will be fully implemented when constructor injection is added in Sprint 2
        assertTrue(true, "Setup for JUnit 5 complete");
    }
}
