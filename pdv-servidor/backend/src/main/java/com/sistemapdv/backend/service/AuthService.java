package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.response.UsuarioResponseDTO;
import com.sistemapdv.backend.dto.login.LoginRequestDTO;
import com.sistemapdv.backend.dto.login.LoginResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.InvalidCredentialsException;
import com.sistemapdv.backend.mapper.UsuarioMapper;
import com.sistemapdv.backend.repository.UsuarioRepository;
import com.sistemapdv.backend.security.JwtService;
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

    public AuthService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper,
                       AuthenticationManager authenticationManager, UsuarioMapper usuarioMapper1,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.usuarioMapper = usuarioMapper1;
        this.jwtService = jwtService;
    }

    /**
     * Autentica un usuario mediante sus credenciales (username y contraseña).
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
     * @author Emanuel Dev
     */
    public LoginResponseDTO login(LoginRequestDTO request){

        try {
            // Autenticar credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }

        // Buscar el usuario ya autenticado
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado"));

        // Verificar que el usuario esté activo
        if(!usuario.getActivo())
            throw new InvalidCredentialsException("El usuario se encuentra inactivo");

        // Generar token JWT para el usuario autenticado
        String token = jwtService.generateToken(usuario);

        // Convertir usuario a DTO
        UsuarioResponseDTO usuarioDTO = usuarioMapper.toResponseDTO(usuario);

        // Retornar respuesta con token e información del usuario
        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .usuario(usuarioDTO)
                .build();
    }
}
