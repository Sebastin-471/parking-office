package com.parkingoffice.repository;

import com.parkingoffice.model.Tarifa;
import com.parkingoffice.model.TipoVehiculo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TarifaRepositoryIT extends AbstractRepositoryIT {

    @Test
    void findActivaByTipoVehiculo_returnsNull_whenNone() {
        TarifaRepository repository = new TarifaRepository();
        Tarifa result = repository.findActivaByTipoVehiculo(1);
        assertNull(result);
    }

    @Test
    void saveAndFindActiva_works() {
        TarifaRepository repository = new TarifaRepository();
        TipoVehiculo tipo = new TipoVehiculo();
        tipo.setId(1);
        tipo.setNombre("AUTO");
        
        Tarifa tarifa = new Tarifa();
        tarifa.setTipoVehiculo(tipo);
        tarifa.setMontoPorHora(new BigDecimal("10.00"));
        tarifa.setMontoPorMinuto(new BigDecimal("0.50"));
        tarifa.setActiva(true);
        
        repository.save(tarifa);
        Tarifa found = repository.findActivaByTipoVehiculo(1);
        assertNotNull(found);
        assertEquals(new BigDecimal("10.00"), found.getMontoPorHora());
    }
}