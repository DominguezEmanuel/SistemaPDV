export interface VarianteVentaResponse {
  idVariante: number;
  nombreVariante: string;

  idProducto: number;
  nombreProducto: string;

  codigoBarras: string;
  codigoInterno: string;

  precioMinorista: number;
  precioMayorista: number;

  stockDisponible: number;
}
