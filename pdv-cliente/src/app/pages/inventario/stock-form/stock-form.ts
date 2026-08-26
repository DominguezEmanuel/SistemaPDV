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
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  FormsModule,
  FormControl,
  Validators,
} from '@angular/forms';
// Models
import { StockRequest, StockResponse } from '../../../models/Stock';
import { CanalResponse } from '../../../models/Canal';
import { ProductoResponse } from '../../../models/Producto';
import { VarianteResponse } from '../../../models/Variante';
// Services
import { ToastrService } from 'ngx-toastr';
import { StockService } from '../../../core/services/stock-service';
import { CanalService } from '../../../core/services/canal-service';
import { ProductoService } from '../../../core/services/producto-service';
import { VarianteService } from '../../../core/services/variante-service';
// Otros
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-stock-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './stock-form.html',
  styleUrl: './stock-form.css',
})
export class StockForm implements OnInit {
  @Input() stockForm: StockResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() stockGuardado = new EventEmitter<{
    stock: StockResponse | null;
    accion: 'crear' | 'editar';
  }>();

  formStock!: FormGroup;
  nuevoRegistroStock: StockRequest | null = null;
  stock: StockResponse | null = null;
  canales: CanalResponse[] = [];
  modo: 'crear' | 'editar' = 'crear';
  guardando = false;
  productos: ProductoResponse[] = [];
  variantes: VarianteResponse[] = [];
  productoSeleccionado: ProductoResponse | null = null;
  //cargandoVariantes = false;
  busquedaControl = new FormControl('');
  productoControl = new FormControl<ProductoResponse | null>(null);

  constructor(
    private fb: FormBuilder,
    private toastr: ToastrService,
    private stockService: StockService,
    private canalService: CanalService,
    private productoService: ProductoService,
    private varianteService: VarianteService,
  ) {
    this.formStock = this.fb.group(this.getControlesFormulario());
  }

  ngOnInit(): void {
    this.cargarCanales();
    // FormControl para la búsqueda dinámica de productos
    this.busquedaControl.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((texto) => {
        const busqueda = texto?.trim() ?? '';

        this.reiniciarCamposDependientes();

        if (busqueda.length < 2) {
          return;
        }

        this.buscarProductos(busqueda);
      });
    // FormControl para la elección de la lista de productos filtrados
    this.productoControl.valueChanges.subscribe((producto) => {
      if (!producto) {
        return;
      }

      this.seleccionarProducto(producto);
    });
  }

  private reiniciarCamposDependientes(): void {
    this.productos = [];
    this.productoControl.setValue(null);
    this.variantes = [];
    this.formStock.get('variante')?.reset(null);
    this.formStock.get('variante')?.disable();
    this.productoSeleccionado = null;
  }

  private getControlesFormulario() {
    return {
      variante: new FormControl<number | null>(
        { value: null, disabled: true },
        {
          validators: [Validators.required],
        },
      ),

      canal: new FormControl<number | null>(null, {
        validators: [Validators.required],
      }),

      disponible: new FormControl<number | null>(null, {
        validators: [
          Validators.required,
          Validators.min(1),
          Validators.pattern(/^[0-9]+$/),
        ],
      }),

      minimo: new FormControl<number | null>(null, {
        validators: [
          Validators.required,
          Validators.min(3),
          Validators.pattern(/^[0-9]+$/),
        ],
      }),
    };
  }

  buscarProductos(nombre: string): void {
    // Filtra los productos que contengan 'nombre' y estén activos
    this.productoService.buscarPorFiltros(0, 10, nombre, null, true).subscribe({
      next: (response) => {
        this.productos = response.content;
        if (this.productos.length === 0) {
          this.toastr.info(
            'Por favor, verifica que hayas escrito bien',
            'Sin resultados',
          );
        }
      },
      error: (error) => {
        this.toastr.error('Error al buscar productos', 'Error');
      },
    });
  }

  seleccionarProducto(producto: ProductoResponse): void {
    this.productoSeleccionado = producto;

    // Asigna el nombre del producto a la búsqueda y no dispara el evento de búsqueda
    this.busquedaControl.setValue(producto.nombre, { emitEvent: false });

    this.cargarVariantesPorProducto(producto.idProducto);
  }

  cargarVariantesPorProducto(idProducto: number): void {
    this.formStock.get('variante')?.reset(null);
    this.formStock.get('variante')?.disable();
    this.variantes = [];

    this.varianteService
      .obtenerVariantesPorProducto(idProducto)
      .pipe(finalize(() => {}))
      .subscribe({
        next: (response) => {
          this.variantes = response;
          this.controlarCampoVariante(this.variantes);
        },
        error: (error) => {
          this.variantes = [];
          this.formStock.get('variante')?.disable();
          this.formStock.get('variante')?.reset(null);
          this.formStock.get('variante')?.disable();
          this.toastr.error(
            'Error al cargar las variantes del productos',
            'Error',
          );
        },
      });
  }

  private controlarCampoVariante(variantes: VarianteResponse[]): void {
    if (this.productoSeleccionado?.tieneVariantes == false) {
      this.formStock.patchValue({ variante: variantes[0].idVariante });
      this.formStock.get('variante')?.disable();
      return;
    }

    this.formStock.get('variante')?.enable();
    this.formStock.get('variante')?.setValue(null);
  }

  procesarFormulario(): void {
    if (this.formStock.invalid) {
      this.formStock.markAllAsTouched();
      return;
    }

    this.guardando = true;

    this.asignarValores();

    if (this.modo === 'crear') {
      this.crearRegistroStock(this.nuevoRegistroStock);
    }
  }

  crearRegistroStock(request: StockRequest | null): void {
    this.stockService
      .crearRegistroStock(request)
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.stockGuardado.emit({ stock: response, accion: 'crear' });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Registro duplicado');
        },
      });
  }

  cargarCanales(): void {
    this.canalService.obtenerCanales().subscribe({
      next: (response) => {
        this.canales = response;
      },
      error: (error) => {
        this.toastr.error('Error al obtener los canales de venta', 'Error');
      },
    });
  }

  private asignarValores(): void {
    const valoresForm = this.formStock.getRawValue();

    this.nuevoRegistroStock = {
      cantidadDisponible: valoresForm.disponible,
      stockMinimo: valoresForm.minimo,
      idCanalVenta: valoresForm.canal,
      idVariante: valoresForm.variante,
    };
  }

  private resetearFormulario(): void {
    this.formStock.reset({
      producto: null,
      variante: null,
      canal: null,
      disponible: null,
      minimo: null,
    });

    this.formStock.markAsPristine();
    this.formStock.markAsUntouched();

    this.busquedaControl.setValue('');
    this.productoControl.setValue(null);

    this.nuevoRegistroStock = null;
    this.productoSeleccionado = null;
    this.productos = [];
    this.variantes = [];
    this.guardando = false;
    this.modo = 'crear';
  }

  cerrarModal(): void {
    this.resetearFormulario();
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
