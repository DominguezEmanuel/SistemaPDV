import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { Observable } from 'rxjs';
import { VarianteVentaResponse } from '../../models/Venta';
@Injectable({
  providedIn: 'root',
})
export class VentaService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/ventas/';
  }

  obtenerVarianteParaVenta(
    codigoBarras: string,
    idCanalVenta: number,
  ): Observable<VarianteVentaResponse> {
    const params = new HttpParams()
      .set('codigoBarras', codigoBarras)
      .set('idCanalVenta', idCanalVenta);

    return this.http.get<VarianteVentaResponse>(`${this.hostBase}variantes`, {
      params,
    });
  }
}
