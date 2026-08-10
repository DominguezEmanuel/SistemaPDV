import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { HttpClient } from '@angular/common/http';
import { ProductoResponse } from '../../models/Producto';

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

  crearProducto(formData: FormData): Observable<ProductoResponse> {
    return this.http.post<ProductoResponse>(`${this.hostBase}`, formData);
  }
}
