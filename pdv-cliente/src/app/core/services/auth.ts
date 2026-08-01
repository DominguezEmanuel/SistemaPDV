import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { Observable } from 'rxjs';

// Models
import { LoginRequest } from '../../models/LoginRequest';
import { UsuarioResponse } from '../../models/UsuarioResponse';
import { LoginResponse } from '../../models/LoginResponse';
// Environment
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${environment.apiUrl}/auth/login`,
      request,
    );
  }

  getUserLogued(): UsuarioResponse | null {
    const usuario = localStorage.getItem('usuario');

    if (!usuario) return null;

    return JSON.parse(usuario) as UsuarioResponse;
  }

  isAuthenticated(): boolean {
    return localStorage.getItem('token') !== null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return this.isAuthenticated() && this.getUserLogued() !== null;
  }

  userRole(): string | null {
    const token = this.getToken();

    if (!token) return '';

    const payload = token.split('.')[1];

    try {
      const decodedPayload = JSON.parse(atob(payload));
      return decodedPayload.rol || '';
    } catch (e) {
      return '';
    }
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
