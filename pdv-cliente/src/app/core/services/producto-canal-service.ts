import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { ProductoCanalResponse } from '../../models/ProductoCanal';
import { ProductoCanalRequest } from '../../models/ProductoCanal';

@Injectable({
  providedIn: 'root',
})
export class ProductoCanalService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/productos-canales/';
  }

  crearConfiguracionProductoCanal(
    request: ProductoCanalRequest,
  ): Observable<ProductoCanalResponse> {
    return this.http.post<ProductoCanalResponse>(this.hostBase, request);
  }

  editarLimiteMayoristaConfiguracion(
    idConfiguracion: number,
    nuevoLimite: number,
  ): Observable<ProductoCanalResponse> {
    const params = { nuevoLimite: nuevoLimite };

    return this.http.patch<ProductoCanalResponse>(
      `${this.hostBase}${idConfiguracion}`,
      {},
      { params },
    );
  }

  eliminarConfiguracionProductoCanal(
    idConfiguracion: number,
  ): Observable<void> {
    return this.http.delete<void>(`${this.hostBase}${idConfiguracion}`);
  }
}
