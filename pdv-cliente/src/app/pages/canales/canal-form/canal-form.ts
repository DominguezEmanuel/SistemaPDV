import {
  Component,
  OnChanges,
  EventEmitter,
  Input,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
import { CanalRequest, CanalResponse } from '../../../models/Canal';
import { CanalService } from '../../../core/services/canal-service';
import { ToastrService } from 'ngx-toastr';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

@Component({
  selector: 'app-canal-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './canal-form.html',
  styleUrl: './canal-form.css',
})
export class CanalForm implements OnChanges {
  @Input() canal: CanalResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() canalGuardado = new EventEmitter<{
    canal: CanalResponse;
    accion: 'crear' | 'editar';
  }>();

  formCanal!: FormGroup;
  canalForm: CanalRequest | null = null;
  modo: 'crear' | 'editar' = 'crear';
  guardando = false;

  private valoresIniciales: {
    nombre: string;
  } | null = null;

  constructor(
    private fb: FormBuilder,
    private canalService: CanalService,
    private toastr: ToastrService,
  ) {
    this.formCanal = this.fb.group(this.getControlesFormulario());
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && !changes['visible'].currentValue) {
      this.resetearFormulario();
    }

    if (changes['canal']) {
      if (this.canal) {
        this.cargarDatosCanal();
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
          Validators.maxLength(50),
        ],
      }),
    };
  }

  private cargarDatosCanal(): void {
    if (!this.canal) {
      return;
    }

    this.modo = 'editar';

    const datosCanal = { nombre: this.canal.nombre };

    this.formCanal.patchValue(datosCanal);

    this.valoresIniciales = { ...datosCanal };

    this.formCanal.markAsPristine();
  }

  procesarFormulario(): void {
    if (this.formCanal.invalid) {
      this.formCanal.markAllAsTouched();
      return;
    }

    if (this.modo === 'editar' && !this.cambiosEnFormulario()) {
      this.toastr.info('No se realizaron cambios en el canal', 'Sin cambios');
      return;
    }

    this.guardando = true;

    this.asignarValores();

    if (this.modo === 'crear') {
      this.crearCanal();
    } else {
      this.actualizarCanal();
    }
  }

  crearCanal(): void {
    this.canalService
      .crearCanal(this.canalForm)
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.canalGuardado.emit({ canal: response, accion: 'crear' });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  actualizarCanal(): void {
    this.canalService
      .actualizarCanal(
        this.canal?.idCanalVenta ? this.canal.idCanalVenta : 0,
        this.canalForm,
      )
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.canalGuardado.emit({ canal: response, accion: 'editar' });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  asignarValores(): void {
    const valores = this.formCanal.getRawValue();

    this.canalForm = {
      nombre: valores.nombre,
    };
  }

  private cambiosEnFormulario(): boolean {
    if (!this.valoresIniciales) {
      return false;
    }

    const valoresActuales = this.formCanal.getRawValue();

    return valoresActuales.nombre !== this.valoresIniciales.nombre;
  }

  cerrarModal(): void {
    this.resetearFormulario();
    this.cerrar.emit();
  }

  resetearFormulario(): void {
    this.formCanal.reset({
      nombre: '',
    });

    this.formCanal.markAsPristine();
    this.formCanal.markAsUntouched();
    this.canalForm = null;
    this.guardando = false;
    this.modo = 'crear';
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
