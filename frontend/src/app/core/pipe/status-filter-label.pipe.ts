import { Pipe, PipeTransform } from '@angular/core';
import { TranslatorService } from '@core/services/translator.service';

@Pipe({
  name: 'statusFilterLabel'
})
export class StatusFilterLabelPipe implements PipeTransform {

  constructor(private translatorService: TranslatorService){}

  transform(type: string): string {
    return this.translatorService.translateJobStatusFilter(type);
  }
}