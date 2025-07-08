import { Directive, inject, Input, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { EditorComponent } from "@tinymce/tinymce-angular";

@Directive()
export abstract class BaseInputComponent implements OnInit {
    @Input({required: true}) form!: FormGroup;
    @Input() controlName!: string;
    @Input() initialValue: string | undefined;

    protected fb = inject(FormBuilder);
    public control: FormControl | null = null;

    // for rich text editors
    init: EditorComponent['init'] = {
        plugins: ['link', 'autolink', 'lists'],
        toolbar: 'undo redo | bold italic | link | bullist',
        promotion: false,
        menubar: 'null',
        statusbar: false
    };
    
    constructor(protected defaultControlName: string){
    }

    ngOnInit(): void {
        if(!this.controlName) {
            this.controlName = this.defaultControlName;
        }
        this.control = this.fb.control(this.initialValue, this.getValidators());
        this.getForm().addControl(this.controlName, this.control);
    }

    updateRichText(event: any) :boolean {
        if(this.control !== null) {
            this.control.setValue(event.editor.getContent());
        }
        return true;
    }

    protected getValidators(): any[] {
        return [];
    }

    protected getForm(): FormGroup {
        if (this.form instanceof FormGroup) {
            return this.form;
        } else {
            throw new Error('Form is not a FormGroup');
        }
    }
}