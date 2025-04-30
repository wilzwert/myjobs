import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';
import { LocaleService } from '../services/locale.service';

@Pipe({ name: 'localizedDate', standalone: true, pure: false })
export class LocalizedDatePipe implements PipeTransform {
  constructor(private localeService: LocaleService, private datePipe: DatePipe) {}

  transform(value: Date | string | number, format = 'longDate'): string | null {
    // return formatDate(value, format, this.currentLang);

    return this.datePipe.transform(value, format, undefined, this.localeService.currentLocale);
  }
}