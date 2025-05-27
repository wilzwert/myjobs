import { AfterContentInit, AfterViewInit, Component, ComponentRef, computed, Inject, Injector, OnChanges, OnDestroy, OnInit, SimpleChanges, viewChild, ViewChild, ViewContainerRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ComponentInputData, ComponentInputDomainData } from '@core/model/component-input-data.interface';
import { BaseChildComponent } from '@core/component/base-child.component';
import { MatButton, MatIconButton } from '@angular/material/button';
import { Subject, takeUntil } from 'rxjs';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-modal',
  imports: [CommonModule, MatIconButton, MatIcon, MatDialogContent],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss'
})
export class ModalComponent implements AfterViewInit, OnDestroy {

  // component loading target
  @ViewChild('component', { read: ViewContainerRef }) container!: ViewContainerRef;

  // helps prevent elements display before the component is added
  protected isLoaded = false;

  private destroy$: Subject<boolean> = new Subject<boolean>();
  
  constructor(public dialogRef: MatDialogRef<BaseChildComponent>, @Inject(MAT_DIALOG_DATA) public inputData: ComponentInputData, private viewContainerRef: ViewContainerRef) {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  ngAfterContentInit(): void {
    this.isLoaded = true;
  }

  ngAfterViewInit(): void {
    this.isLoaded = false;
    this.loadComponent();
    this.isLoaded = true;
  }
  
  public succeeded(data: ComponentInputDomainData) :void {
    this.dialogRef.close();
    if(this.inputData.succeeded !== null) {
      this.inputData.succeeded(data);
    }
  }

  public failed(data: ComponentInputDomainData) :void {
    this.dialogRef.close();
    if(this.inputData.failed !== null) {
      this.inputData.failed(data);
    }
  }

  public close() :void {
    this.dialogRef.close();
  }

  private loadComponent() {
    try {
      this.viewContainerRef.clear(); // cleans up current view
      console.log(this.inputData.component);
      const componentRef: ComponentRef<BaseChildComponent> = this.container.createComponent(this.inputData.component!);
      
      // assign input
      if (this.inputData.data !== undefined) {
        componentRef.instance.data = this.inputData.data;
      }

      // handle what would normally be @Output
      componentRef.instance.succeeded.pipe(takeUntil(this.destroy$)).subscribe(this.succeeded.bind(this));
      componentRef.instance.failed.pipe(takeUntil(this.destroy$)).subscribe(this.failed.bind(this));
    }
    catch(e) {
      console.log(e);
    }
  }
}
