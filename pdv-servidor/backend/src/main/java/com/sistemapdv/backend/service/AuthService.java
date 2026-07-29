package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.UsuarioResponseDTO;
import com.sistemapdv.backend.dto.login.LoginRequestDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.InvalidCredentialsException;
import com.sistemapdv.backend.mapper.UsuarioMapper;
import com.sistemapdv.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Autentica un usuario mediante sus credenciales (username y contraseña).
     * Realiza las siguientes validaciones:
     *     Verifica que el usuario exista en la base de datos</li>
     *     Valida que la contraseña sea correcta usando PasswordEncoder</li>
     *     Comprueba que el usuario esté activo</li>
     * @author Emanuel Dev
     */
    public UsuarioResponseDTO login(LoginRequestDTO request){
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow( ()-> new InvalidCredentialsException("Usuario o contraseña incorrectos"));

        if(!passwordEncoder.matches(request.getPassword(), usuario.getPassword()))
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");

        if(!usuario.getActivo())
            throw new InvalidCredentialsException("Usuario inactivo");

        return usuarioMapper.toResponseDTO(usuario);
    }
}
