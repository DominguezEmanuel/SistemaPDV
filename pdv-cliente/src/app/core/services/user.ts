import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { environment } from '../../../environment/environment';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root',
})
export class User {
  hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/usuarios/';
  }

  getAllUsers(): Observable<any> {
    return this.http.get<any>(`${this.hostBase}`);
  }
}
