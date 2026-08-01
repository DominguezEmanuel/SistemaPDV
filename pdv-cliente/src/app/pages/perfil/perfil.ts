import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  idUsuario!: number;
  nombre!: string;
  apellido!: string;
  username!: string;
  activo!: boolean;
  rol!: string;

  constructor(private router: Router) {}

  ngOnInit() {
    const usuario = JSON.parse(localStorage.getItem('usuario')!);
    this.idUsuario = usuario.idUsuario || 0;
    this.nombre = usuario.nombre || '';
    this.apellido = usuario.apellido || '';
    this.username = usuario.username || '';
    this.activo = usuario.activo || false;
    this.rol = usuario.rol || '';
  }
}
