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
import { FormsModule, FormControl } from '@angular/forms';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
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
  @Input() producto: ProductoResponse | null = null;
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
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['producto']) {
      if (this.producto) {
        this.cargarDatosProducto();
      } else {
        this.prepararFormularioCrear();
      }
    }
  }

  private cargarDatosProducto(): void {
    if (!this.producto) {
      return;
    }

    this.modo = 'editar';

    this.formProducto.patchValue({
      nombre: this.producto.nombre,
      categoria: this.producto.categoria.idCategoria,
      imagen: this.producto.imagen,
      precioMinorista: this.producto.precioMinorista,
      precioMayorista: this.producto.precioMayorista,
      minimoMayorista: this.producto.minimoMayorista,
    });

    // Guardamos la imagen actual del producto
    this.imagenOriginal = this.producto.imagen;

    // La mostramos inicialmente
    this.previewImagen = this.producto.imagen;

    // Todavía no se seleccionó una nueva imagen
    this.imagenSeleccionada = null;
  }

  private prepararFormularioCrear(): void {
    this.modo = 'crear';

    this.formProducto.reset();

    this.imagenSeleccionada = null;
    this.previewImagen = null;

    this.formProducto.get('imagen')?.setValue(null);

    this.formProducto.markAsPristine();
    this.formProducto.markAsUntouched();
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
    };
  }

  procesarFormulario(): void {
    // Si acción es == registrar

    if (this.formProducto.invalid) {
      this.formProducto.markAllAsTouched();
      return;
    }

    // La imagen solamente es obligatoria al crear
    if (this.modo === 'crear' && !this.imagenSeleccionada) {
      this.formProducto.get('imagen')?.markAsTouched();
      return;
    }

    const formData = this.crearFormData();

    // Mostrar contenido de FormData para depuración
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
    this.productoService.crearProducto(formData).subscribe({
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
    if (!this.producto) {
      return;
    }

    this.productoService
      .actualizarProducto(this.producto.idProducto, formData)
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

    formData.append('nombre', this.formProducto.value.nombre);
    formData.append('precioMinorista', this.formProducto.value.precioMinorista);
    formData.append('precioMayorista', this.formProducto.value.precioMayorista);
    formData.append('minimoMayorista', this.formProducto.value.minimoMayorista);
    formData.append('idCategoria', this.formProducto.value.categoria);
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
    this.formProducto.reset();

    this.formProducto.markAsPristine();
    this.formProducto.markAsUntouched();

    this.imagenSeleccionada = null;
    this.previewImagen = null;
    this.imagenOriginal = null;

    if (this.inputImagen) {
      this.inputImagen.nativeElement.value = '';
    }

    this.modo = 'crear';

    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
