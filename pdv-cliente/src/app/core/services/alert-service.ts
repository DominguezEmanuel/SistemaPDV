import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  userDisabled() {
    return Swal.fire({
      icon: 'success',
      title: 'Usuario deshabilitado',
      text: 'El usuario fue deshabilitado correctamente.',
      timer: 1800,
      showConfirmButton: false,
    });
  }

  confirmarCambioEstado(
    tipo: 'usuario' | 'producto',
    nombre: string,
    nuevoEstado: boolean,
  ): Promise<any> {
    const entidad = tipo === 'usuario' ? 'usuario' : 'producto';

    const texto =
      tipo === 'usuario'
        ? nuevoEstado
          ? `El usuario "${nombre}" podrá iniciar sesión y acceder al sistema.`
          : `El usuario "${nombre}" no podrá iniciar sesión ni acceder al sistema.`
        : `El producto "${nombre}" dejará de estar disponible para nuevas ventas.`;

    return Swal.fire({
      title: `${nuevoEstado ? '¿Habilitar' : '¿Deshabilitar'} ${entidad}?`,
      text: texto,
      icon: nuevoEstado ? 'info' : 'warning',
      showCancelButton: true,
      confirmButtonText: `Sí, ${nuevoEstado ? 'habilitar' : 'deshabilitar'}`,
      cancelButtonText: 'Cancelar',
      customClass: {
        confirmButton: `btn btn-${nuevoEstado ? 'info' : 'warning'}`,
        cancelButton: 'btn btn-danger',
      },
    });
  }
}
