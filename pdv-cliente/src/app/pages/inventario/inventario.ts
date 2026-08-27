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
// Others
import { StockInfo } from './stock-info/stock-info';
import { StockForm } from './stock-form/stock-form';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-inventario',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    StockInfo,
    StockForm,
  ],
  templateUrl: './inventario.html',
  styleUrl: './inventario.css',
})
export class Inventario implements OnInit {
  // Estructuras utilizadas
  stocks: StockResponse[] = [];
  canales: CanalResponse[] = [];
  idCanal: number | null = null;
  estado: string | null = null;
  busquedaControl = new FormControl('');

  // Variables que controlan la vista de modal de info y formulario
  modalStockVisible = false;
  registroStockSeleccionado: StockResponse | null = null;
  formStockVisible = false;
  registroStockForm: StockResponse | null = null;

  // Variables para las tarjetas de resumen
  unidadesTotales!: number;
  registrosConStockBajo!: number;
  registrosSinStock!: number;

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
    this.stockService.obtenerStocks().subscribe({
      next: (response) => {
        this.stocks = response.content;
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

      if (this.tieneStockBajo(stock.cantidadDisponible, stock.stockMinimo)) {
        this.registrosConStockBajo++;
      }

      if (this.noTieneStock(stock.cantidadDisponible)) {
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
    /*console.log('\nCanal: ', this.idCanal);
    console.log('Estado: ', this.estado);
    console.log('Busqueda: ', this.busquedaControl.value);*/

    const nombre = this.busquedaControl.value?.trim() ?? '';

    this.stockService
      .filtrarStock(0, 0, nombre, this.idCanal, this.estado)
      .subscribe({
        next: (response) => {
          this.stocks = response.content;
          this.cargarTarjetasResumen();
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  limpiarFiltros(): void {
    this.busquedaControl.setValue('');

    this.idCanal = null;

    this.estado = null;

    this.aplicarFiltros();
  }

  verRegistroStock(registro?: StockResponse): void {
    if (registro) {
      this.registroStockSeleccionado = registro;
    } else {
      this.registroStockSeleccionado = null;
    }
    this.modalStockVisible = true;
  }

  verFormularioStock(stock?: StockResponse): void {
    if (stock) {
      this.registroStockForm = stock;
    } else {
      this.registroStockForm = null;
    }
    this.formStockVisible = true;
  }

  cerrarModalStock(): void {
    this.modalStockVisible = false;
    this.registroStockSeleccionado = null;
  }

  cerrarFormularioStock(): void {
    this.formStockVisible = false;
    this.registroStockForm = null;
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

  private tieneStockBajo(cantidad: number, stock: number): boolean {
    return cantidad > 0 && cantidad <= stock;
  }

  private noTieneStock(cantidad: number): boolean {
    return cantidad === 0;
  }

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

  formatearIdRegistro(id: number): string {
    return `ID-${id.toString().padStart(4, '0')}`;
  }
}
