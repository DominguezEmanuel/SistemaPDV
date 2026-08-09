import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { CategoriaResponse } from '../../models/Categoria';

@Injectable({
  providedIn: 'root',
})
export class CategoriaService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/categorias/';
  }

  getAllCategorias() {
    return this.http.get<CategoriaResponse[]>(this.hostBase);
  }
}
