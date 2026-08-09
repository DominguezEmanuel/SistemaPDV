import { Injectable, Inject } from '@angular/core';
// Router: permite redireccionar o construir una nueva ruta
import { CanActivate, Router } from '@angular/router';

import { Auth } from '../core/services/auth';

@Injectable({ providedIn: 'root' })
export class authGuard implements CanActivate {
  constructor(@Inject(Auth) private auth: Auth) {}

  canActivate(): boolean {
    return true;
  }
}
