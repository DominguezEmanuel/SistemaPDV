package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.UsuarioResponseDTO;
import com.sistemapdv.backend.dto.login.LoginRequestDTO;
import com.sistemapdv.backend.dto.login.LoginResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.InvalidCredentialsException;
import com.sistemapdv.backend.mapper.UsuarioMapper;
import com.sistemapdv.backend.repository.UsuarioRepository;
import com.sistemapdv.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, 
                       UsuarioMapper usuarioMapper, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
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
        // Paso 1: Buscar el usuario por username
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Usuario o contraseña incorrectos"));

        // Paso 2: Validar que la contraseña sea correcta
        // passwordEncoder.matches() compara la contraseña sin encriptar con la hash almacenada
        if(!passwordEncoder.matches(request.getPassword(), usuario.getPassword()))
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");

        // Paso 3: Verificar que el usuario esté activo
        if(!usuario.getActivo())
            throw new InvalidCredentialsException("Usuario inactivo");

        // Paso 4: Generar token JWT para el usuario autenticado
        String token = jwtService.generateToken(usuario);

        // Paso 5: Convertir usuario a DTO
        UsuarioResponseDTO usuarioDTO = usuarioMapper.toResponseDTO(usuario);

        // Paso 6: Retornar respuesta con token e información del usuario
        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .usuario(usuarioDTO)
                .build();
    }
}
