import { Pipe, PipeTransform } from '@angular/core';
import { JobStatus } from '../model/job.interface';

@Pipe({
  name: 'statusLabel'
})
export class StatusLabelPipe implements PipeTransform {
  transform(type: string): string {
    return JobStatus[type as keyof typeof JobStatus] || type;
  }
}