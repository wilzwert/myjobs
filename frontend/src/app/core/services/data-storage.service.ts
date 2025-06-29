import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {

  getRawItem(key: string): string | null {
    return window.localStorage.getItem(key);
  }

  getItem<T>(key: string) : T | null {
    const item = window.localStorage.getItem(key);
    if(item === null) {
      return null;
    }
    try {
      return JSON.parse(item) as T;
    }
    catch(e) {
      return null;
    }
  }

  removeItem(key: string): void {
    window.localStorage.removeItem(key);
  }

  setItem(key: string, value: any): void {
    window.localStorage.setItem(key, typeof value === 'string' ? value : JSON.stringify(value));
  }
}