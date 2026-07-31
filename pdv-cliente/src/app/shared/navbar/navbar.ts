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
  nombre: string = '';
  apellido: string = '';
  username: string = '';
  rol: string = '';

  constructor(private router: Router) {}

  ngOnInit() {
    this.nombre = localStorage.getItem('nombre') || '';
    this.apellido = localStorage.getItem('apellido') || '';
    this.username = localStorage.getItem('username') || '';
    this.rol = localStorage.getItem('rol') || '';
  }
}
