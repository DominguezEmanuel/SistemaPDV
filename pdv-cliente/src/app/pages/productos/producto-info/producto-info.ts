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
import { ProductoResponse } from '../../../models/Producto';
import { CanalResponse } from '../../../models/Canal';
import { VarianteResponse } from '../../../models/Variante';
import { StockResponse } from '../../../models/Stock';
// Services
import { VarianteService } from '../../../core/services/variante-service';
import { CanalService } from '../../../core/services/canal-service';
import { StockService } from '../../../core/services/stock-service';
import { ProductoCanalService } from '../../../core/services/producto-canal-service';
import { ToastrService } from 'ngx-toastr';
import { AlertService } from '../../../core/services/alert-service';
// Otros
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
  styleUrls: ['./producto-info.css'],
})
export class ProductViewModalComponent implements OnChanges {
  // Estructuras utilizadas
  @Input() producto: ProductoResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();
  variantesCargadas = false;
  canalesCargados = false;
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
      icon: 'bi bi-grid-3x3-gap',
    },
    { id: 'stock', label: 'Stock', icon: 'bi bi-box-seam' },
    {
      id: 'comercial',
      label: 'Configuración Comercial',
      icon: 'bi bi-tag',
    },
  ];
  // Seccion activada por defecto
  tabActiva: TabId = 'informacion';

  variantes: VarianteResponse[] = [];
  varianteSeleccionadaId: number | null = null;
  varianteSeleccionadaNombre: string | null = null;
  canales: CanalResponse[] = [];
  stock: StockResponse | null = null;
  configuracionCanales: ConfiguracionCanal[] = [];
  totalStockPorCanal: ConfiguracionCanal[] = [];
  private totalStockRequestId = 0;

  constructor(
    private varianteService: VarianteService,
    private canalService: CanalService,
    private stockService: StockService,
    private productoCanalService: ProductoCanalService,
    private toastr: ToastrService,
    private alertService: AlertService,
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['producto'] && this.producto) {
      this.resetearStock();
      this.cargarVariantes();
      if (this.tabActiva === 'stock' || this.tabActiva === 'comercial') {
        this.cargarCanales();
      }
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
          // Asignación del primer ID y nombre
          this.varianteSeleccionadaId = response[0]?.idVariante ?? null;
          this.varianteSeleccionadaNombre = response[0]?.nombre ?? null;

          if (
            this.tabActiva === 'stock' &&
            this.varianteSeleccionadaId !== null
          ) {
            this.cargarStockPorCanales(this.varianteSeleccionadaId);
          }

          this.variantesCargadas = true;
          this.actualizarStockTotalSiEsNecesario();
        },
        error: (error) => {
          console.error('Error al cargar variantes:', error);
        },
      });
  }

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

  cargarCanales() {
    this.canalService.obtenerCanales().subscribe({
      next: (response) => {
        this.canales = response;

        if (
          this.tabActiva === 'stock' &&
          this.varianteSeleccionadaId !== null
        ) {
          this.cargarStockPorCanales(this.varianteSeleccionadaId);
        }

        this.canalesCargados = true;
        this.actualizarStockTotalSiEsNecesario();
      },
      error: (error) => {
        console.error('Error al obtener canales:', error);
      },
    });
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

  seleccionarVariante(idVariante: number, nombre: string): void {
    this.varianteSeleccionadaId = idVariante;
    this.varianteSeleccionadaNombre = nombre;
    this.cargarStockPorCanales(idVariante);
  }

  cargarStockPorCanales(idVariante: number): void {
    if (!this.producto || !this.canales.length) {
      this.configuracionCanales = [];
      return;
    }

    this.configuracionCanales = [];

    this.canales.forEach((canal) => {
      this.stockService
        .obtenerStockPorCanalYVariante(canal.idCanalVenta, idVariante)
        .subscribe({
          next: (response) => {
            this.configuracionCanales.push({
              idCanal: canal.idCanalVenta,
              nombreCanal: canal.nombre,
              cantidad: response?.cantidadDisponible ?? 0,
            });
          },
          error: (error) => {
            console.error(
              `Error al cargar stock para canal ${canal.idCanalVenta}:`,
              error,
            );
            this.configuracionCanales.push({
              idCanal: canal.idCanalVenta,
              nombreCanal: canal.nombre,
              cantidad: 0,
            });
          },
        });
    });
  }

  cargarTotalStockPorCanales(): void {
    if (!this.producto || !this.canales.length || !this.variantes.length) {
      this.totalStockPorCanal = [];
      return;
    }

    const requestId = ++this.totalStockRequestId;
    this.totalStockPorCanal = [];

    this.canales.forEach((canal) => {
      let total = 0;
      let pendientes = this.variantes.length;

      if (pendientes === 0) {
        this.totalStockPorCanal.push({
          idCanal: canal.idCanalVenta,
          nombreCanal: canal.nombre,
          cantidad: 0,
        });
        return;
      }

      this.variantes.forEach((variante) => {
        this.stockService
          .obtenerStockPorCanalYVariante(
            canal.idCanalVenta,
            variante.idVariante,
          )
          .subscribe({
            next: (response) => {
              if (requestId !== this.totalStockRequestId) {
                return;
              }
              total += response?.cantidadDisponible ?? 0;
              pendientes -= 1;
              if (pendientes === 0) {
                this.totalStockPorCanal.push({
                  idCanal: canal.idCanalVenta,
                  nombreCanal: canal.nombre,
                  cantidad: total,
                });
              }
            },
            error: (error) => {
              if (requestId !== this.totalStockRequestId) {
                return;
              }
              console.error(
                `Error al sumar stock para canal ${canal.idCanalVenta} variante ${variante.idVariante}:`,
                error,
              );
              pendientes -= 1;
              if (pendientes === 0) {
                this.totalStockPorCanal.push({
                  idCanal: canal.idCanalVenta,
                  nombreCanal: canal.nombre,
                  cantidad: total,
                });
              }
            },
          });
      });
    });
  }

  obtenerTotalStockCanal(idCanal: number): number {
    return (
      this.totalStockPorCanal.find((item) => item.idCanal === idCanal)
        ?.cantidad ?? 0
    );
  }

  obtenerCantidadByCanal(idCanal: number): number {
    return (
      this.configuracionCanales.find((item) => item.idCanal === idCanal)
        ?.cantidad ?? 0
    );
  }

  resetearStock(): void {
    this.varianteSeleccionadaId = null;
    this.varianteSeleccionadaNombre = null;
    this.variantesCargadas = false;
    this.canalesCargados = false;
    this.totalStockPorCanal = [];
    this.configuracionCanales = [];
  }

  seleccionarTab(id: TabId): void {
    this.tabActiva = id;

    if (id === 'variantes') {
      this.cargarVariantes();
    }

    if (id === 'stock') {
      this.cargarVariantes();
      this.cargarCanales();
    }

    if (id === 'comercial') {
      this.cargarCanales();
      this.cargarProductosCanales();
    }
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

  private actualizarStockTotalSiEsNecesario(): void {
    if (this.tabActiva !== 'stock') {
      return;
    }

    if (!this.canales.length || !this.variantes.length) {
      return;
    }

    this.cargarTotalStockPorCanales();
  }

  esTabActiva(id: TabId): boolean {
    return this.tabActiva === id;
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

  // Limpia todas las variables/estructuras utilizadas en este componente
  limpiarDatos(): void {
    this.variantes = [];
    this.varianteSeleccionadaId = null;
    this.varianteSeleccionadaNombre = null;
    this.canales = [];
    this.stock = null;
    this.configuracionCanales = [];
    this.totalStockPorCanal = [];
    this.tabActiva = 'informacion';
  }

  formatearMoneda(valor: number): string {
    return valor.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS',
    });
  }
}
