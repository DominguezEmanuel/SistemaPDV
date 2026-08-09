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
  @ViewChild('inputImagen')
  inputImagen!: ElementRef<HTMLInputElement>;
  // Estructuras
  formProducto!: FormGroup;
  categorias: CategoriaResponse[] = [];
  nuevoProducto: ProductoRequest | null = null;
  previewImagen: string | null = null;
  imagenSeleccionada: File | null = null;

  constructor(
    private fb: FormBuilder,
    private categoriaService: CategoriaService,
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

      imagen: new FormControl<string>('', {
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

  procesarFormulario(): void {}

  asignarValores(): void {
    const valores = this.formProducto.getRawValue();

    this.nuevoProducto = {
      nombre: valores.nombre,
      imagen: valores.imagen,
      precioMinorista: valores.precioMinorista,
      precioMayorista: valores.precioMayorista,
      minimoMayorista: valores.minimoMayorista,
      idCategoria: valores.categoria,
    };
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

    if (archivo.size > 2 * 1024 * 1024) {
      return;
    }

    this.imagenSeleccionada = archivo;

    this.formProducto.get('imagen')?.setValue(archivo);
    this.formProducto.get('imagen')?.markAsTouched();

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

    this.inputImagen.nativeElement.value = '';

    this.formProducto.get('imagen')?.reset();
  }

  cerrarModal(): void {
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }
}
