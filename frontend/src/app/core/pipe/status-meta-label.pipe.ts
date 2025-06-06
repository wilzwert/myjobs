import { Pipe, PipeTransform } from '@angular/core';
import { TranslatorService } from '@core/services/translator.service';

@Pipe({
  name: 'statusMetaLabel'
})
export class StatusMetaLabelPipe implements PipeTransform {

  constructor(private readonly translatorService: TranslatorService){}

  transform(type: string): string {
    return this.translatorService.translateJobStatusMeta(type);
  }
}