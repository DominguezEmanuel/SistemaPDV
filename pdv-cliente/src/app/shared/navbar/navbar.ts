import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  nombre!: string;
  apellido!: string;
  username!: string;
  activo!: boolean;
  rol!: string;

  constructor(private router: Router) {}

  ngOnInit() {
    const usuario = JSON.parse(localStorage.getItem('usuario')!);
    this.nombre = usuario.nombre || '';
    this.apellido = usuario.apellido || '';
    this.username = usuario.username || '';
    this.activo = usuario.activo || false;
    this.rol = usuario.rol || '';
  }

  logout() {
    localStorage.clear();
    this.nombre = '';
    this.apellido = '';
    this.username = '';
    this.activo = false;
    this.rol = '';
    this.router.navigate(['/login']);
  }
}
