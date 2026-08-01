import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { User } from '../../core/services/user';

@Component({
  selector: 'app-configuracion',
  imports: [CommonModule],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.css',
})
export class Configuracion implements OnInit {
  usuarios: any[] = [];

  constructor(private userService: User) {}

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
    // ?. : Evita errores si los datos son nulos o indefinidos
    const nameInitial = name?.trim().charAt(0).toUpperCase() || '';
    const surnameInitial = surname?.trim().charAt(0).toUpperCase() || '';
    return nameInitial + surnameInitial;
  }
}
