import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsuarioService } from '../../core/services/usuario-service';
import { Auth } from '../../core/services/auth';
import { CanalService } from '../../core/services/canal-service';

import { UsuarioResponse } from '../../models/Usuario';
import { CanalResponse } from '../../models/Canal';
import { UsuarioForm } from '../usuarios/usuario-form/usuario-form';
import { CanalForm } from '../canales/canal-form/canal-form';

import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../core/services/alert-service';

@Component({
  selector: 'app-configuracion',
  imports: [CommonModule, UsuarioForm, CanalForm],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.css',
})
export class Configuracion implements OnInit {
  usuarios: UsuarioResponse[] = [];
  canales: CanalResponse[] = [];
  usuarioLogueado!: UsuarioResponse;
  usuarioSeleccionado: UsuarioResponse | null = null;
  canalSeleccionado: CanalResponse | null = null;
  modalVisible = false;
  formularioCanal = false;
  constructor(
    private usuarioService: UsuarioService,
    private authService: Auth,
    private canalService: CanalService,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {}

  ngOnInit() {
    this.cargarUsuarios();
    this.cargarCanalesVentas();
  }

  abrirModal(usuario?: UsuarioResponse): void {
    if (usuario) {
      this.usuarioSeleccionado = usuario;
    } else {
      this.usuarioSeleccionado = null;
    }
    this.modalVisible = true;
  }

  verFormularioCanal(canal?: CanalResponse): void {
    if (canal) {
      this.canalSeleccionado = canal;
    } else {
      this.canalSeleccionado = null;
    }
    this.formularioCanal = true;
  }

  cerrarModal(): void {
    if (this.modalVisible) {
      this.modalVisible = false;
      this.usuarioSeleccionado = null;
    } else {
      this.formularioCanal = false;
      this.canalSeleccionado = null;
    }
  }

  cargarUsuarios(): void {
    this.usuarioService.getAllUsers().subscribe({
      next: (response) => {
        console.log('Usuarios obtenidos:', response);
        this.usuarios = response;
      },
      error: (error) => {
        console.log('Error', error);
        this.toastr.error(error.message, 'Error');
      },
    });
  }

  cargarCanalesVentas(): void {
    this.canalService.obtenerCanales().subscribe({
      next: (response) => {
        console.log('Canales obtenidos: ', response);
        this.canales = response;
      },
      error: (error) => {
        this.toastr.error(error.message, 'Error');
      },
    });
  }

  getInitials(name: string, surname: string): string {
    // trim: Elimina espacios al inicio y al final de la cadena
    // charAt(0): Obtiene el primer carácter de la cadena
    // ?.: Evita errores si los datos son nulos o indefinidos
    const nameInitial = name?.trim().charAt(0).toUpperCase() || '';
    const surnameInitial = surname?.trim().charAt(0).toUpperCase() || '';
    return nameInitial + surnameInitial;
  }

  // Devuelve el username del usuario logueado, o null si no hay usuario logueado
  userLogged(): string | null {
    const usuario = this.authService.getUserLogued();
    if (usuario) {
      this.usuarioLogueado = usuario;
    }
    return this.usuarioLogueado ? this.usuarioLogueado.username : null;
  }

  onUsuarioCreado(usuario: UsuarioResponse): void {
    this.modalVisible = false;
    this.cargarUsuarios();
    this.toastr.success('Usuario creado correctamente', 'Usuario creado');
  }

  onCanalGuardado(event: {
    canal: CanalResponse;
    accion: 'crear' | 'editar';
  }): void {
    this.cargarCanalesVentas();

    if (event.accion === 'crear') {
      this.toastr.success('El canal se creó correctamente', 'Canal creado');
    } else {
      this.toastr.success(
        'Los cambios del canal se guardaron correctamente',
        'Canal actualizado',
      );
    }
  }

  cambiarEstadoUsuario(username: string, activo: boolean): void {
    this.alertService
      .confirmarCambioEstado('usuario', username, activo)
      .then((result) => {
        if (result.isConfirmed) {
          this.usuarioService.changeUserStatus(username, activo).subscribe({
            next: (response) => {
              this.toastr.success(
                'Estado del usuario actualizado correctamente',
                'Éxito',
              );
              this.cargarUsuarios(); // Recargar la lista de usuarios después de actualizar el estado
            },
            error: (error) => {
              this.toastr.error(error.error.mensaje, 'Error');
            },
          });
        }
      });
  }
}
