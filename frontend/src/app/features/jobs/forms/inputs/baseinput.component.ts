import { Directive, inject, Input, OnInit } from "@angular/core";
import { AbstractControl, FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { EditorComponent } from "@tinymce/tinymce-angular";

@Directive()
export abstract class BaseInputComponent implements OnInit {
    @Input({ required: true }) form!: FormGroup;
    @Input() initialValue: string | undefined;

    protected fb = inject(FormBuilder);

    // for riche text editors
    init: EditorComponent['init'] = {
        plugins: ['link', 'autolink', 'lists'],
        toolbar: 'undo redo | bold italic | link | bullist',
        promotion: false,
        menubar: 'null',
        statusbar: false
    };
    
    constructor(protected controlName: string){
    }

    ngOnInit(): void {
        console.log('BaseInputComponent ngOnInit ?');
        console.log('form', this.form);
        console.log('initial value', this.initialValue);
        this.configure();
    }

    updateRichText(event: any) :boolean {
        if(this.control !== null) {
            this.control.setValue(event.editor.getContent());
        }
        return true;
    }

    public get control() {
        return this.form?.get(this.controlName) as FormControl;
    }

    abstract configure(): void;
}