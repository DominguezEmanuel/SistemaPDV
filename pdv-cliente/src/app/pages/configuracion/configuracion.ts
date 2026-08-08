import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule } from '@angular/forms';
import { User } from '../../core/services/user';
import { Auth } from '../../core/services/auth';

import { UsuarioResponse } from '../../models/UsuarioResponse';
import { UsuarioForm } from '../usuarios/usuario-form/usuario-form';

import { ToastrService } from 'ngx-toastr';

import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Validadores } from '../../validators/validadores';

@Component({
  selector: 'app-configuracion',
  imports: [CommonModule, UsuarioForm, ReactiveFormsModule, FormsModule],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.css',
})
export class Configuracion implements OnInit {
  usuarios: UsuarioResponse[] = [];
  usuarioLogueado!: UsuarioResponse;
  usuarioSeleccionado: UsuarioResponse | null = null;
  modalVisible = false;
  constructor(
    private userService: User,
    private authService: Auth,
    private toastr: ToastrService,
    private fb: FormBuilder,
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  abrirModal(usuario?: UsuarioResponse): void {
    if (usuario) {
      this.usuarioSeleccionado = usuario;
    } else {
      this.usuarioSeleccionado = null;
    }
    this.modalVisible = true;
    console.log('Modal abierto: ', this.modalVisible, this.usuarioSeleccionado);
  }

  cerrarModal(): void {
    this.modalVisible = false;
    this.usuarioSeleccionado = null;
  }

  guardarUsuario(
    usuario: Partial<UsuarioResponse> & { password?: string },
  ): void {
    const peticion = usuario.idUsuario
      ? this.userService.updateUser(usuario.idUsuario, usuario)
      : this.userService.createUser(usuario);

    peticion.subscribe({
      next: () => {
        const accion = usuario.idUsuario ? 'actualizado' : 'creado';
        this.toastr.success(`Usuario ${accion} correctamente`, 'Éxito');
        this.loadUsers();
        this.cerrarModal();
      },
      error: (error) => {
        console.error('Error al guardar usuario:', error);
        this.toastr.error('Error al guardar el usuario', 'Error');
      },
    });
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (response) => {
        console.log('Usuarios obtenidos:', response);
        this.usuarios = response;
      },
      error: (error) => {
        console.error('Error al obtener usuarios:', error);
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
    if (usuario) this.usuarioLogueado = usuario;
    return this.usuarioLogueado ? this.usuarioLogueado.username : null;
  }

  changeUserStatus(username: string, newState: boolean): void {
    console.log(`Cambiando estado del usuario ${username} a ${newState}`);
    this.userService.changeUserStatus(username, newState).subscribe({
      next: (response) => {
        console.log('Estado del usuario actualizado:', response);
        this.toastr.success(
          'Estado del usuario actualizado correctamente',
          'Éxito',
        );
        this.loadUsers(); // Recargar la lista de usuarios después de actualizar el estado
      },
      error: (error) => {
        console.error('Error al actualizar el estado del usuario:', error);
        this.toastr.error('Error al actualizar el estado del usuario', 'Error');
      },
    });
  }
}
