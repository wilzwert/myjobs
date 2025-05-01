import { Injectable } from '@angular/core';
import { ErrorTranslatorService } from './error-translator.service';
import { AbstractControl, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FormErrorService  {
    // useful to cancel subsriptions when user changes a value
    private cleanupSubscriptions = new Map<AbstractControl, Subscription>();

  constructor(private errorTranslatorService: ErrorTranslatorService) { }

  setBackendErrors(
    form: FormGroup,
    errorMap: Map<string, string[]> 
  ): void {
    for (const [fieldName, errors] of Object.entries(errorMap)) {
      const control = form.get(fieldName);
      if (control) {
        const translatedErrors = errors.map((err:string) =>
          this.errorTranslatorService.translate(err)
        );
        control.setErrors({ backend: translatedErrors });
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
