package com.parkingoffice.service;

import com.parkingoffice.model.Usuario;
import com.parkingoffice.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private Usuario loggedInUser;

    public AuthService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuario login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario != null && usuario.getActivo()) {
            if (BCrypt.checkpw(password, usuario.getPasswordHash())) {
                this.loggedInUser = usuario;
                return usuario;
            }
        }
        return null;
    }

    public void logout() {
        this.loggedInUser = null;
    }

    public Usuario getLoggedInUser() {
        return loggedInUser;
    }

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
