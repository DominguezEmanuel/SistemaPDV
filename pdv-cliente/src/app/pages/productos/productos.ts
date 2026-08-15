import { Component, OnInit } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CategoriaService } from '../../core/services/categoria-service';
import { ProductoService } from '../../core/services/producto-service';
import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../core/services/alert-service';
import { CategoriaResponse } from '../../models/Categoria';
import { ProductoResponse } from '../../models/Producto';
import { ProductViewModalComponent } from './product-view-modal/product-view-modal.component';
import { ProductoForm } from './producto-form/producto-form';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-productos',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ProductViewModalComponent,
    ProductoForm,
  ],
  templateUrl: './productos.html',
  styleUrl: './productos.css',
})
export class Productos implements OnInit {
  // Estructuras utilizadas
  categorias: CategoriaResponse[] = [];
  productos: ProductoResponse[] = [];
  idCategoria: number | null = null;
  estado: boolean | null = null;
  busquedaControl = new FormControl('');

  private readonly paletasCategorias = [
    { bg: '#fbe9f0', color: '#c7638f' },
    { bg: '#e7f5ff', color: '#2d77b5' },
    { bg: '#f3ecff', color: '#7b61ff' },
    { bg: '#e8f7ec', color: '#2e9e5b' },
    { bg: '#fff4de', color: '#b06a00' },
    { bg: '#ffeef3', color: '#c94b7f' },
    { bg: '#eaf6f2', color: '#2f6f5b' },
    { bg: '#fef2f2', color: '#c13b3b' },
  ];

  modalVisible = false;
  modalFormularioProducto = false;
  productoSeleccionado: ProductoResponse | null = null;

  constructor(
    private categoriaService: CategoriaService,
    private productoService: ProductoService,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {}

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarProductos();
    // Importante para el filtrado de los productos
    this.busquedaControl.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(() => {
        this.aplicarFiltros();
      });
  }

  aplicarFiltros(): void {
    const nombre = this.busquedaControl.value?.trim() ?? '';

    this.productoService
      .buscarPorFiltros(nombre, this.idCategoria, this.estado)
      .subscribe({
        next: (response) => {
          this.productos = response;
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  limpiarFiltros(): void {
    this.busquedaControl.setValue('');

    this.idCategoria = null;

    this.estado = null;

    this.aplicarFiltros();
  }

  verFormulario(producto?: ProductoResponse): void {
    if (producto) {
      this.productoSeleccionado = producto;
    } else {
      this.productoSeleccionado = null;
    }
    this.modalFormularioProducto = true;
  }

  verProducto(producto: ProductoResponse): void {
    this.productoSeleccionado = producto;
    this.modalVisible = true;
  }

  cerrarModal(): void {
    if (this.modalVisible) {
      this.modalVisible = false;
    } else {
      this.modalFormularioProducto = false;
    }
    this.productoSeleccionado = null;
  }

  cargarCategorias(): void {
    this.categoriaService.getAllCategorias().subscribe({
      next: (response) => {
        this.categorias = response;
      },
      error: (error) => {
        this.toastr.error('Error al cargar las categorías', 'Error');
      },
    });
  }

  cargarProductos(): void {
    this.productoService.getAllProductos().subscribe({
      next: (response) => {
        this.productos = response;
      },
      error: (error) => {
        this.toastr.error('Error al cargar los productos', 'Error');
      },
    });
  }

  cambiarEstadoProducto(
    idProducto: number,
    nombre: string,
    activo: boolean,
  ): void {
    this.alertService
      .confirmarCambioEstado('producto', nombre, activo)
      .then((result) => {
        if (result.isConfirmed) {
          this.productoService
            .actualizarEstadoProducto(idProducto, activo)
            .subscribe({
              next: (response) => {
                this.toastr.success(
                  'Estado del producto actualizado correctamente',
                  'Éxito',
                );
                this.cargarProductos();
              },
              error: (error) => {
                this.toastr.error(error.error.mensaje, 'Error');
              },
            });
        }
      });
  }

  onProductoGuardado(event: {
    producto: ProductoResponse;
    accion: 'crear' | 'editar';
  }): void {
    this.modalFormularioProducto = false;

    this.cargarProductos();

    if (event.accion === 'crear') {
      this.toastr.success(
        'El producto se creó correctamente',
        'Producto creado',
      );
    } else {
      this.toastr.success(
        'Los cambios del producto se guardaron correctamente',
        'Producto actualizado',
      );
    }
  }

  getCategoriaStyle(
    categoria?: CategoriaResponse | null,
  ): Record<string, string> {
    const seed = categoria?.idCategoria ?? categoria?.nombre?.length ?? 0;
    const palette =
      this.paletasCategorias[seed % this.paletasCategorias.length];

    return {
      '--cat-bg': palette.bg,
      '--cat-color': palette.color,
    };
  }

  formatearMoneda(valor: number): string {
    return valor.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS',
    });
  }
}
