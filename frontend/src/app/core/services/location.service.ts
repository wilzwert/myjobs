import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class LocationService {
    assign(url: string): void {
        window.location.assign(url);
    }
}
