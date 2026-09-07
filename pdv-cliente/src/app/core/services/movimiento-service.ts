import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { Observable } from 'rxjs';
import { MovimientoResponse } from '../../models/Movimiento';
@Injectable({
  providedIn: 'root',
})
export class MovimientoService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/movimientos-stock/';
  }

  obtenerUltimosMovimientosStock(
    idStock: number,
  ): Observable<MovimientoResponse[]> {
    return this.http.get<MovimientoResponse[]>(
      `${this.hostBase}${idStock}/listado`,
    );
  }
}
