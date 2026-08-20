import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { CanalRequest, CanalResponse } from '../../models/Canal';
import { Observable } from 'rxjs';
import { VarianteResponse } from '../../models/Variante';

@Injectable({
  providedIn: 'root',
})
export class CanalService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/canales-venta/';
  }

  obtenerCanales() {
    return this.http.get<CanalResponse[]>(`${this.hostBase}`);
  }

  crearCanal(request: CanalRequest | null): Observable<CanalResponse> {
    return this.http.post<CanalResponse>(`${this.hostBase}`, request);
  }

  actualizarCanal(
    idCanal: number,
    request: CanalRequest | null,
  ): Observable<CanalResponse> {
    return this.http.put<CanalResponse>(`${this.hostBase}${idCanal}`, request);
  }
}
