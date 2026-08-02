import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { User } from '../../core/services/user';
import { Auth } from '../../core/services/auth';

import { UsuarioResponse } from '../../models/UsuarioResponse';

import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-configuracion',
  imports: [CommonModule],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.css',
})
export class Configuracion implements OnInit {
  usuarios: any[] = [];
  usuarioLogueado!: UsuarioResponse;

  constructor(
    private userService: User,
    private authService: Auth,
    private toastr: ToastrService,
  ) {}

  ngOnInit() {
    this.loadUsers();
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
