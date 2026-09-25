import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
// Services
import { VentaService } from '../../core/services/venta-service';
import { CanalService } from '../../core/services/canal-service';
import { ToastrService } from 'ngx-toastr';
import { Auth } from '../../core/services/auth';
// Models
import { VarianteVentaResponse } from '../../models/Venta';
import { CanalResponse } from '../../models/Canal';
import { UsuarioResponse } from '../../models/Usuario';
//Others
import {
  debounceTime,
  distinctUntilChanged,
  interval,
  Subscription,
} from 'rxjs';

interface ItemCarrito extends VarianteVentaResponse {
  cantidad: number;
}

@Component({
  selector: 'app-ventas',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './ventas.html',
  styleUrl: './ventas.css',
})
export class Ventas implements OnInit, OnDestroy {
  // Estructuras utilizadas del componente
  canales: CanalResponse[] = [];
  idCanalVenta: number | null = null;
  busquedaControl = new FormControl('');
  contador: number = 0;
  subtotalVenta: number = 0;
  totalVenta: number = 0;
  carrito: ItemCarrito[] = [];

  // Fecha y hora
  fechaHora!: string;

  // Info usuario
  nombreUsuario!: string;

  // Variable de subscripción
  private intervalSubscription?: Subscription;

  ngOnInit(): void {
    this.cargarCanalesVenta();
    this.obtenerUsuarioLogueado();
    /*this.busquedaControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => {
        this.buscarPorCodigoBarras();
      });*/
    this.actualizarFechaHora();
    this.intervalSubscription = interval(30000).subscribe(() => {
      this.actualizarFechaHora();
    });
  }

  ngOnDestroy(): void {
    if (this.intervalSubscription) {
      this.intervalSubscription.unsubscribe();
    }
  }

  constructor(
    private ventaService: VentaService,
    private canalService: CanalService,
    private toastr: ToastrService,
    private authService: Auth,
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

  obtenerUsuarioLogueado(): void {
    const nombre = this.authService.getUserLogued()?.nombre ?? '';
    const apellido = this.authService.getUserLogued()?.apellido ?? '';
    this.nombreUsuario = `${nombre} ${apellido}`;
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
    this.calcularSubtotalVenta();
  }

  calcularSubtotalItem(item: ItemCarrito): number {
    return item.precioMinorista * item.cantidad;
  }

  aumentarCantidad(item: ItemCarrito): void {
    if (item.cantidad < item.stockDisponible) {
      item.cantidad++;
      this.contador++;
      this.calcularSubtotalVenta();
    }
  }

  disminuirCantidad(item: ItemCarrito): void {
    if (item.cantidad > 1) {
      item.cantidad--;
      this.contador--;
      this.calcularSubtotalVenta();
    }
  }

  calcularSubtotalVenta(): void {
    this.subtotalVenta = this.carrito.reduce((acumulador, item) => {
      return acumulador + item.precioMinorista * item.cantidad;
    }, 0);
  }

  actualizarFechaHora(): void {
    const ahora = new Date();
    const fecha = ahora.toLocaleDateString('es-AR');
    const hora = ahora.toLocaleTimeString('es-AR', {
      hour: '2-digit',
      minute: '2-digit',
    });
    this.fechaHora = `${fecha} - ${hora}`;
  }

  formatearMoneda(valor: number): string {
    return valor.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS',
    });
  }
}
