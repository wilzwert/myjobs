import { Pipe, PipeTransform } from '@angular/core';
import { ActivityType } from '../model/activity-type';

@Pipe({
  name: 'activityLabel'
})
export class ActivityLabelPipe implements PipeTransform {
  transform(type: string): string {
    return ActivityType[type as keyof typeof ActivityType] || type;
  }
}