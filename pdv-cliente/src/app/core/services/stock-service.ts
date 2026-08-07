import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { StockResponse } from '../../models/StockResponse';

@Injectable({
  providedIn: 'root',
})
export class StockService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/stocks/';
  }

  getStockByCanalAndVariante(idCanal: number, idVariante: number) {
    return this.http.get<StockResponse>(
      `${this.hostBase}canal/${idCanal}/variante/${idVariante}`,
    );
  }
}
