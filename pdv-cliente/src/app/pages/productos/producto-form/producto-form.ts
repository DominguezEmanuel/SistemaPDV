import {
  Component,
  OnInit,
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
export class ProductoForm implements OnInit {
  @Input() producto: ProductoResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() productoCreado = new EventEmitter<ProductoResponse>();
  @ViewChild('inputImagen')
  inputImagen!: ElementRef<HTMLInputElement>;
  // Estructuras
  formProducto!: FormGroup;
  categorias: CategoriaResponse[] = [];
  nuevoProducto: ProductoRequest | null = null;
  imagenSeleccionada: File | null = null;
  previewImagen: string | null = null;

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

  private getControlesFormulario() {
    return {
      nombre: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(3)],
      }),

      categoria: new FormControl<number | null>(null, {
        validators: [Validators.required],
      }),

      imagen: new FormControl<string | null>(null, {
        nonNullable: true,
        validators: [Validators.required],
      }),

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

  procesarFormulario() {
    // Si acción es == registrar

    if (this.formProducto.invalid) {
      this.formProducto.markAllAsTouched();
      return;
    }

    if (!this.imagenSeleccionada) {
      this.formProducto.get('imagen')?.markAsTouched();
      return;
    }

    const formData = this.crearFormData();

    formData.forEach((valor, clave) => {
      console.log(clave, valor);
    });

    this.productoService.crearProducto(formData).subscribe({
      next: (response) => {
        //console.log('Producto creado:', response);
        this.productoCreado.emit(response);
        this.cerrarModal();
      },
      error: (error) => {
        //console.error('Error al crear el producto', error);
        this.toastr.error(error.error.mensaje, 'Error');
      },
    });
  }

  crearProducto(formData: FormData): void {
    this.productoService;
  }

  crearFormData(): FormData {
    const formData = new FormData();

    formData.append('nombre', this.formProducto.value.nombre);
    formData.append('precioMinorista', this.formProducto.value.precioMinorista);
    formData.append('precioMayorista', this.formProducto.value.precioMayorista);
    formData.append('minimoMayorista', this.formProducto.value.minimoMayorista);
    formData.append('idCategoria', this.formProducto.value.categoria);

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
      return;
    }

    // Controla que el archivo no supere 2MB
    if (archivo.size > 2 * 1024 * 1024) {
      return;
    }

    this.imagenSeleccionada = archivo;

    this.formProducto.get('imagen')?.setValue(archivo.name);
    this.formProducto.get('imagen')?.markAsTouched();
    this.formProducto.get('imagen')?.updateValueAndValidity();

    const reader = new FileReader();

    reader.onload = () => {
      this.previewImagen = reader.result as string;
    };

    reader.readAsDataURL(archivo);
  }

  quitarImagen(event: Event): void {
    event.stopPropagation();

    this.imagenSeleccionada = null;
    this.previewImagen = null;

    this.formProducto.get('imagen')?.setValue(null);

    this.formProducto.get('imagen')?.markAsTouched();

    this.formProducto.get('imagen')?.updateValueAndValidity();
  }

  cerrarModal(): void {
    this.formProducto.reset();

    this.formProducto.markAsPristine();
    this.formProducto.markAsUntouched();

    this.imagenSeleccionada = null;
    this.previewImagen = null;

    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
