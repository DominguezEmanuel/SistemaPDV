import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { VarianteRequest, VarianteResponse } from '../../models/Variante';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root',
})
export class VarianteService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/variantes/';
  }

  obtenerVariantesPorProducto(idProducto: number) {
    return this.http.get<VarianteResponse[]>(
      `${environment.apiUrl}/productos/${idProducto}/variantes`,
    );
  }

  crearVariante(request: VarianteRequest | null): Observable<VarianteResponse> {
    return this.http.post<VarianteResponse>(`${this.hostBase}`, request);
  }

  actualizarVariante(
    idVariante: number,
    request: VarianteRequest | null,
  ): Observable<VarianteResponse> {
    return this.http.put<VarianteResponse>(
      `${this.hostBase}${idVariante}`,
      request,
    );
  }

  cambiarEstadoVariante(
    idVariante: number,
    activo: boolean,
  ): Observable<VarianteResponse> {
    const params = { activo: activo };

    return this.http.patch<VarianteResponse>(
      `${this.hostBase}${idVariante}`,
      {},
      { params },
    );
  }
}
