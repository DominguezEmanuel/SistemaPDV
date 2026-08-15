import {
  Component,
  OnInit,
  OnChanges,
  SimpleChanges,
  EventEmitter,
  Input,
  Output,
  ViewChild,
  ElementRef,
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
import { finalize } from 'rxjs';
import { ProductoRequest, ProductoResponse } from '../../../models/Producto';
import { CategoriaResponse } from '../../../models/Categoria';
import { CategoriaService } from '../../../core/services/categoria-service';
import { ProductoService } from '../../../core/services/producto-service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-producto-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './producto-form.html',
  styleUrl: './producto-form.css',
})
export class ProductoForm implements OnInit, OnChanges {
  @Input() productoForm: ProductoResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() productoGuardado = new EventEmitter<{
    producto: ProductoResponse;
    accion: 'crear' | 'editar';
  }>();
  @ViewChild('inputImagen')
  inputImagen!: ElementRef<HTMLInputElement>;
  // Estructuras
  formProducto!: FormGroup;
  categorias: CategoriaResponse[] = [];
  nuevoProducto: ProductoRequest | null = null;
  imagenSeleccionada: File | null = null;
  previewImagen: string | null = null;
  imagenOriginal: string | null = null;
  modo: 'crear' | 'editar' = 'crear';
  guardando = false;

  constructor(
    private fb: FormBuilder,
    private categoriaService: CategoriaService,
    private productoService: ProductoService,
    private toastr: ToastrService,
  ) {
    this.formProducto = this.fb.group(this.getControlesFormulario());
  }

  ngOnInit(): void {
    this.cargarCategorias();
    this.limpiarCodigoBarras();
  }

  limpiarCodigoBarras(): void {
    this.formProducto
      .get('tieneVariantes')
      ?.valueChanges.subscribe((tieneVariantes) => {
        const codigoBarras = this.formProducto.get('codigoBarras');

        if (tieneVariantes === false) {
          // Producto sin variantes
          codigoBarras?.enable();
        } else if (tieneVariantes === true) {
          // Producto con variantes
          codigoBarras?.reset();
          codigoBarras?.disable();
        }
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && !changes['visible'].currentValue) {
      this.resetearFormulario();
    }

    if (changes['productoForm']) {
      if (this.productoForm) {
        this.cargarDatosProducto();
      } else {
        this.resetearFormulario();
      }
    }
  }

  private cargarDatosProducto(): void {
    if (!this.productoForm) {
      return;
    }

    this.modo = 'editar';

    this.formProducto.patchValue({
      nombre: this.productoForm.nombre,
      categoria: this.productoForm.categoria.idCategoria,
      imagen: this.productoForm.imagen,
      precioMinorista: this.productoForm.precioMinorista,
      precioMayorista: this.productoForm.precioMayorista,
      minimoMayorista: this.productoForm.minimoMayorista,
      tieneVariantes: this.productoForm.tieneVariantes,
    });

    // Deshabilitar el campo tieneVariantes en modo edición
    this.formProducto.get('tieneVariantes')?.disable();

    // Guardamos la imagen actual del producto
    this.imagenOriginal = this.productoForm.imagen;

    // La mostramos inicialmente
    this.previewImagen = this.productoForm.imagen;

    // Todavía no se seleccionó una nueva imagen
    this.imagenSeleccionada = null;
  }

  private resetearFormulario(): void {
    this.formProducto.reset({
      nombre: '',
      categoria: null,
      imagen: null,
      precioMinorista: null,
      precioMayorista: null,
      minimoMayorista: null,
      tieneVariantes: false,
    });

    // Re-habilitar el campo tieneVariantes para modo crear
    this.formProducto.get('tieneVariantes')?.enable();

    this.formProducto.markAsPristine();
    this.formProducto.markAsUntouched();

    this.imagenSeleccionada = null;
    this.previewImagen = null;
    this.imagenOriginal = null;
    this.nuevoProducto = null;
    this.guardando = false;
    this.modo = 'crear';

    if (this.inputImagen) {
      this.inputImagen.nativeElement.value = '';
    }
  }

  private getControlesFormulario() {
    return {
      nombre: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(3)],
      }),

      categoria: new FormControl<number | null>(null, {
        validators: [Validators.required],
      }),

      // La imagen solo será obligatoria al crear
      // Al editar, el formulario no debería de exigirla
      imagen: new FormControl<string | null>(null),

