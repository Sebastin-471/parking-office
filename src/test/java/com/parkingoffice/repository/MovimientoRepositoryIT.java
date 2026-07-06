package com.parkingoffice.repository;

import com.parkingoffice.model.Movimiento;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoRepositoryIT extends AbstractRepositoryIT {

    @Test
    void findActivos_returnsOnlyActive() {
        MovimientoRepository repository = new MovimientoRepository();
        List<Movimiento> activos = repository.findActivos();
        assertNotNull(activos);
    }
}