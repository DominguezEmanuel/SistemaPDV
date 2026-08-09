import { CategoriaResponse } from './Categoria';

export interface ProductoResponse {
  idProducto: number;
  nombre: string;
  imagen: string;
  precioMinorista: number;
  precioMayorista: number;
  minimoMayorista: number;
  activo: boolean;
  categoria: CategoriaResponse;
}

export interface ProductoRequest {
  nombre: string;
  imagen: string;
  precioMinorista: number;
  precioMayorista: number;
  minimoMayorista: number;
  idCategoria: number;
}
