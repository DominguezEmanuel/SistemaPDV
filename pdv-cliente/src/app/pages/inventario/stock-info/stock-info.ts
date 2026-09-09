import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockResponse } from '../../../models/Stock';

@Component({
  selector: 'app-stock-info',
  imports: [CommonModule],
  templateUrl: './stock-info.html',
  styleUrl: './stock-info.css',
})
export class StockInfo {
  // Variables de entrada y salida del componente
  @Input() stock: StockResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();

  asignarEstadoStock(estado: string): string {
    if (estado.toLowerCase() === 'sin_stock') {
      return 'Sin stock';
    }

    if (estado.toLowerCase() === 'stock_bajo') {
      return 'Stock bajo';
    }

    return 'Disponible';
  }

  obtenerClaseEstado(estado: string): string {
    if (estado.toLowerCase() === 'sin_stock') {
      return 'sin-stock';
    }

    if (estado.toLowerCase() === 'stock_bajo') {
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
