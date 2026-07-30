import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { LoginRequest } from '../../models/login-request';
import { UsuarioResponse } from '../../models/UsuarioResponse';

import { environment } from '../../../environment/environment';
@Injectable({
  providedIn: 'root',
})
export class Auth {
  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(
      `${environment.apiUrl}/auth/login`,
      request,
    );
  }
}
