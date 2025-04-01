import { Directive, EventEmitter, Input, Output } from "@angular/core";
import { ComponentInputData, ComponentInputDomainData } from "../model/component-input-data.interface";


// abstract class used to handle common behaviour for components that may be used in modals
// or in regular child components
@Directive()
export abstract class BaseChildComponent {
  @Input({required: true}) data!: ComponentInputDomainData;
  @Output() succeeded = new EventEmitter<ComponentInputDomainData>();
  @Output() failed = new EventEmitter<ComponentInputDomainData>();

  success() :void {
    this.succeeded.emit(this.data);
  }

  fail() :void {
    this.failed.emit(this.data);
  }
}
