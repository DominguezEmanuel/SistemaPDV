package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.login.LoginRequestDTO;
import com.sistemapdv.backend.dto.login.LoginResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica un usuario mediante sus credenciales (username y contraseña).
     * Realiza las siguientes validaciones:
     *     Verifica que el usuario exista en la base de datos</li>
     *     Valida que la contraseña sea correcta usando PasswordEncoder</li>
     *     Comprueba que el usuario esté activo</li>
     * @author Emanuel Dev
     */
    public LoginResponseDTO login(LoginRequestDTO request){
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow( ()-> new RuntimeException("Usuario o contraseña incorrectos"));

        if(!passwordEncoder.matches(request.getPassword(), usuario.getPassword()))
            throw new RuntimeException("Usuario o contraseña incorrectos");

        if(!usuario.getActivo())
            throw new RuntimeException("Usuario inactivo");

        LoginResponseDTO response = new LoginResponseDTO();

        response.setId(usuario.getIdUsuario());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setUsername(usuario.getUsername());

        return response;
    }
}
