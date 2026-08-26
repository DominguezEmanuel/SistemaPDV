import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { StockRequest, StockResponse } from '../../models/Stock';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StockService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/stock/';
  }

  obtenerStocks(): Observable<StockResponse[]> {
    return this.http.get<StockResponse[]>(`${this.hostBase}`);
  }

  crearRegistroStock(request: StockRequest | null): Observable<StockResponse> {
    return this.http.post<StockResponse>(`${this.hostBase}`, request);
  }

  getStockByCanalAndVariante(idCanal: number, idVariante: number) {
    return this.http.get<StockResponse>(
      `${this.hostBase}canal/${idCanal}/variante/${idVariante}`,
    );
  }
}
