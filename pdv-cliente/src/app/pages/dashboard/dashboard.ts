import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
//import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  nombre: string = '';
  apellido: string = '';
  username: string = '';
  rol: string = '';

  constructor(private router: Router) {}
}
