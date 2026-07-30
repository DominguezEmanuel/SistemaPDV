import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { LoginRequest } from '../models/login-request';
import { UsuarioResponse } from '../models/UsuarioResponse';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly storageKey = 'pdv-token';

  constructor(private readonly http: HttpClient) {}

  login(credentials: LoginRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(
      `${environment.apiUrl}/auth/login`,
      credentials,
    );
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }

  getToken(): string | null {
    return localStorage.getItem(this.storageKey);
  }
}
