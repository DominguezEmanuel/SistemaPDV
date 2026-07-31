import { UsuarioResponse } from './UsuarioResponse';

export interface LoginResponse {
  token: string;
  tokenType: string;
  usuario: UsuarioResponse;
}
