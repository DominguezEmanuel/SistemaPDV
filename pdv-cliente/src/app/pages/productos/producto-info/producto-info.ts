import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
// Models
import {
  ProductoResponse,
  StockProductoResponse,
} from '../../../models/Producto';
import { CanalResponse } from '../../../models/Canal';
import { VarianteResponse } from '../../../models/Variante';
import { StockResponse } from '../../../models/Stock';
import { ProductoCanalResponse } from '../../../models/ProductoCanal';
// Services
import { ProductoService } from '../../../core/services/producto-service';
import { VarianteService } from '../../../core/services/variante-service';
import { ProductoCanalService } from '../../../core/services/producto-canal-service';
import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../../core/services/alert-service';
// Others
import { VarianteForm } from '../../variantes/variante-form/variante-form';
import { ProductoCanalForm } from '../../productos/producto-canal-form/producto-canal-form';

type TabId = 'informacion' | 'variantes' | 'stock' | 'comercial';

@Component({
  selector: 'app-product-view-modal',
  imports: [CommonModule, VarianteForm, ProductoCanalForm],
  templateUrl: './producto-info.html',
  styleUrls: [
    './producto-info.css',
    './section-stock.css',
    './section-info.css',
    './section-variants.css',
  ],
})
export class ProductViewModalComponent implements OnChanges {
  // Estructuras utilizadas
  @Input() producto: ProductoResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();

  // Variables para el formulario de registro/edicion de Variante
  modalFormularioVariante = false;
  varianteSeleccionada: VarianteResponse | null = null;

  // Estructura para las secciones de la vista
  tabs: { id: TabId; label: string; icon: string }[] = [
    {
      id: 'informacion',
      label: 'Información',
      icon: 'bi bi-info-circle',
    },
    {
      id: 'variantes',
      label: 'Variantes del producto',
      icon: 'bi bi-boxes',
    },
    { id: 'stock', label: 'Stock', icon: 'bi bi-columns-gap' },
    {
      id: 'comercial',
      label: 'Configuración Comercial',
      icon: 'bi bi-tag',
    },
  ];

  tabActiva: TabId = 'informacion';

  // Panel Variantes
  variantes: VarianteResponse[] = [];
  variantesCargadas = false;

  // Panel Stock
  stocks: StockProductoResponse[] = [];
  stockLocalFisico!: number;
  stockTikTok!: number;
  stockCargado = false;

  // Panel Configuración Comercial
  configuracionesComerciales: ProductoCanalResponse[] = [];
  configuracionesCargadas = false;
  formConfiguracionVisible = false;
  configuracionSeleccionada: ProductoCanalResponse | null = null;

  constructor(
    private productoService: ProductoService,
    private varianteService: VarianteService,
    private configuracionService: ProductoCanalService,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    // Evalúa si el componente ha recibido un nuevo producto a través del @Input()
    // Se asegura que dicho producto sea válido antes de ejecutar la lógica
    if (changes['producto'] && this.producto) {
      this.limpiarDatos();
    }
  }

  cargarVariantes(): void {
    if (!this.producto || this.variantesCargadas) {
      return;
    }

    this.varianteService
      .obtenerVariantesPorProducto(this.producto.idProducto)
      .subscribe({
        next: (response) => {
          this.variantes = response;
          this.variantesCargadas = true;
        },
        error: (error) => {
          this.toastr.error(
            'Error al cargar las variantes del producto',
            'Error',
          );
        },
      });
  }

  // Métodos para el panel de variantes del producto
  verFormulario(variante?: VarianteResponse): void {
    if (variante) {
      this.varianteSeleccionada = variante;
    } else {
      this.varianteSeleccionada = null;
    }
    this.modalFormularioVariante = true;
  }

  verFormularioConfiguracion(configuracion?: ProductoCanalResponse): void {
    if (configuracion) {
      this.configuracionSeleccionada = configuracion;
    } else {
      this.configuracionSeleccionada = null;
    }
    this.formConfiguracionVisible = true;
  }

  onVarianteGuardada(event: {
    variante: VarianteResponse;
    accion: 'crear' | 'editar';
  }): void {
    // Cargar nuevamente el listado de variantes
    this.variantesCargadas = false;
    this.cargarVariantes();

    if (event.accion === 'crear') {
      this.toastr.success(
        'La variante se creó correctamente',
        'Variante creada',
      );
    } else {
      this.toastr.success(
        'Los cambios de la variante se guardaron correctamente',
        'Variante actualizada',
      );
    }
  }

