import { CategoriaResponse } from './CategoriaResponse';

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
