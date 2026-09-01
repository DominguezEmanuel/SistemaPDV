export interface StockResponse {
  idStock: number;

  cantidadDisponible: number;
  stockMinimo: number;
  estado: string;

  idProducto: number;
  nombreProducto: string;

  idVariante: number;
  nombreVariante: string;
  codigoBarras: string;
  codigoInterno: string;

  idCanalVenta: number;
  nombreCanalVenta: string;
}

export interface StockRequest {
  cantidadDisponible: number;
  stockMinimo: number;
  idVariante: number;
  idCanalVenta: number;
}
