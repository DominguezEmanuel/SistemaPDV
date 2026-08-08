import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsuarioResponse } from '../../models/Usuario';

import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  usuario!: UsuarioResponse;
  idUsuario!: number;
  nombre!: string;
  apellido!: string;
  username!: string;
  activo!: boolean;
  rol!: string;

  constructor(private authService: Auth) {}

  ngOnInit() {
    const usuario = this.authService.getUserLogued();
    if (usuario) {
      this.usuario = usuario;
      this.cargarUsuario();
    }
  }

  private cargarUsuario() {
    this.idUsuario = this.usuario.idUsuario || 0;
    this.nombre = this.usuario.nombre || '';
    this.apellido = this.usuario.apellido || '';
    this.username = this.usuario.username || '';
    this.activo = this.usuario.activo || false;
    this.rol = this.usuario.rol || '';
  }
}
