import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { LoginRequest } from '../../models/LoginRequest';
import { UsuarioResponse } from '../../models/UsuarioResponse';
import { LoginResponse } from '../../models/LoginResponse';

import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${environment.apiUrl}/auth/login`,
      request,
    );
  }
}
