import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { Observable } from 'rxjs';

import { LoginRequest } from '../../models/LoginRequest';
import { UsuarioResponse } from '../../models/UsuarioResponse';
import { LoginResponse } from '../../models/LoginResponse';

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

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
