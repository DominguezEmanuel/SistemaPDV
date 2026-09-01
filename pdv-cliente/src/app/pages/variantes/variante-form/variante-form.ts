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
// Models
import { ProductoResponse } from '../../../models/Producto';
import { VarianteRequest, VarianteResponse } from '../../../models/Variante';
// Services
import { VarianteService } from '../../../core/services/variante-service';
import { ToastrService } from 'ngx-toastr';
// Others
import { finalize } from 'rxjs';

@Component({
  selector: 'app-variante-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './variante-form.html',
  styleUrl: './variante-form.css',
})
export class VarianteForm implements OnChanges {
  // Variables de entrada y salida del componente
  @Input() producto: ProductoResponse | null = null;
  @Input() variante: VarianteResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() varianteGuardada = new EventEmitter<{
    variante: VarianteResponse;
    accion: 'crear' | 'editar';
  }>();

  // Variables del formulario
  formVariante!: FormGroup;
  varianteForm: VarianteRequest | null = null;
  modo: 'crear' | 'editar' = 'crear';
  guardando = false;

  private valoresIniciales: {
    nombre: string;
    codigoBarras: string | null;
  } | null = null;

  constructor(
    private fb: FormBuilder,
    private varianteService: VarianteService,
    private toastr: ToastrService,
  ) {
    this.formVariante = this.fb.group(this.getControlesFormulario());
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && !changes['visible'].currentValue) {
      this.resetearFormulario();
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

    const datosVariante = {
      nombre: this.variante.nombre,
      codigoBarras: this.variante.codigoBarras,
    };

    this.formVariante.patchValue(datosVariante);

    this.valoresIniciales = { ...datosVariante };

    this.formVariante.markAsPristine();
  }

  procesarFormulario(): void {
    if (this.formVariante.invalid) {
      this.formVariante.markAllAsTouched();
      return;
    }

    if (this.modo === 'editar' && !this.cambiosEnFormulario()) {
      this.toastr.info(
        'No se realizaron cambios en el formulario',
        'Información',
      );
      return;
    }

    this.guardando = true;

    this.asignarValores();

    if (this.modo === 'crear') {
      this.crearVariante();
    } else {
      this.actualizarVariante();
    }
  }

  crearVariante(): void {
    this.varianteService
      .crearVariante(this.varianteForm)
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

  actualizarVariante(): void {
    if (!this.variante) {
      return;
    }

    this.varianteService
      .actualizarVariante(this.variante.idVariante, this.varianteForm)
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.varianteGuardada.emit({ variante: response, accion: 'editar' });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  private asignarValores(): void {
    if (!this.producto) {
      return;
    }

    const valores = this.formVariante.getRawValue();

    this.varianteForm = {
      nombre: valores.nombre,
      codigoBarras: valores.codigoBarras,
      idProducto: this.producto.idProducto,
    };
  }

  private cambiosEnFormulario(): boolean {
    if (!this.valoresIniciales) {
      return false;
    }

    const valoresActuales = this.formVariante.getRawValue();

    return (
      valoresActuales.nombre !== this.valoresIniciales.nombre ||
      valoresActuales.codigoBarras !== this.valoresIniciales.codigoBarras
    );
  }

  cerrarModal(): void {
    this.resetearFormulario();
    this.cerrar.emit();
  }

  // Limpia el formulario de variante
  private resetearFormulario(): void {
    this.formVariante.reset({
      nombre: '',
      codigoBarras: '',
    });

    this.formVariante.markAsPristine();
    this.formVariante.markAsUntouched();
    this.varianteForm = null;
    this.guardando = false;
    this.modo = 'crear';
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
