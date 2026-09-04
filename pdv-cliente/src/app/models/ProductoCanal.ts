export interface ProductoCanalResponse {
  idProductoCanal: number;

  idProducto: number;
  nombreProducto: string;

  idCanalVenta: number;
  nombreCanalVenta: string;

  limiteMayorista: number;
}

export interface ProductoCanalRequest {
  idProducto: number;

  idCanalVenta: number;

  limiteMayorista: number;
}
