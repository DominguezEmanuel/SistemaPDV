import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Auth } from '../../core/services/auth';
import { LoginRequest } from '../../models/LoginRequest';
import { Router } from '@angular/router';
import { UsuarioResponse } from '../../models/UsuarioResponse';
import { HttpErrorResponse } from '@angular/common/http';
import { LoginResponse } from '../../models/LoginResponse';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  // Variables para el formulario de login
  username = '';
  password = '';
  showPassword = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: Auth,
    private router: Router,
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const request: LoginRequest = {
      username: this.username,
      password: this.password,
    };

    this.isLoading = true;

    this.authService.login(request).subscribe({
      next: (response: LoginResponse) => {
        this.saveUserSession(response);
        this.isLoading = false;
        // Redirige hacia a la página principal del sistema
        this.router.navigate(['/pdv']);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage =
          error.error?.mensaje ?? 'Ocurrió un error al iniciar sesión';

        this.isLoading = false;
      },
    });
  }

  saveUserSession(response: LoginResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('usuario', JSON.stringify(response.usuario));
    console.log('Usuario guardado', localStorage.getItem('usuario'));
  }
}
