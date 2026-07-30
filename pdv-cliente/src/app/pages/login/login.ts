import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Auth } from '../../core/services/auth';
import { LoginRequest } from '../../models/login-request';
import { Router } from '@angular/router';
import { UsuarioResponse } from '../../models/UsuarioResponse';
import { HttpErrorResponse } from '@angular/common/http';

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
      next: (response: UsuarioResponse) => {
        //console.log('Login exitoso:', response);
        this.successMessage = 'Inicio de sesión correcto';
        this.isLoading = false;
        // Redirige hacia el dashbboard
        this.router.navigate(['/dashboard']);
      },
      error: (error: HttpErrorResponse) => {
        //console.log('Error al iniciar sesión: ', error.error);
        this.errorMessage =
          error.error?.mensaje ?? 'Ocurrió un error al iniciar sesión';

        this.isLoading = false;
      },
    });
  }
}
