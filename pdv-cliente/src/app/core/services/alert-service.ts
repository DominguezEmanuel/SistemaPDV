import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  texto: string | null = null;
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

    if (tipo === 'usuario') {
      this.texto = nuevoEstado
        ? `El usuario "${nombre}" podrá iniciar sesión y acceder al sistema`
        : `El usuario "${nombre}" no podrá iniciar sesión ni acceder al sistema`;
    } else {
      this.texto = nuevoEstado
        ? `El producto "${nombre}" estará habilitado para nuevas ventas`
        : `El producto "${nombre}" dejará de estar disponible para nuevas ventas`;
    }

    return Swal.fire({
      title: `${nuevoEstado ? '¿Habilitar' : '¿Deshabilitar'} ${entidad}?`,
      text: this.texto,
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

  confirmarCambioEstadoVariante(
    nombre: string,
    nuevoEstado: boolean,
  ): Promise<any> {
    this.texto = nuevoEstado
      ? `La variante "${nombre}" estará habilitada para nuevas ventas`
      : `La variante "${nombre}" dejará de estar disponible para nuevas ventas`;

    return Swal.fire({
      title: `${nuevoEstado ? '¿Habilitar Variante?' : '¿Deshabilitar Variante?'}`,
      text: this.texto,
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

  confirmarEliminacionConfiguracion(nombre: string): Promise<any> {
    return Swal.fire({
      title: '¿Eliminar Configuración Comercial?',
      text: `La configuración comercial del producto "${nombre}" será eliminada y no podrá ser recuperada.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      customClass: {
        confirmButton: 'btn btn-danger',
        cancelButton: 'btn btn-secondary',
      },
    });
  }
}
