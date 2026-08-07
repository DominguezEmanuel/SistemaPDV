import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProductoResponse } from '../../../models/ProductoResponse';
import { CanalResponse } from '../../../models/CanalResponse';
import { VarianteResponse } from '../../../models/VarianteResponse';
import { StockResponse } from '../../../models/StockResponse';

import { VarianteService } from '../../../core/services/variante-service';
import { CanalService } from '../../../core/services/canal-service';
import { StockService } from '../../../core/services/stock-service';
import { ProductoCanalService } from '../../../core/services/producto-canal-service';

interface ConfiguracionCanal {
  idCanal: number;
  nombreCanal: string;
  cantidad: number;
}

type TabId = 'informacion' | 'variantes' | 'stock' | 'comercial';

@Component({
  selector: 'app-product-view-modal',
  imports: [CommonModule],
  templateUrl: './product-view-modal.component.html',
  styleUrls: ['./product-view-modal.component.css'],
})
export class ProductViewModalComponent {
  @Input() producto: ProductoResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();

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

  tabActiva: TabId = 'informacion';

  variantes: VarianteResponse[] = [];
  varianteSeleccionadaId: number | null = null;
  varianteSeleccionadaNombre: string | null = null;
  canales: CanalResponse[] = [];
  stock: StockResponse | null = null;
  configuracionCanales: ConfiguracionCanal[] = [];
  totalStockPorCanal: ConfiguracionCanal[] = [];

  constructor(
    private varianteService: VarianteService,
    private canalService: CanalService,
    private stockService: StockService,
    private productoCanalService: ProductoCanalService,
  ) {}

  ngOnChanges() {
    if (this.producto) {
      this.cargarVariantes();
      this.resetearStock();
    }
  }

  cargarVariantes(): void {
    if (!this.producto) return;

    this.varianteService
      .getVariantesByProductoId(this.producto.idProducto)
      .subscribe({
        next: (response) => {
          this.variantes = response;
          this.varianteSeleccionadaId = response[0]?.idVariante ?? null;
          this.varianteSeleccionadaNombre = response[0]?.nombre ?? null;

          if (
            this.tabActiva === 'stock' &&
            this.varianteSeleccionadaId !== null
          ) {
            this.cargarStockPorCanales(this.varianteSeleccionadaId);
          }

          // Si estamos en la pestaña Stock y ya tenemos canales cargados,
          // calcular el total por canal sumando todas las variantes.
          if (this.tabActiva === 'stock' && this.canales.length) {
            this.cargarTotalStockPorCanales();
          }
        },
        error: (error) => {
          console.error('Error al cargar variantes:', error);
        },
      });
  }

  cargarCanales() {
    this.canalService.getAllCanales().subscribe({
      next: (response) => {
        this.canales = response;

        if (
          this.tabActiva === 'stock' &&
          this.varianteSeleccionadaId !== null
        ) {
          this.cargarStockPorCanales(this.varianteSeleccionadaId);
        }

        // Si estamos en la pestaña Stock y ya tenemos variantes cargadas,
        // calcular el total por canal sumando todas las variantes.
        if (this.tabActiva === 'stock' && this.variantes.length) {
          this.cargarTotalStockPorCanales();
        }
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
        .getStockByCanalAndVariante(canal.idCanalVenta, idVariante)
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
          .getStockByCanalAndVariante(canal.idCanalVenta, variante.idVariante)
          .subscribe({
            next: (response) => {
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

  esTabActiva(id: TabId): boolean {
    return this.tabActiva === id;
  }

  cerrarModal(): void {
    this.limpiarDatos();
    this.cerrar.emit();
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }

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
