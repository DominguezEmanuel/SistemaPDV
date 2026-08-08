export interface UsuarioResponse {
  idUsuario: number;
  nombre: string;
  apellido: string;
  username: string;
  rol: string;
  activo: boolean;
}

export interface UsuarioRequest {
  nombre: string;
  apellido: string;
  username: string;
  password: string;
  rol: string;
}
