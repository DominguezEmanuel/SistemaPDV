import {
  Component,
  OnChanges,
  SimpleChanges,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  FormsModule,
  FormControl,
  Validators,
} from '@angular/forms';
// Models
import { StockResponse } from '../../../models/Stock';
import {
  MovimientoRequest,
  MovimientoResponse,
} from '../../../models/Movimiento';
// Services
import { StockService } from '../../../core/services/stock-service';
import { ToastrService } from 'ngx-toastr';
// Others
import { finalize } from 'rxjs';

@Component({
  selector: 'app-movimiento-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './movimiento-form.html',
  styleUrl: './movimiento-form.css',
})
export class MovimientoForm implements OnChanges {
  // Variables de entrada y salida del componente
  @Input() stock: StockResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() movimientoGuardado = new EventEmitter<{
    movimiento: MovimientoResponse;
    accion: 'crear' | 'editar';
  }>();
  // Variables y estructuras del componente
  formMovimiento!: FormGroup;
  movimientoForm: MovimientoRequest | null = null;
  guardando = false;
  modo: 'crear' | 'editar' = 'crear';

  constructor(
    private fb: FormBuilder,
    private stockService: StockService,
    private toastr: ToastrService,
  ) {
    this.formMovimiento = this.fb.group(this.gerControlesFormulario());

    this.formMovimiento.get('tipo')?.valueChanges.subscribe((tipo) => {
      this.configurarValidadoresPorTipo(tipo);
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['stock'] && this.stock) {
      this.resetearFormulario();
    }
  }

  private configurarValidadoresPorTipo(tipo: string | null): void {
    const cantidad = this.formMovimiento.get('cantidad');
    const stockFisico = this.formMovimiento.get('stockFisico');
    const motivo = this.formMovimiento.get('motivo');

    // Primero limpiamos los validadores dinámicos
    cantidad?.clearValidators();
    stockFisico?.clearValidators();
    motivo?.clearValidators();

    motivo?.reset(null, { emitEvent: false });

    switch (tipo) {
      case 'ENTRADA':
        cantidad?.setValidators([Validators.required, Validators.min(1)]);

        //stockFisico?.setValidators([Validators.min(0)]);

        motivo?.setValidators([
          Validators.minLength(5),
          Validators.maxLength(50),
        ]);

        // Limpiar stock físico
        stockFisico?.reset(null, { emitEvent: false });
        break;

      case 'SALIDA':
        cantidad?.setValidators([Validators.required, Validators.min(1)]);

        //stockFisico?.setValidators([Validators.min(0)]);

        motivo?.setValidators([
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(50),
        ]);

        // Limpiar stock físico
        stockFisico?.reset(null, { emitEvent: false });
        break;

      case 'AJUSTE':
        //cantidad?.setValidators([Validators.min(1)]);

        stockFisico?.setValidators([Validators.required, Validators.min(0)]);

        motivo?.setValidators([
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(50),
        ]);

        // Limpiar cantidad
        cantidad?.reset(null, { emitEvent: false });
        break;

      default:
        // Si no hay tipo seleccionado, limpiar ambos
        cantidad?.reset(null, { emitEvent: false });
        stockFisico?.reset(null, { emitEvent: false });
        break;
    }

    // Recalcular estado de los controles
    cantidad?.updateValueAndValidity();
    stockFisico?.updateValueAndValidity();
    motivo?.updateValueAndValidity();
  }

  private gerControlesFormulario() {
    return {
      tipo: new FormControl<string | null>(null, {
        nonNullable: true,
        validators: [Validators.required],
      }),

      cantidad: new FormControl<number | null>(null),

      stockFisico: new FormControl<number | null>(null),

      motivo: new FormControl<string | null>(null),
    };
  }

  procesarFormulario() {
    if (this.formMovimiento.invalid) {
      this.formMovimiento.markAllAsTouched();
      return;
    }

    this.guardando = true;
    this.asignarValores();

    console.log('Movimiento: ', this.movimientoForm);
    if (this.modo === 'crear') {
      this.crearMovimientoStock();
    }
  }

  crearMovimientoStock(): void {
    if (!this.stock || !this.movimientoForm) {
      return;
    }

    this.stockService
      .crearMovimientoStock(this.stock.idStock, this.movimientoForm)
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.movimientoGuardado.emit({
            movimiento: response,
            accion: 'crear',
          });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  private asignarValores() {
    const valoresForm = this.formMovimiento.getRawValue();

    this.movimientoForm = {
      tipo: valoresForm.tipo,
      cantidad: valoresForm.cantidad,
      stockFisico: valoresForm.stockFisico,
      motivo: valoresForm.motivo,
    };
  }

  private resetearFormulario(): void {
    this.formMovimiento.reset(
      {
        tipo: null,
        cantidad: null,
        stockFisico: null,
        motivo: null,
      },
      { emitEvent: false },
    );

    this.formMovimiento.markAsPristine();
    this.formMovimiento.markAsUntouched();

    this.guardando = false;
    this.modo = 'crear';
    this.movimientoForm = null;
  }

  obtenerTipoMovimiento(): string {
    return this.formMovimiento.get('tipo')?.getRawValue();
  }

  cerrarModal(): void {
    this.resetearFormulario();
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
