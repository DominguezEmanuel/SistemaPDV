import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { CanalResponse } from '../../models/CanalResponse';

@Injectable({
  providedIn: 'root',
})
export class CanalService {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/canales/';
  }

  getAllCanales() {
    return this.http.get<CanalResponse[]>(`${this.hostBase}`);
  }
}
