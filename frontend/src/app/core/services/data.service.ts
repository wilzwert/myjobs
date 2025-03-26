import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DataService {
  // API's base URL
  // not really necessary as we use a proxy for now
  private baseUrl = 'api/'; 

  constructor(private http: HttpClient) {}

   // GET
   get<T>(endpoint: string, options?: {params?: HttpParams, headers?: HttpHeaders}): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, { ...options, withCredentials: true});
  }

  // POST
  post<T>(endpoint: string, body: unknown, options?: {params?: HttpParams, headers?: HttpHeaders}): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, body, {...options, withCredentials: true});
  }

  // PUT
  put<T>(endpoint: string, body: unknown, options?: {params?: HttpParams, headers?: HttpHeaders}): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, body, {...options, withCredentials: true});
  }

  // PATCH
  patch<T>(endpoint: string, body: unknown, options?: {params?: HttpParams, headers?: HttpHeaders}): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${endpoint}`, body, {...options, withCredentials: true});
  }

  // DELETE
  delete<T>(endpoint: string, options?: {params?: HttpParams, headers?: HttpHeaders}): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`, {...options, withCredentials: true});
  }
}