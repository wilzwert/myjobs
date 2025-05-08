import { Pipe, PipeTransform } from '@angular/core';
import { ActivityType } from '../model/activity-type';
import { TranslatorService } from '../services/translator.service';

@Pipe({
  name: 'activityLabel'
})
export class ActivityLabelPipe implements PipeTransform {
  constructor(private translatorService: TranslatorService){}

  transform(type: string): string {
    return this.translatorService.translateActivityType(type);
    // return ActivityType[type as keyof typeof ActivityType] || type;
  }
}