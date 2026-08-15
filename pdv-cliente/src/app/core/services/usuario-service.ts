import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../../environment/environment';

import { UsuarioRequest, UsuarioResponse } from '../../models/Usuario';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/usuarios/';
  }

  getAllUsers() {
    return this.http.get<UsuarioResponse[]>(`${this.hostBase}`);
  }

  changeUserStatus(username: string, activo: boolean) {
    const params = { activo: activo };

    return this.http.patch<UsuarioResponse>(
      `${this.hostBase}estado/${username}`,
      {},
      { params },
    );
  }

  createUser(request: UsuarioRequest | null): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(`${this.hostBase}`, request);
  }

  updateUser(
    idUsuario: number,
    usuario: Partial<UsuarioResponse> & { password?: string },
  ) {
    return this.http.put<any>(`${this.hostBase}${idUsuario}`, usuario);
  }
}
