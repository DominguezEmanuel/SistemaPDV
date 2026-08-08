import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { Observable } from 'rxjs';
import { UsuarioResponse } from '../../models/UsuarioResponse';

@Injectable({
  providedIn: 'root',
})
export class User {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/usuarios/';
  }

  getAllUsers(): Observable<any> {
    return this.http.get<any>(`${this.hostBase}`);
  }

  changeUserStatus(username: string, activo: boolean): Observable<any> {
    const params = { activo: activo };

    return this.http.patch<any>(
      `${this.hostBase}estado/${username}`,
      {},
      { params },
    );
  }

  createUser(
    usuario: Partial<UsuarioResponse> & { password?: string },
  ): Observable<any> {
    return this.http.post<any>(`${this.hostBase}`, usuario);
  }

  updateUser(
    idUsuario: number,
    usuario: Partial<UsuarioResponse> & { password?: string },
  ): Observable<any> {
    return this.http.put<any>(`${this.hostBase}${idUsuario}`, usuario);
  }
}
