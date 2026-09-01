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

  asignarEstadoStock(estado: string): string {
    if (estado === 'SIN_STOCK') {
      return 'Sin stock';
    }

    if (estado === 'STOCK_BAJO') {
      return 'Stock bajo';
    }

    return 'Disponible';
  }

  obtenerClaseEstado(estado: string): string {
    if (estado === 'SIN_STOCK') {
      return 'sin-stock';
    }

    if (estado === 'STOCK_BAJO') {
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
