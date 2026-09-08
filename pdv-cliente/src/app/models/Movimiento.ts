export interface MovimientoResponse {
  idMovimiento: number;
  fechaHora: String;
  tipo: String;
  cantidad: number;
  stockAnterior: number;
  stockResultante: number;
  motivo: String;

  idUsuario: number;
  nombreUsuario: String;

  idStock: number;
}

export interface MovimientoRequest {
  tipo: string;

  cantidad: number;
  stockFisico: number;

  motivo: string;
}
