import { Pipe, PipeTransform } from '@angular/core';
import { JobStatus } from '../model/job.interface';
import { TranslatorService } from '../services/translator.service';

@Pipe({
  name: 'statusLabel'
})
export class StatusLabelPipe implements PipeTransform {

  constructor(private translatorService: TranslatorService){}

  transform(type: string): string {
    return this.translatorService.translateJobStatus(type);
  }
}