      precioMinorista: new FormControl<number | null>(null, {
        validators: [Validators.required, Validators.min(0.01)],
      }),

      precioMayorista: new FormControl<number | null>(null, {
        validators: [Validators.required, Validators.min(0.01)],
      }),

      minimoMayorista: new FormControl<number | null>(null, {
        validators: [
          Validators.required,
          Validators.min(1),
          Validators.pattern(/^[0-9]+$/),
        ],
      }),
      tieneVariantes: new FormControl<boolean>(false, {
        validators: [Validators.required],
      }),
      codigoBarras: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.maxLength(50)],
      }),
    };
  }

  procesarFormulario(): void {
    if (this.formProducto.invalid) {
      this.formProducto.markAllAsTouched();
      return;
    }

    // La imagen solamente es obligatoria al crear
    if (this.modo === 'crear' && !this.imagenSeleccionada) {
      this.formProducto.get('imagen')?.markAsTouched();
      return;
    }

    this.guardando = true;

    const formData = this.crearFormData();

    /* Mostrar contenido de FormData para depuración */
    const formDataDebug: { [key: string]: any } = {};
    formData.forEach((value, key) => {
      formDataDebug[key] = value instanceof File ? value.name : value;
    });
    console.log('FormData contenido:', formDataDebug);

    if (this.modo === 'crear') {
      this.crearProducto(formData);
    } else {
      this.actualizarProducto(formData);
    }
  }

  crearProducto(formData: FormData): void {
    this.productoService
      .crearProducto(formData)
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.productoGuardado.emit({ producto: response, accion: 'crear' });
          this.cerrarModal();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  private actualizarProducto(formData: FormData): void {
    if (!this.productoForm) {
      return;
    }

    this.productoService
      .actualizarProducto(this.productoForm.idProducto, formData)
      .pipe(
        finalize(() => {
          this.guardando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.productoGuardado.emit({
            producto: response,
            accion: 'editar',
          });
          this.cerrarModal();
        },

        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  crearFormData(): FormData {
    const formData = new FormData();

    // Usar getRawValue() para incluir controles deshabilitados (como tieneVariantes en edición)
    const formValues = this.formProducto.getRawValue();

    formData.append('nombre', formValues.nombre);
    formData.append('precioMinorista', formValues.precioMinorista);
    formData.append('precioMayorista', formValues.precioMayorista);
    formData.append('minimoMayorista', formValues.minimoMayorista);
    formData.append('idCategoria', formValues.categoria);
    formData.append('tieneVariantes', formValues.tieneVariantes);
    formData.append('codigoBarras', formValues.codigoBarras);
    // Solamente enviar la imagen si el usuario seleccionó una nueva
    if (this.imagenSeleccionada) {
      formData.append('imagen', this.imagenSeleccionada);
    }

    return formData;
  }

  cargarCategorias(): void {
    this.categoriaService.getAllCategorias().subscribe({
      next: (response) => {
        this.categorias = response;
      },
      error: (error) => {
        console.error('Error al obtener categorías:', error);
      },
    });
  }

  handleImagenChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) {
      return;
    }

    const archivo = input.files[0];

    if (!['image/jpeg', 'image/png'].includes(archivo.type)) {
      this.toastr.warning(
        'La imagen debe estar en formato JPG, JPEG o PNG',
        'Imagen no válida',
      );

      return;
    }

    // Controla que el archivo no supere 2MB
    if (archivo.size > 2 * 1024 * 1024) {
      this.toastr.warning(
        'La imagen no puede superar los 2 MB',
        'Imagen demasiado grande',
      );

      return;
    }

    this.imagenSeleccionada = archivo;

    this.formProducto.get('imagen')?.setValue(archivo.name);

    const reader = new FileReader();

    reader.onload = () => {
      this.previewImagen = reader.result as string;
    };

    reader.readAsDataURL(archivo);
  }

  quitarImagen(event: Event): void {
    event.stopPropagation();

    // Cancelar la nueva imagen seleccionada
    this.imagenSeleccionada = null;

    // Volver a mostrar la imagen original
    this.previewImagen = this.imagenOriginal;

    // Restaurar el valor original del formulario
    this.formProducto.get('imagen')?.setValue(this.imagenOriginal);

    // Limpiar el input file
    if (this.inputImagen) {
      this.inputImagen.nativeElement.value = '';
    }
  }

  cerrarModal(): void {
    this.resetearFormulario();
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
