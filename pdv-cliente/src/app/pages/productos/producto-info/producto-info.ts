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
// Services
import { ProductoService } from '../../../core/services/producto-service';
import { VarianteService } from '../../../core/services/variante-service';
import { ProductoCanalService } from '../../../core/services/producto-canal-service';
import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../../core/services/alert-service';
// Others
import { VarianteForm } from '../../variantes/variante-form/variante-form';

interface ConfiguracionCanal {
  idCanal: number;
  nombreCanal: string;
  cantidad: number;
}

type TabId = 'informacion' | 'variantes' | 'stock' | 'comercial';

@Component({
  selector: 'app-product-view-modal',
  imports: [CommonModule, VarianteForm],
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
  //variantesCargadas = false;
  //canalesCargados = false;

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

  canales: CanalResponse[] = [];
  stock: StockResponse | null = null;
  configuracionCanales: ConfiguracionCanal[] = [];
  totalStockPorCanal: ConfiguracionCanal[] = [];

  // Panel Variantes
  variantes: VarianteResponse[] = [];

  // Panel Stock
  stocks: StockProductoResponse[] = [];
  stockLocalFisico!: number;
  stockTikTok!: number;

  constructor(
    private productoService: ProductoService,
    private varianteService: VarianteService,
    private productoCanalService: ProductoCanalService,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    // Evalúa si el componente ha recibido un nuevo producto a través del @Input()
    // Se asegura que dicho producto sea válido antes de ejecutar la lógica
    if (changes['producto'] && this.producto) {
      this.resetearStock();
      this.cargarVariantes();
      this.cargarStockProducto();
    }
  }

  cargarVariantes(): void {
    if (!this.producto) {
      return;
    }

    this.varianteService
      .obtenerVariantesPorProducto(this.producto.idProducto)
      .subscribe({
        next: (response) => {
          this.variantes = response;
        },
        error: (error) => {
          console.log('Error:', error);
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

  onVarianteGuardada(event: {
    variante: VarianteResponse;
    accion: 'crear' | 'editar';
  }): void {
    // Cargar nuevamente el listado de variantes
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

  cargarProductosCanales() {
    if (!this.producto || !this.canales.length) {
      this.configuracionCanales = [];
      return;
    }

    this.configuracionCanales = [];

    this.canales.forEach((canal) => {
      this.productoCanalService
        .findByCanalAndProducto(canal.idCanalVenta, this.producto?.idProducto)
        .subscribe({
          next: (response) => {
            this.configuracionCanales.push({
              idCanal: canal.idCanalVenta,
              nombreCanal: canal.nombre,
              cantidad: response.limiteMayorista ?? 0,
            });
          },
          error: (error) => {
            console.error('Error:', error);
            this.configuracionCanales.push({
              idCanal: canal.idCanalVenta,
              nombreCanal: canal.nombre,
              cantidad: 0,
            });
          },
        });
    });
  }

  resetearStock(): void {
    this.totalStockPorCanal = [];
    this.configuracionCanales = [];
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
                this.cargarVariantes();
              },
              error: (error) => {
                this.toastr.error(error.error.mensaje, 'Error');
              },
            });
        }
      });
  }

  cargarStockProducto(): void {
    if (!this.producto) {
      return;
    }

    this.productoService
      .obtenerStockProducto(this.producto.idProducto)
      .subscribe({
        next: (response) => {
          this.stocks = response;
          this.calcularStockTotalPorCanal();
        },
        error: (error) => {
          console.log('Error: ', error);
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
  asignarEstadoStock(cantidadDisponible: number, stockMinimo: number): string {
    if (cantidadDisponible <= 0) {
      return 'Sin stock';
    }

    if (cantidadDisponible <= stockMinimo) {
      return 'Stock bajo';
    }

    return 'Disponible';
  }

  obtenerClaseEstado(cantidad: number, minimo: number): string {
    if (cantidad <= 0) {
      return 'sin-stock';
    }

    if (cantidad <= minimo) {
      return 'stock-bajo';
    }

    return 'disponible';
  }

  // Funciones para los TABS
  seleccionarTab(id: TabId): void {
    this.tabActiva = id;

    if (id === 'comercial') {
      this.cargarProductosCanales();
    }
  }

  esTabActiva(id: TabId): boolean {
    return this.tabActiva === id;
  }

  // Limpia todas las variables/estructuras utilizadas en este componente
  private limpiarDatos(): void {
    this.variantes = [];
    this.canales = [];
    this.stock = null;
    this.configuracionCanales = [];
    this.totalStockPorCanal = [];
    this.tabActiva = 'informacion';
  }

  cerrarModal(): void {
    if (this.modalFormularioVariante) {
      this.modalFormularioVariante = false;
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
