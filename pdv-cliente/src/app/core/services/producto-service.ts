import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ProductoResponse } from '../../models/Producto';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/productos/';
  }

  getAllProductos(): Observable<ProductoResponse[]> {
    return this.http.get<ProductoResponse[]>(this.hostBase);
  }

  buscarPorFiltros(
    nombre: string,
    idCategoria: number | null,
    activo: boolean | null,
  ): Observable<ProductoResponse[]> {
    let params = new HttpParams();

    if (nombre.trim()) {
      params = params.set('nombre', nombre.trim());
    }

    if (idCategoria !== null) {
      params = params.set('idCategoria', idCategoria.toString());
    }

    if (activo !== null) {
      params = params.set('activo', activo.toString());
    }

    return this.http.get<ProductoResponse[]>(`${this.hostBase}buscar`, {
      params,
    });
  }

  crearProducto(formData: FormData): Observable<ProductoResponse> {
    return this.http.post<ProductoResponse>(`${this.hostBase}`, formData);
  }

  actualizarProducto(
    idProducto: number,
    formData: FormData,
  ): Observable<ProductoResponse> {
    return this.http.put<ProductoResponse>(
      `${this.hostBase}${idProducto}`,
      formData,
    );
  }

  actualizarEstadoProducto(
    idProducto: number,
    activo: boolean,
  ): Observable<ProductoResponse> {
    const params = { activo: activo };

    return this.http.patch<ProductoResponse>(
      `${this.hostBase}estado/${idProducto}`,
      {},
      { params },
    );
  }
}
