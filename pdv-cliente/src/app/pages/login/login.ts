import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Auth } from '../../core/services/auth';
import { LoginRequest } from '../../models/login-request';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
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
      next: () => {
        this.successMessage = 'Inicio de sesión correcto';
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.errorMessage =
          'No fue posible iniciar sesión. Verifica tus credenciales.';
        this.isLoading = false;
      },
    });
  }
}
