package com.parkingoffice.repository;

import com.parkingoffice.model.Rol;
import com.parkingoffice.model.Usuario;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRepositoryIT extends AbstractRepositoryIT {

    @Test
    void findByUsername_returnsNull_whenNotFound() {
        UsuarioRepository repository = new UsuarioRepository();
        Usuario result = repository.findByUsername("nonexistent");
        assertNull(result);
    }

    @Test
    void saveAndFindByUsername_works() {
        UsuarioRepository repository = new UsuarioRepository();
        Rol rol = new Rol("OPERADOR");
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPasswordHash("hash");
        usuario.setNombreCompleto("Test User");
        usuario.setRol(rol);
        usuario.setActivo(true);
        
        repository.save(usuario);
        Usuario found = repository.findByUsername("testuser");
        assertNotNull(found);
        assertEquals("Test User", found.getNombreCompleto());
    }

    @Test
    void findAll_returnsList() {
        UsuarioRepository repository = new UsuarioRepository();
        List<Usuario> usuarios = repository.findAll();
        assertNotNull(usuarios);
    }
}