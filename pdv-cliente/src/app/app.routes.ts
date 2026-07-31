import { Routes } from '@angular/router';

import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { Perfil } from './pages/perfil/perfil';
import { Ventas } from './pages/ventas/ventas';
import { Productos } from './pages/productos/productos';
import { Inventario } from './pages/inventario/inventario';
import { Reportes } from './pages/reportes/reportes';
import { Configuracion } from './pages/configuracion/configuracion';
import { PublicLayout } from './shared/public-layout/public-layout';

import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  //{ path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'pdv',
    component: PublicLayout,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'ventas', component: Ventas },
      { path: 'productos', component: Productos },
      { path: 'inventario', component: Inventario },
      { path: 'reportes', component: Reportes },
      { path: 'configuracion', component: Configuracion },
      { path: 'perfil', component: Perfil },
    ],
  },
  { path: '', component: Login },
  // Crear una pagina de error
];
