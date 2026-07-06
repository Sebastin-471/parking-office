package com.parkingoffice.service;

import com.parkingoffice.model.Usuario;
import com.parkingoffice.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks AuthService authService;

    @Test
    void login_success() {
        Usuario user = new Usuario();
        user.setId(1);
        user.setUsername("admin");
        user.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        user.setActivo(true);

        when(usuarioRepository.findByUsername("admin")).thenReturn(user);

        Usuario result = authService.login("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(usuarioRepository).findByUsername("admin");
    }

    @Test
    void login_wrongPassword_returnsNull() {
        Usuario user = new Usuario();
        user.setId(1);
        user.setUsername("admin");
        user.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        user.setActivo(true);

        when(usuarioRepository.findByUsername("admin")).thenReturn(user);

        Usuario result = authService.login("admin", "wrongpass");

        assertNull(result);
    }

    @Test
    void login_inactiveUser_returnsNull() {
        Usuario user = new Usuario();
        user.setId(1);
        user.setUsername("admin");
        user.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        user.setActivo(false);

        when(usuarioRepository.findByUsername("admin")).thenReturn(user);

        Usuario result = authService.login("admin", "admin123");

        assertNull(result);
    }

    @Test
    void login_userNotFound_returnsNull() {
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(null);

        Usuario result = authService.login("nonexistent", "pass");

        assertNull(result);
    }

    @Test
    void hashPassword_generatesValidHash() {
        String password = "testPassword";
        String hash = authService.hashPassword(password);

        assertNotNull(hash);
        assertTrue(BCrypt.checkpw(password, hash));
        assertFalse(BCrypt.checkpw("wrongPassword", hash));
    }

    @Test
    void logout_clearsSession() {
        Usuario user = new Usuario();
        user.setId(1);
        user.setUsername("admin");
        user.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        user.setActivo(true);

        when(usuarioRepository.findByUsername("admin")).thenReturn(user);
        authService.login("admin", "admin123");
        assertNotNull(authService.getLoggedInUser());

        authService.logout();
        assertNull(authService.getLoggedInUser());
    }

    @Test
    void createDefaultAdminIfNotExists_userAlreadyExists_doesNothing() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(new Usuario());
        authService.createDefaultAdminIfNotExists();
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}