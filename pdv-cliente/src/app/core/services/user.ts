import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root',
})
export class User {
  private hostBase!: string;

  constructor(private http: HttpClient) {
    this.hostBase = environment.apiUrl + '/usuarios/';
  }

  getAllUsers(): Observable<any> {
    return this.http.get<any>(`${this.hostBase}`);
  }

  changeUserStatus(username: string, activo: boolean): Observable<any> {
    const params = { activo: activo };

    return this.http.patch<any>(
      `${this.hostBase}estado/${username}`,
      {},
      { params },
    );
  }
}
