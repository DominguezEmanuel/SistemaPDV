import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ProductoResponse } from '../../models/Producto';
import { PageResponse } from '../../models/PageResponse';
import { StockProductoResponse } from '../../models/Producto';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/productos/';
  }

  // page: indica cuál página de resultados se desea obtener - page 0: primer página
  // size: cantidad máxima de registros que se desea obtener por cada página
  obtenerProductos(
    page: number = 0,
    size: number = 10,
  ): Observable<PageResponse<ProductoResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http.get<PageResponse<ProductoResponse>>(this.hostBase, {
      params,
    });
  }

  buscarPorFiltros(
    page: number = 0,
    size: number = 0,
    nombre: string,
    idCategoria: number | null,
    activo: boolean | null,
  ): Observable<PageResponse<ProductoResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);

    if (nombre?.trim()) {
      params = params.set('nombre', nombre.trim());
    }

    if (idCategoria !== null) {
      params = params.set('idCategoria', idCategoria.toString());
    }

    if (activo !== null) {
      params = params.set('activo', activo.toString());
    }

    return this.http.get<PageResponse<ProductoResponse>>(
      `${this.hostBase}filtrar`,
      {
        params,
      },
    );
  }

  buscarPorNombre(nombre: string): Observable<ProductoResponse[]> {
    const params = new HttpParams().set('nombre', nombre.trim());

    return this.http.get<ProductoResponse[]>(`${this.hostBase}filtrar/nombre`, {
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

  obtenerStockProducto(
    idProducto: number,
  ): Observable<StockProductoResponse[]> {
    return this.http.get<StockProductoResponse[]>(
      `${this.hostBase}${idProducto}/stock`,
    );
  }
}
