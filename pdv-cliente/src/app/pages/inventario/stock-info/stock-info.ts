import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockResponse } from '../../../models/Stock';

@Component({
  selector: 'app-stock-info',
  imports: [CommonModule],
  templateUrl: './stock-info.html',
  styleUrl: './stock-info.css',
})
export class StockInfo {
  // Estructuras utilizadas
  @Input() stock: StockResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();

  constructor() {}

  asignarEstadoStock(cantidadDisponible: number, stockMinimo: number): string {
    if (cantidadDisponible <= 0) {
      return 'Sin stock';
    }

    if (cantidadDisponible <= stockMinimo) {
      return 'Stock bajo';
    }

    return 'Disponible';
  }

  obtenerClaseEstado(cantidad: number, minimo: number): string {
    if (cantidad <= 0) {
      return 'sin-stock';
    }

    if (cantidad <= minimo) {
      return 'stock-bajo';
    }

    return 'disponible';
  }

  cerrarModal(): void {
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
