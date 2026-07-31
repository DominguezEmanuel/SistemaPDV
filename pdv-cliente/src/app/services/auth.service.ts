import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { LoginRequest } from '../models/LoginRequest';
import { environment } from '../../environment/environment';
import { LoginResponse } from '../models/LoginResponse';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly storageKey = 'pdv-token';

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${environment.apiUrl}/auth/login`,
      request,
    );
  }

  //////
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
