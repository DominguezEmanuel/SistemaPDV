import {
  Component,
  OnChanges,
  EventEmitter,
  Input,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { finalize } from 'rxjs';
import { ProductoResponse } from '../../../models/Producto';
import { VarianteRequest, VarianteResponse } from '../../../models/Variante';
import { VarianteService } from '../../../core/services/variante-service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-variante-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './variante-form.html',
  styleUrl: './variante-form.css',
})
export class VarianteForm implements OnChanges {
  @Input() producto: ProductoResponse | null = null;
  @Input() variante: VarianteResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() varianteGuardada = new EventEmitter<{
    variante: VarianteResponse;
    accion: 'crear' | 'editar';
  }>();

  formVariante!: FormGroup;
  nuevaVariante: VarianteRequest | null = null;
  modo: 'crear' | 'editar' = 'crear';
  guardando = false;

  constructor(
    private fb: FormBuilder,
    private varianteService: VarianteService,
    private toastr: ToastrService,
  ) {
    this.formVariante = this.fb.group(this.getControlesFormulario());
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && !changes['visible'].currentValue) {
    }

    if (changes['variante']) {
      if (this.variante) {
        this.cargarDatosVariante();
      } else {
        this.resetearFormulario();
      }
    }
  }

  private getControlesFormulario() {
    return {
      nombre: new FormControl<string>('', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(150),
        ],
      }),
      codigoBarras: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required, Validators.maxLength(50)],
      }),
    };
  }

  private cargarDatosVariante(): void {
    if (!this.variante) {
      return;
    }

    this.modo = 'editar';

    this.formVariante.patchValue({
      nombre: this.variante.nombre,
      codigoBarras: this.variante.codigoBarras,
    });
  }

  procesarFormulario(): void {
    if (this.formVariante.invalid) {
      this.formVariante.markAllAsTouched();
      return;
    }

    this.guardando = true;

    this.asignarValores();

    console.log('Variante: ', this.nuevaVariante);

    if (this.modo === 'crear') {
      this.crearVariante();
    }
  }

  crearVariante(): void {
    this.varianteService
      .crearVariante(this.nuevaVariante)
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          console.log('Variante guardada: ', response);
          this.varianteGuardada.emit({ variante: response, accion: 'crear' });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  asignarValores(): void {
    const valores = this.formVariante.getRawValue();

    this.nuevaVariante = {
      nombre: valores.nombre,
      codigoBarras: valores.codigoBarras,
      idProducto: this.producto?.idProducto ? this.producto.idProducto : 0,
    };
  }

  cerrarModal(): void {
    this.resetearFormulario();
    this.cerrar.emit();
  }

  private resetearFormulario(): void {
    this.formVariante.reset({
      nombre: '',
      codigoBarras: '',
    });

    this.formVariante.markAsPristine();
    this.formVariante.markAsUntouched();
    this.nuevaVariante = null;
    this.guardando = false;
    this.modo = 'crear';
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
