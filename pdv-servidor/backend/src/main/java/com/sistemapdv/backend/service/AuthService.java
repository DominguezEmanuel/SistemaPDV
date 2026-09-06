package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.response.UsuarioResponseDTO;
import com.sistemapdv.backend.dto.login.LoginRequestDTO;
import com.sistemapdv.backend.dto.login.LoginResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.InvalidCredentialsException;
import com.sistemapdv.backend.mapper.UsuarioMapper;
import com.sistemapdv.backend.repository.UsuarioRepository;
import com.sistemapdv.backend.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    public AuthService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper,
                       AuthenticationManager authenticationManager, UsuarioMapper usuarioMapper1,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.usuarioMapper = usuarioMapper1;
        this.jwtService = jwtService;
    }

    /**
     * Autentica un usuario mediante sus credenciales (username y password)
     * 
     * Proceso de validación:
     * 1. Verifica que el usuario exista en la base de datos
     * 2. Valida que la contraseña sea correcta usando PasswordEncoder (BCrypt)
     * 3. Comprueba que el usuario esté activo
     * 4. Genera un token JWT firmado para el usuario
     * 5. Retorna LoginResponseDTO con el token y información del usuario
     * 
     * @param request DTO con username y password del usuario
     * @return LoginResponseDTO con token JWT y datos del usuario autenticado
     * @throws InvalidCredentialsException si las credenciales son inválidas o el usuario está inactivo
     */
    public LoginResponseDTO login(LoginRequestDTO request){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado"));


        if(!usuario.getActivo())
            throw new InvalidCredentialsException("El usuario se encuentra inactivo");

        // Generar token JWT para el usuario autenticado
        String token = jwtService.generateToken(usuario);

        UsuarioResponseDTO usuarioDTO = usuarioMapper.toResponseDTO(usuario);

        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .usuario(usuarioDTO)
                .build();
    }
}
