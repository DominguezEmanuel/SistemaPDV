import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environment';
import { HttpClient } from '@angular/common/http';
import { ProductoResponse } from '../../models/ProductoResponse';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/productos/';
  }

  getAllProductos() {
    return this.http.get<ProductoResponse[]>(this.hostBase);
  }
}
