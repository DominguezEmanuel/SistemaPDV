export interface VarianteResponse {
  idVariante: number;
  nombre: string;
  codigoBarras: string;
  codigoInterno: string;
  activo: boolean;
  idProducto: number;
  nombreProducto: string;
  precioMinorista: number;
  precioMayorista: number;
}

export interface VarianteRequest {
  nombre: string;
  codigoBarras: string;
  idProducto: number;
}
