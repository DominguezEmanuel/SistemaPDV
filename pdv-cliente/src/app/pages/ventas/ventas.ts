import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
// Services
import { VentaService } from '../../core/services/venta-service';
import { CanalService } from '../../core/services/canal-service';
import { ToastrService } from 'ngx-toastr';
// Models
import { VarianteVentaResponse } from '../../models/Venta';
import { CanalResponse } from '../../models/Canal';
//Others
import { debounceTime, distinctUntilChanged } from 'rxjs';

interface ItemCarrito extends VarianteVentaResponse {
  cantidad: number;
}

@Component({
  selector: 'app-ventas',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './ventas.html',
  styleUrl: './ventas.css',
})
export class Ventas implements OnInit {
  // Estructuras utilizadas del componente
  canales: CanalResponse[] = [];
  idCanalVenta: number | null = null;
  busquedaControl = new FormControl('');
  contador: number = 0;
  carrito: ItemCarrito[] = [];

  ngOnInit(): void {
    this.cargarCanalesVenta();
    this.busquedaControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => {
        this.buscarPorCodigoBarras();
      });
  }

  constructor(
    private ventaService: VentaService,
    private canalService: CanalService,
    private toastr: ToastrService,
  ) {}

  cargarCanalesVenta(): void {
    this.canalService.obtenerCanales().subscribe({
      next: (response) => {
        this.canales = response;
        this.idCanalVenta = response[0]?.idCanalVenta ?? null;
      },
      error: (error) => {
        console.log('Error: ', error);
        this.toastr.error('Error al cargar los canales de venta', 'Error');
      },
    });
  }

  buscarPorCodigoBarras(): void {
    const codigoBarras = this.busquedaControl.value?.trim() ?? '';

    if (this.idCanalVenta === null || codigoBarras === '') {
      return;
    }

    console.log('Codigo de barras: ', codigoBarras);
    console.log('idCanalVenta: ', this.idCanalVenta);

    this.ventaService
      .obtenerVarianteParaVenta(codigoBarras, this.idCanalVenta)
      .subscribe({
        next: (response) => {
          console.log('Variante encontrada: ', response);
          this.sumarAlCarrito(response);
        },
        error: (error) => {
          this.toastr.error(error.error.mensaje, 'Error');
        },
      });
  }

  limpiarBusqueda(): void {
    this.busquedaControl.setValue('', { emitEvent: false });
  }

  sumarAlCarrito(varianteVenta: VarianteVentaResponse): void {
    const itemExistente = this.carrito.find(
      (item) => item.idVariante === varianteVenta.idVariante,
    );

    if (itemExistente) {
      itemExistente.cantidad++;
    } else {
      this.carrito.push({ ...varianteVenta, cantidad: 1 });
    }

    console.log('Carrito: ', this.carrito);
    this.contador++;
  }

  calcularSubtotal(item: ItemCarrito): number {
    return item.precioMinorista * item.cantidad;
  }

  aumentarCantidad(item: ItemCarrito): void {
    if (item.cantidad < item.stockDisponible) {
      item.cantidad++;
      this.contador++;
    }
  }

  disminuirCantidad(item: ItemCarrito): void {
    if (item.cantidad > 1) {
      item.cantidad--;
      this.contador--;
    }
  }

  formatearMoneda(valor: number): string {
    return valor.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS',
    });
  }
}
