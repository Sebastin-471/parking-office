package com.parkingoffice.core;

import com.parkingoffice.model.Usuario;

public class SessionManager {
    
    private static SessionManager instance;
    private Usuario currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(Usuario user) {
        this.currentUser = user;
    }

    public Usuario getCurrentUser() {
        return currentUser;
    }
    
    public boolean isAdmin() {
        return hasRole("ADMINISTRADOR");
    }

    public boolean hasRole(String roleName) {
        return currentUser != null && currentUser.getRol() != null && roleName.equals(currentUser.getRol().getNombre());
    }

    public void logout() {
        this.currentUser = null;
    }
}
