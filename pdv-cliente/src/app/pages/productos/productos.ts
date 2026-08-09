import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CategoriaService } from '../../core/services/categoria-service';
import { ProductoService } from '../../core/services/producto-service';
import { CategoriaResponse } from '../../models/Categoria';
import { ProductoResponse } from '../../models/Producto';
import { ProductViewModalComponent } from './product-view-modal/product-view-modal.component';
import { ProductoForm } from './producto-form/producto-form';

@Component({
  selector: 'app-productos',
  imports: [CommonModule, FormsModule, ProductViewModalComponent, ProductoForm],
  templateUrl: './productos.html',
  styleUrl: './productos.css',
})
export class Productos implements OnInit {
  categorias: CategoriaResponse[] = [];
  productos: ProductoResponse[] = [];
  idCategoria: number | null = null;
  estado: string | null = null;

  private readonly paletasCategorias = [
    { bg: '#fbe9f0', color: '#c7638f' },
    { bg: '#e7f5ff', color: '#2d77b5' },
    { bg: '#f3ecff', color: '#7b61ff' },
    { bg: '#e8f7ec', color: '#2e9e5b' },
    { bg: '#fff4de', color: '#b06a00' },
    { bg: '#ffeef3', color: '#c94b7f' },
    { bg: '#eaf6f2', color: '#2f6f5b' },
    { bg: '#fef2f2', color: '#c13b3b' },
  ];

  modalVisible = false;
  modalFormularioProducto = false;
  productoSeleccionado: ProductoResponse | null = null;

  get categoriaSeleccionada(): string {
    return this.idCategoria === null
      ? 'Todas las categorías'
      : `Categoría ${this.idCategoria}`;
  }

  constructor(
    private categoriaService: CategoriaService,
    private productoService: ProductoService,
  ) {}

  ngOnInit() {
    this.cargarCategorias();
    this.cargarProductos();
  }

  verFormulario(producto?: ProductoResponse): void {
    if (producto) {
      this.productoSeleccionado = producto;
    } else {
      this.productoSeleccionado = null;
    }
    this.modalFormularioProducto = true;
  }

  verProducto(producto: ProductoResponse): void {
    this.productoSeleccionado = producto;
    this.modalVisible = true;
  }

  cerrarModal(): void {
    if (this.modalVisible) {
      this.modalVisible = false;
    } else {
      this.modalFormularioProducto = false;
    }
    this.productoSeleccionado = null;
  }

  cargarCategorias(): void {
    this.categoriaService.getAllCategorias().subscribe({
      next: (response) => {
        this.categorias = response;
      },
      error: (error) => {
        console.error('Error al obtener categorías:', error);
      },
    });
  }

  cargarProductos(): void {
    this.productoService.getAllProductos().subscribe({
      next: (response) => {
        this.productos = response;
      },
      error: (error) => {
        console.error('Error al obtener productos:', error);
      },
    });
  }

  getCategoriaStyle(
    categoria?: CategoriaResponse | null,
  ): Record<string, string> {
    const seed = categoria?.idCategoria ?? categoria?.nombre?.length ?? 0;
    const palette =
      this.paletasCategorias[seed % this.paletasCategorias.length];

    return {
      '--cat-bg': palette.bg,
      '--cat-color': palette.color,
    };
  }

  formatearMoneda(valor: number): string {
    return valor.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS',
    });
  }
}
