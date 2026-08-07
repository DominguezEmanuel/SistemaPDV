import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { ProductoCanalResponse } from '../../models/ProductoCanalResponse';

@Injectable({
  providedIn: 'root',
})
export class ProductoCanalService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/productos-canales/';
  }

  findByCanalAndProducto(
    idCanal: number | undefined,
    idProducto: number | undefined,
  ) {
    return this.http.get<ProductoCanalResponse>(
      `${this.hostBase}canal/${idCanal}/producto/${idProducto}`,
    );
  }
}
