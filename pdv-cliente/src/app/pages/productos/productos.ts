import { Component, OnInit } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
// Services
import { CategoriaService } from '../../core/services/categoria-service';
import { ProductoService } from '../../core/services/producto-service';
import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../core/services/alert-service';
// Models
import { CategoriaResponse } from '../../models/Categoria';
import { ProductoResponse } from '../../models/Producto';
// Others
import { ProductViewModalComponent } from './producto-info/producto-info';
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

  // Variables para paginación
  paginaActual: number = 0;
  tamanioPagina: number = 0;
  totalProductos: number = 0;
  totalPaginas: number = 0;

  // Variables para visibilidad de Modal/Formulario
  modalVisible = false;
  modalFormularioProducto = false;
  productoSeleccionado: ProductoResponse | null = null;
  productoSeleccionadoForm: ProductoResponse | null = null;

  constructor(
    private categoriaService: CategoriaService,
    private productoService: ProductoService,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {}

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarProductos();
    // Detecta cambios en el 'busquedaControl' para el filtrado de los productos
    this.busquedaControl.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(() => {
        this.aplicarFiltros();
      });
  }

  aplicarFiltros(): void {
    this.paginaActual = 0;
    const nombre = this.busquedaControl.value?.trim() ?? '';

    this.productoService
      .buscarPorFiltros(
        this.paginaActual,
        this.tamanioPagina,
        nombre,
        this.idCategoria,
        this.estado,
      )
      .subscribe({
        next: (response) => {
          this.productos = response.content;
          this.totalProductos = response.page.totalElements;
          this.totalPaginas = response.page.totalPages;
          this.paginaActual = response.page.number;
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
      this.productoSeleccionadoForm = producto;
    } else {
      this.productoSeleccionadoForm = null;
    }
    this.modalFormularioProducto = true;
  }

  verProducto(producto: ProductoResponse): void {
    this.productoSeleccionado = producto;
    this.modalVisible = true;
  }

  cargarCategorias(): void {
    this.categoriaService.obtenerCategorias().subscribe({
      next: (response) => {
        this.categorias = response;
      },
      error: (error) => {
        this.toastr.error('Error al cargar las categorías', 'Error');
      },
    });
  }

  cargarProductos(): void {
    this.productoService.obtenerProductos().subscribe({
      next: (response) => {
        this.totalProductos = response.page.totalElements;
        this.totalPaginas = response.page.totalPages;
        this.paginaActual = response.page.number;
        this.productos = response.content;
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

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i);
  }

  cambiarPagina(pagina: number): void {
    if (pagina < 0 || pagina >= this.totalPaginas) {
      return;
    }

    this.paginaActual = pagina;

    console.log('Página actual: ', this.paginaActual);

    this.cargarProductos();
  }

  cerrarModal(): void {
    // Reestablece las variables usadas para el modal/formulario
    if (this.modalVisible) {
      // Modal de información de producto
      this.modalVisible = false;
      this.productoSeleccionado = null;
    } else {
      // Formulario de registro/edición de producto
      this.modalFormularioProducto = false;
      this.productoSeleccionadoForm = null;
    }
  }

  // Devuelve el estilo CSS que debe tener la categoría
  obtenerEstiloCategoria(nombre: string): string {
    switch (nombre.toLowerCase()) {
      case 'uñas':
        return 'unias';
      case 'pestañas':
        return 'pestanias';
      case 'maquillaje':
        return 'maquillaje';
      case 'skincare':
        return 'skincare';
      case 'depilacion':
        return 'depilacion';
      case 'peinados':
        return 'peinados';
      case 'perfumeria':
        return 'perfumeria';
      case 'pedicura':
        return 'pedicura';
      default:
        return '';
    }
  }

  // Formatea 'valor' a moneda ARS
  formatearMoneda(valor: number): string {
    return valor.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS',
    });
  }

  // Formatea el 'id' al siguiente formato 'ID-0001'
  formatearIdRegistro(id: number): string {
    return `ID-${id.toString().padStart(4, '0')}`;
  }
}