  onConfiguracionGuardada(event: {
    configuracion: ProductoCanalResponse;
    accion: 'crear' | 'editar';
  }): void {
    // Cargar nuevamente el listado de configuraciones comerciales
    this.configuracionesCargadas = false;
    this.cargarProductosCanales();

    if (event.accion === 'crear') {
      this.toastr.success(
        'La configuración comercial se creó correctamente',
        'Configuración creada',
      );
    } else {
      this.toastr.success(
        'Los cambios de la configuración comercial se guardaron correctamente',
        'Configuración actualizada',
      );
    }
  }

  cargarProductosCanales() {
    if (!this.producto || this.configuracionesCargadas) {
      return;
    }

    this.productoService
      .obtenerConfiguracionesProducto(this.producto.idProducto)
      .subscribe({
        next: (response) => {
          this.configuracionesComerciales = response;
          this.configuracionesCargadas = true;
        },
        error: (error) => {
          this.toastr.error(
            'Error al cargar la configuración comercial del producto',
            'Error',
          );
        },
      });
  }

  cambiarEstadoVariante(
    idVariante: number,
    nombre: string,
    activo: boolean,
  ): void {
    this.alertService
      .confirmarCambioEstadoVariante(nombre, activo)
      .then((result) => {
        if (result.isConfirmed) {
          this.varianteService
            .cambiarEstadoVariante(idVariante, activo)
            .subscribe({
              next: (response) => {
                this.toastr.success(
                  'Estado de la variante actualizado correctamente',
                  'Éxito',
                );
                this.variantesCargadas = false;
                this.cargarVariantes();
              },
              error: (error) => {
                this.toastr.error(error.error.mensaje, 'Error');
              },
            });
        }
      });
  }

  eliminarConfiguracion(idConfiguracion: number, nombre: string): void {
    this.alertService
      .confirmarEliminacionConfiguracion(nombre)
      .then((result) => {
        if (result.isConfirmed) {
          this.configuracionService
            .eliminarConfiguracionProductoCanal(idConfiguracion)
            .subscribe({
              next: (response) => {
                console.log(response);
                this.toastr.success(
                  'La configuración comercial se eliminó correctamente',
                  'Éxito',
                );
                this.configuracionesCargadas = false;
                this.cargarProductosCanales();
              },
              error: (error) => {
                console.log(error);
                this.toastr.error(error.error.mensaje, 'Error');
              },
            });
        }
      });
  }

  cargarStockProducto(): void {
    if (!this.producto || this.stockCargado) {
      return;
    }

    this.productoService
      .obtenerStockProducto(this.producto.idProducto)
      .subscribe({
        next: (response) => {
          this.stocks = response;
          this.stockCargado = true;
          this.calcularStockTotalPorCanal();
        },
        error: (error) => {
          this.toastr.error('Error al cargar el stock del producto', 'Error');
        },
      });
  }

  private calcularStockTotalPorCanal(): void {
    if (!this.stocks) {
      return;
    }

    this.stockLocalFisico = 0;
    this.stockTikTok = 0;

    this.stocks.forEach((stock) => {
      if (stock.nombreCanalVenta === 'Local Fisico') {
        this.stockLocalFisico += stock.cantidadDisponible;
      } else {
        this.stockTikTok += stock.cantidadDisponible;
      }
    });
  }

  // Métodos para el estado del Stock
  asignarEstadoStock(estado: string): string {
    if (estado === 'SIN_STOCK') {
      return 'Sin stock';
    }

    if (estado === 'STOCK_BAJO') {
      return 'Stock bajo';
    }

    return 'Disponible';
  }

  obtenerClaseEstado(estado: string): string {
    if (estado === 'SIN_STOCK') {
      return 'sin-stock';
    }

    if (estado === 'STOCK_BAJO') {
      return 'stock-bajo';
    }

    return 'disponible';
  }

  // Funciones para los TABS
  seleccionarTab(tab: TabId): void {
    this.tabActiva = tab;

    switch (tab) {
      case 'variantes':
        this.cargarVariantes();
        break;

      case 'stock':
        this.cargarStockProducto();
        break;

      case 'comercial':
        this.cargarProductosCanales();
        break;
    }
  }

  esTabActiva(tab: TabId): boolean {
    return this.tabActiva === tab;
  }

  // Limpia todas las variables/estructuras utilizadas en este componente
  private limpiarDatos(): void {
    this.variantes = [];
    this.stocks = [];
    this.configuracionesComerciales = [];

    this.variantesCargadas = false;
    this.stockCargado = false;
    this.configuracionesCargadas = false;

    this.varianteSeleccionada = null;
    this.tabActiva = 'informacion';
  }

  cerrarModal(): void {
    if (this.modalFormularioVariante) {
      this.modalFormularioVariante = false;
    } else if (this.formConfiguracionVisible) {
      this.formConfiguracionVisible = false;
    } else {
      this.limpiarDatos();
      this.cerrar.emit();
    }
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }

  formatearMoneda(valor: number): string {
    return valor.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS',
    });
  }
}
