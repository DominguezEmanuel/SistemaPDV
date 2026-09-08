import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
// Models
import { StockResponse } from '../../../models/Stock';
import { MovimientoResponse } from '../../../models/Movimiento';
// Services
import { MovimientoService } from '../../../core/services/movimiento-service';
import { ToastrService } from 'ngx-toastr';
// Others
import { finalize } from 'rxjs';

@Component({
  selector: 'app-movimiento-record',
  imports: [CommonModule],
  templateUrl: './movimiento-record.html',
  styleUrl: './movimiento-record.css',
})
export class MovimientoRecord implements OnChanges {
  // Variables de entrada y salida del componente
  @Input() stock: StockResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();

  // Estructuras utilizadas en el componente
  movimientos: MovimientoResponse[] = [];

  constructor(
    private movimientoService: MovimientoService,
    private toastr: ToastrService,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['stock'] && this.stock) {
      this.obtenerHistorialMovimientos();
    }
  }

  obtenerHistorialMovimientos(): void {
    if (!this.stock) {
      return;
    }

    this.movimientoService
      .obtenerUltimosMovimientosStock(this.stock.idStock)
      .subscribe({
        next: (response) => {
          //console.log('Movimientos: ', response);
          this.movimientos = response;
        },
        error: (error) => {
          this.toastr.error(
            'Error al obtener los movimientos del stock',
            'Error',
          );
        },
      });
  }

  resetearModal(): void {
    this.movimientos = [];
    this.stock = null;
  }

  cerrarModal(): void {
    this.resetearModal();
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
