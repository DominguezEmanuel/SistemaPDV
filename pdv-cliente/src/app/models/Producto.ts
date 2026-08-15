import { CategoriaResponse } from './Categoria';

export interface ProductoResponse {
  idProducto: number;
  nombre: string;
  imagen: string;
  precioMinorista: number;
  precioMayorista: number;
  minimoMayorista: number;
  activo: boolean;
  tieneVariantes: boolean;
  categoria: CategoriaResponse;
  codigoBarras: string;
}

export interface ProductoRequest {
  nombre: string;
  imagen: string;
  precioMinorista: number;
  precioMayorista: number;
  minimoMayorista: number;
  tieneVariantes: boolean;
  idCategoria: number;
}
