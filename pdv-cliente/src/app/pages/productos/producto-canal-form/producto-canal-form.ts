import {
  Component,
  OnInit,
  OnChanges,
  SimpleChanges,
  EventEmitter,
  Input,
  Output,
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
import {
  ProductoCanalResponse,
  ProductoCanalRequest,
} from '../../../models/ProductoCanal';
import { CanalResponse } from '../../../models/Canal';
import { ProductoResponse } from '../../../models/Producto';
// Services
import { CanalService } from '../../../core/services/canal-service';
import { ProductoCanalService } from '../../../core/services/producto-canal-service';
import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../../core/services/alert-service';
// Others
import { finalize } from 'rxjs/operators';
@Component({
  selector: 'app-producto-canal-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './producto-canal-form.html',
  styleUrl: './producto-canal-form.css',
})
export class ProductoCanalForm implements OnInit, OnChanges {
  // Variables para el formulario de registro/edición de Configuración Comercial
  @Input() producto: ProductoResponse | null = null;
  @Input() configuracion: ProductoCanalResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() configuracionGuardada = new EventEmitter<{
    configuracion: ProductoCanalResponse;
    accion: 'crear' | 'editar';
  }>();

  // Variables del formulario
  formConfiguracion!: FormGroup;
  configuracionForm: ProductoCanalRequest | null = null;
  modo: 'crear' | 'editar' = 'crear';
  guardando = false;
  canales: CanalResponse[] = [];

  ngOnInit(): void {
    this.cargarCanales();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && !changes['visible'].currentValue) {
      this.resetearFormulario();
    }

    if (changes['configuracion']) {
      if (this.configuracion) {
        this.cargarDatosConfiguracion();
      } else {
        this.resetearFormulario();
      }
    }
  }

  constructor(
    private fb: FormBuilder,
    private canalService: CanalService,
    private configuracionService: ProductoCanalService,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {
    this.formConfiguracion = this.fb.group(this.getControlesFormulario());
  }

  private getControlesFormulario() {
    return {
      canal: new FormControl<number | null>(null, {
        validators: [Validators.required],
      }),
      limiteMayorista: new FormControl<number | null>(null, {
        validators: [
          Validators.required,
          Validators.min(1),
          Validators.pattern(/^[0-9]+$/),
        ],
      }),
    };
  }

  cargarCanales(): void {
    this.canalService.obtenerCanales().subscribe({
      next: (response) => {
        this.canales = response;
      },
      error: (error) => {
        this.toastr.error('Error al cargar los canales de venta', 'Error');
      },
    });
  }

  private cargarDatosConfiguracion(): void {
    if (!this.configuracion) {
      return;
    }

    this.modo = 'editar';

    this.formConfiguracion.patchValue({
      canal: this.configuracion.idCanalVenta,
      limiteMayorista: this.configuracion.limiteMayorista,
    });

    this.formConfiguracion.get('canal')?.disable();
  }

  procesarFormulario(): void {
    if (this.formConfiguracion.invalid) {
      this.formConfiguracion.markAllAsTouched();
      return;
    }

    this.guardando = true;

    this.asignarValores();

    if (this.modo === 'crear') {
      this.guardarConfiguracion();
    } else {
      this.editarConfiguracion();
    }
  }

  guardarConfiguracion(): void {
    if (!this.configuracionForm) {
      return;
    }

    this.configuracionService
      .crearConfiguracionProductoCanal(this.configuracionForm)
      .pipe(finalize(() => (this.guardando = false)))
      .subscribe({
        next: (response) => {
          this.configuracionGuardada.emit({
            configuracion: response,
            accion: 'crear',
          });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  editarConfiguracion(): void {
    if (!this.configuracion || !this.configuracionForm) {
      return;
    }

    this.configuracionService
      .editarLimiteMayoristaConfiguracion(
        this.configuracion.idProductoCanal,
        this.configuracionForm.limiteMayorista,
      )
      .pipe(finalize(() => (this.guardando = false)))
      .subscribe({
        next: (response) => {
          this.configuracionGuardada.emit({
            configuracion: response,
            accion: 'editar',
          });
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

    const valores = this.formConfiguracion.getRawValue();

    this.configuracionForm = {
      idProducto: this.producto.idProducto,
      idCanalVenta: valores.canal,
      limiteMayorista: valores.limiteMayorista,
    };
  }

  private resetearFormulario(): void {
    this.formConfiguracion.reset({
      canal: null,
      limiteMayorista: null,
    });

    this.formConfiguracion.markAsPristine();
    this.formConfiguracion.markAsUntouched();

    this.formConfiguracion.get('canal')?.enable();

    this.configuracionForm = null;
    this.modo = 'crear';
    this.guardando = false;
  }

  cerrarModal(): void {
    this.resetearFormulario();
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
