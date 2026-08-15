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
    this.hostBase = environment.apiUrl + '/productos/';
  }

  obtenerVariantesPorProducto(idProducto: number) {
    return this.http.get<VarianteResponse[]>(
      `${this.hostBase}${idProducto}/variantes`,
    );
  }

  crearVariante(request: VarianteRequest | null): Observable<VarianteResponse> {
    return this.http.post<VarianteResponse>(
      `${environment.apiUrl}/variantes/`,
      request,
    );
  }
}
