import {Injectable} from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import {Subject} from 'rxjs';

import '@angular/localize/init';
import { MatStepperIntl } from '@angular/material/stepper';

@Injectable()
export class JobStepperIntl extends MatStepperIntl {
  override optionalLabel: string = $localize `:@@stepper.optional.label:Optional`;
}
