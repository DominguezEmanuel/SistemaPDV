import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
// Services
import { StockService } from '../../core/services/stock-service';
import { CanalService } from '../../core/services/canal-service';
import { ToastrService } from 'ngx-toastr';
// Models
import { StockResponse } from '../../models/Stock';
import { CanalResponse } from '../../models/Canal';
import { MovimientoResponse } from '../../models/Movimiento';
// Others
import { StockInfo } from './stock-info/stock-info';
import { StockForm } from './stock-form/stock-form';
import { MovimientoRecord } from './movimiento-record/movimiento-record';
import { MovimientoForm } from './movimiento-form/movimiento-form';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-inventario',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    StockInfo,
    StockForm,
    MovimientoRecord,
    MovimientoForm,
  ],
  templateUrl: './inventario.html',
  styleUrl: './inventario.css',
})
export class Inventario implements OnInit {
  // Estructuras utilizadas en el componente
  stocks: StockResponse[] = [];
  canales: CanalResponse[] = [];
  idCanal: number | null = null;
  estado: string | null = null;
  busquedaControl = new FormControl('');

  // Variables que controlan la vista del modal de info y formulario de Stock
  modalStockVisible = false;
  formStockVisible = false;
  registroStockSeleccionado: StockResponse | null = null;
  //registroStockForm: StockResponse | null = null;

  // Variable/s para consultar el historial de movimientos
  modalHistorialVisible = false;
  formMovimientoVisible = false;

  // Variables para las tarjetas de resumen
  unidadesTotales!: number;
  registrosConStockBajo!: number;
  registrosSinStock!: number;

  // Variables para paginación
  paginaActual: number = 0;
  tamanioPagina: number = 10;
  totalRegistros: number = 0;
  totalPaginas: number = 0;

  constructor(
    private stockService: StockService,
    private canalService: CanalService,
    private toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    this.obtenerCanales();
    this.obtenerStocks();
    this.busquedaControl.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(() => {
        this.aplicarFiltros();
      });
  }

  obtenerStocks(): void {
    this.stockService
      .obtenerStocks(this.paginaActual, this.tamanioPagina)
      .subscribe({
        next: (response) => {
          this.stocks = response.content;
          this.totalRegistros = response.page.totalElements;
          this.totalPaginas = response.page.totalPages;
          this.paginaActual = response.page.number;
          this.cargarTarjetasResumen();
        },
        error: (error) => {
          this.toastr.error('Error al cargar los registros', 'Error');
        },
      });
  }

  private cargarTarjetasResumen(): void {
    this.unidadesTotales = 0;
    this.registrosConStockBajo = 0;
    this.registrosSinStock = 0;

    this.stocks.forEach((stock) => {
      this.unidadesTotales = this.unidadesTotales + stock.cantidadDisponible;

      if (stock.estado === 'STOCK_BAJO') {
        this.registrosConStockBajo++;
      }

      if (stock.estado === 'SIN_STOCK') {
        this.registrosSinStock++;
      }
    });
  }

  obtenerCanales(): void {
    this.canalService.obtenerCanales().subscribe({
      next: (response) => {
        this.canales = response;
      },
      error: (error) => {
        this.toastr.error('Error al cargar los canales de venta', 'Error');
      },
    });
  }

  aplicarFiltros(): void {
    // Si se envía el estado, se convierte a mayúsculas para que coincida
    // con los valores esperados en el backend
    let estadoFiltro = '';

    if (this.estado) {
      estadoFiltro = this.estado.toUpperCase();
    }

    //this.paginaActual = 0;

    const texto = this.busquedaControl.value?.trim() ?? '';

    this.stockService
      .filtrarStock(
        this.paginaActual,
        this.tamanioPagina,
        texto,
        this.idCanal,
        estadoFiltro || null,
      )
      .subscribe({
        next: (response) => {
          this.stocks = response.content;
          this.totalRegistros = response.page.totalElements;
          this.totalPaginas = response.page.totalPages;
          this.paginaActual = response.page.number;
          this.cargarTarjetasResumen();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  verModalStock(accion: string, stock?: StockResponse): void {
    if (accion === 'info' && stock) {
      this.registroStockSeleccionado = stock;
      this.modalStockVisible = true;
    } else {
      this.registroStockSeleccionado = null;
      this.formStockVisible = true;
    }
  }

  cerrarModalStock(): void {
    if (this.modalStockVisible) {
      this.modalStockVisible = false;
    } else {
      this.formStockVisible = false;
    }
    this.registroStockSeleccionado = null;
  }

  verModalMovimiento(stock: StockResponse, accion: string) {
    if (accion === 'info') {
      //this.registroStockSeleccionado = stock;
      this.modalHistorialVisible = true;
    } else {
      //this.registroStockSeleccionado = null;
      this.formMovimientoVisible = true;
    }
    this.registroStockSeleccionado = stock;
  }

  cerrarModalMovimiento(): void {
    if (this.modalHistorialVisible) {
      this.modalHistorialVisible = false;
    } else {
      this.formMovimientoVisible = false;
    }
    this.registroStockSeleccionado = null;
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i);
  }

  cambiarPagina(pagina: number): void {
    if (pagina < 0 || pagina >= this.totalPaginas) {
      return;
    }

    this.paginaActual = pagina;

    this.obtenerStocks();
  }

  onStockGuardado(event: {
    stock: StockResponse | null;
    accion: 'crear' | 'editar';
  }): void {
    // Actualizar el listado de registros
    this.obtenerStocks();

    if (event.accion === 'crear') {
      this.toastr.success(
        'El registro se creó correctamente',
        'Registro creado',
      );
    }
  }

  onMovimientoGuardado(event: {
    movimiento: MovimientoResponse | null;
    accion: 'crear' | 'editar';
  }): void {
    this.aplicarFiltros();
    if (event.accion === 'crear') {
      this.toastr.success(
        'El movimiento de stock se creó correctamente',
        'Movimiento creado',
      );
    }
  }

  limpiarFiltros(): void {
    this.busquedaControl.setValue('');

    this.idCanal = null;

    this.estado = null;

    this.aplicarFiltros();
  }

  asignarEstadoStock(estado: string): string {
    if (estado.toLowerCase() === 'sin_stock') {
      return 'Sin stock';
    }

    if (estado.toLowerCase() === 'stock_bajo') {
      return 'Stock bajo';
    }

    return 'Disponible';
  }

  obtenerClaseEstado(estado: string): string {
    if (estado.toLowerCase() === 'sin_stock') {
      return 'sin-stock';
    }

    if (estado.toLowerCase() === 'stock_bajo') {
      return 'stock-bajo';
    }

    return 'disponible';
  }

  formatearIdRegistro(id: number): string {
    return `ID-${id.toString().padStart(4, '0')}`;
  }
}
