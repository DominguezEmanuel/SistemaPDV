import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
// Servicios
import { StockService } from '../../core/services/stock-service';
import { CanalService } from '../../core/services/canal-service';
import { ToastrService } from 'ngx-toastr';
// Modelos
import { StockResponse } from '../../models/Stock';
import { CanalResponse } from '../../models/Canal';
import { StockInfo } from './stock-info/stock-info';
import { StockForm } from './stock-form/stock-form';

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

  // Variables que controlan la vista de modal de info y formulario
  modalStockVisible = false;
  registroStockSeleccionado: StockResponse | null = null;
  formStockVisible = false;
  registroStockForm: StockResponse | null = null;

  constructor(
    private stockService: StockService,
    private canalService: CanalService,
    private toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    this.obtenerCanales();
    this.obtenerStocks();
  }

  obtenerStocks(): void {
    this.stockService.obtenerStocks().subscribe({
      next: (response) => {
        this.stocks = response;
      },
      error: (error) => {
        this.toastr.error('Error al cargar los registros', 'Error');
      },
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

  aplicarFiltros(): void {
    console.log('Canal: ', this.idCanal);
    console.log('Estado: ', this.estado);
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
