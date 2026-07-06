package com.parkingoffice.repository;

import com.parkingoffice.model.EspacioConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EspacioConfigRepositoryIT extends AbstractRepositoryIT {

    @Test
    void findActivo_returnsNull_whenEmpty() {
        EspacioConfigRepository repository = new EspacioConfigRepository();
        EspacioConfig result = repository.findActivo();
        assertNull(result);
    }

    @Test
    void saveAndFindActivo_works() {
        EspacioConfigRepository repository = new EspacioConfigRepository();
        EspacioConfig config = new EspacioConfig(10);
        repository.save(config);
        EspacioConfig found = repository.findActivo();
        assertNotNull(found);
        assertEquals(10, found.getCapacidadMaxima());
    }
}