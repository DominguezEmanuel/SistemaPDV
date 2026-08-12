import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsuarioService } from '../../core/services/usuario-service';
import { Auth } from '../../core/services/auth';

import { UsuarioResponse } from '../../models/Usuario';
import { UsuarioForm } from '../usuarios/usuario-form/usuario-form';

import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../core/services/alert-service';

@Component({
  selector: 'app-configuracion',
  imports: [CommonModule, UsuarioForm],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.css',
})
export class Configuracion implements OnInit {
  usuarios: UsuarioResponse[] = [];
  usuarioLogueado!: UsuarioResponse;
  usuarioSeleccionado: UsuarioResponse | null = null;
  modalVisible = false;
  constructor(
    private usuarioService: UsuarioService,
    private authService: Auth,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {}

  ngOnInit() {
    this.cargarUsuarios();
  }

  abrirModal(usuario?: UsuarioResponse): void {
    if (usuario) {
      this.usuarioSeleccionado = usuario;
    } else {
      this.usuarioSeleccionado = null;
    }
    this.modalVisible = true;
  }

  cerrarModal(): void {
    this.modalVisible = false;
    this.usuarioSeleccionado = null;
  }

  cargarUsuarios(): void {
    this.usuarioService.getAllUsers().subscribe({
      next: (response) => {
        //console.log('Usuarios obtenidos:', response);
        this.usuarios = response;
      },
      error: (error) => {
        this.toastr.error('Error al obtener usuarios');
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
