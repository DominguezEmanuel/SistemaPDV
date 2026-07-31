import { Injectable, Inject } from '@angular/core';
// Router: permite redireccionar o construir una nueva ruta
import { CanActivate, Router } from '@angular/router';

// Servicio en donde se encuentra la lógica del login
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class authGuard implements CanActivate {
  constructor(
    @Inject(AuthService) private auth: AuthService,
    private router: Router,
  ) {}

  canActivate(): boolean {
    return true;
  }
  /*const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated()
    ? true
    : router.createUrlTree(['/login']);*/
}
