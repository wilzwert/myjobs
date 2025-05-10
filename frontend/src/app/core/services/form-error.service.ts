import { Injectable } from '@angular/core';
import { TranslatorService } from './translator.service';
import { AbstractControl, FormGroup, ValidationErrors } from '@angular/forms';
import { Subscription } from 'rxjs';
import { BackendError } from '../errors/backend-error';

@Injectable({
  providedIn: 'root'
})
export class FormErrorService  {
    // useful to cancel subsriptions when user changes a value
    private cleanupSubscriptions = new Map<AbstractControl, Subscription>();

  constructor(private translatorService: TranslatorService) { }

  setBackendErrors(
    form: FormGroup,
    errorMap: Record<string, BackendError[]> 
  ): void {
    console.log(errorMap);
    console.log(Object.entries(errorMap));
    for (const [fieldName, errors] of Object.entries(errorMap)) {
      console.log(`getting control for ${fieldName}`);
      const control = form.get(fieldName);
      if (control) {
        console.log('setting control translated errors');
        const translatedErrors = errors.map((err:BackendError) =>
          this.translatorService.translateError(err.code, err.details)
        );

        const controlErrors = control.errors ?? {} as ValidationErrors;
        controlErrors['backend'] = translatedErrors;
        control.setErrors(controlErrors);
        // unsub the previous subscription if it exists
        this.cleanupSubscriptions.get(control)?.unsubscribe();

        // automatic cleanup errors are removed if user changes form control value
        const sub = control.valueChanges.subscribe(() => {
          const currentErrors = control.errors;
          if (currentErrors && currentErrors['backend']) {
            delete currentErrors['backend'];
            // replace with cleaned up errors
            if (Object.keys(currentErrors).length > 0) {
              control.setErrors(currentErrors);
            } else {
              control.setErrors(null);
            }
          }
        });

        this.cleanupSubscriptions.set(control, sub);
      }
    }
  }
  
  // removes all subscriptions, useful to avoid memory leaks on form components destroy
  cleanup(): void {
    for (const sub of this.cleanupSubscriptions.values()) {
      sub.unsubscribe();
    }
    this.cleanupSubscriptions.clear();
  }
}
