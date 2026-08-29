import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { StockRequest, StockResponse } from '../../models/Stock';
import { Observable } from 'rxjs';
import { PageResponse } from '../../models/PageResponse';

@Injectable({
  providedIn: 'root',
})
export class StockService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/stock/';
  }

  obtenerStocks(
    page: number = 0,
    size: number = 10,
  ): Observable<PageResponse<StockResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http.get<PageResponse<StockResponse>>(`${this.hostBase}`, {
      params,
    });
  }

  crearRegistroStock(request: StockRequest | null): Observable<StockResponse> {
    return this.http.post<StockResponse>(`${this.hostBase}`, request);
  }

  obtenerStockPorCanalYVariante(idCanal: number, idVariante: number) {
    return this.http.get<StockResponse>(
      `${this.hostBase}canal/${idCanal}/variante/${idVariante}`,
    );
  }

  filtrarStock(
    page: number = 0,
    size: number = 0,
    nombre: string,
    idCanal: number | null,
    estado: string | null,
  ): Observable<PageResponse<StockResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);

    if (nombre?.trim()) {
      params = params.set('nombre', nombre.trim());
    }

    if (idCanal !== null) {
      params = params.set('idCanal', idCanal.toString());
    }

    if (estado?.trim()) {
      params = params.set('estado', estado.trim());
    }

    return this.http.get<PageResponse<StockResponse>>(
      `${this.hostBase}buscar`,
      { params },
    );
  }
}
