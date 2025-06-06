import { Pipe, PipeTransform } from '@angular/core';
import { TranslatorService } from '@core/services/translator.service';

@Pipe({
  name: 'statusFilterLabel'
})
export class StatusMetaLabelPipe implements PipeTransform {

  constructor(private translatorService: TranslatorService){}

  transform(type: string): string {
    return this.translatorService.translateJobStatusMeta(type);
  }
}