package com.api.crud.services;

import com.api.crud.models.UsuarioModel;
import com.api.crud.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Para roles (temporalmente vacío)

@Service // Indica que es un componente de servicio de Spring
public class MyUserDetailsServices implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        // Busca el usuario por DNI (que aquí actúa como 'username')
        UsuarioModel usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con DNI: " + dni));

        // Construye un objeto UserDetails de Spring Security
        // Necesitamos los roles del usuario. Por ahora, si solo tienes el rol principal
        // de RolModel, podemos añadirlo como una Authority.
        // En un sistema más complejo, mapearías los roles a GrantedAuthority.
        // Aquí lo hacemos simple: usa el nombre del rol del usuario como su autoridad.
        return new User(
                usuario.getDni(),        // El "username" para Spring Security (tu DNI)
                usuario.getPassword(),   // La contraseña hasheada
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())) // Roles/Autoridades
                // Es buena práctica prefijar los roles con "ROLE_" en Spring Security
        );
    }
}