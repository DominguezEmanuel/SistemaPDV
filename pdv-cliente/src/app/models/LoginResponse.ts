import { UsuarioResponse } from './Usuario';

export interface LoginResponse {
  token: string;
  tokenType: string;
  usuario: UsuarioResponse;
}
