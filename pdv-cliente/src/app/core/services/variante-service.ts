import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { VarianteResponse } from '../../models/VarianteResponse';
@Injectable({
  providedIn: 'root',
})
export class VarianteService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/productos/';
  }

  getVariantesByProductoId(idProducto: number) {
    return this.http.get<VarianteResponse[]>(
      `${this.hostBase}${idProducto}/variantes`,
    );
  }
}
