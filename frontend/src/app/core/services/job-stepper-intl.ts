import {Injectable} from '@angular/core';

import { MatStepperIntl } from '@angular/material/stepper';

@Injectable()
export class JobStepperIntl extends MatStepperIntl {
  override optionalLabel: string = $localize `:@@stepper.optional.label:Optional`;
}
