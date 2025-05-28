import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  constructor(private http: HttpClient) {}

  downloadFile(url: string, withCredentials: boolean = false): Observable<Blob> {
    return this.http.get(url, { responseType: 'blob', withCredentials: withCredentials });
  }
}