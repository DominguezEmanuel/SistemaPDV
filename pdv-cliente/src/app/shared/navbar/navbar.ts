import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';

import { UsuarioResponse } from '../../models/UsuarioResponse';

import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  usuario!: UsuarioResponse;
  nombre!: string;
  apellido!: string;
  username!: string;
  activo!: boolean;
  rol!: string;

  constructor(
    private router: Router,
    private authService: Auth,
  ) {}

  ngOnInit() {
    const usuario = this.authService.getUserLogued();
    if (usuario) {
      this.usuario = usuario;
      this.loadUserData();
    }
  }

  loadUserData() {
    this.nombre = this.usuario.nombre || '';
    this.apellido = this.usuario.apellido || '';
    this.username = this.usuario.username || '';
    this.activo = this.usuario.activo || false;
    this.rol = this.usuario.rol || '';
  }

  logout() {
    this.authService.logout();
  }
}
