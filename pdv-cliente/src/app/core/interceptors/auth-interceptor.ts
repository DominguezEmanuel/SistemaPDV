import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('/api/auth/login')) {
    return next(req);
  }

  // Busca el token en LocalStorage
  const token = localStorage.getItem('token');

  // Si no existe, deja pasar la petición sin modificarla
  if (!token) {
    return next(req);
  }

  // Si existe, clona la petición y añade el token en los headers
  const authRequest = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(authRequest);
};
