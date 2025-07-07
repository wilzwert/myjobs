import { Directive, inject, Input, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { EditorComponent } from "@tinymce/tinymce-angular";

@Directive()
export abstract class BaseInputComponent implements OnInit {
    @Input({ required: true }) form!: FormGroup;
    @Input() initialValue: string | undefined;

    protected fb: FormBuilder;
    
    // for rich text editors
    init: EditorComponent['init'] = {
        plugins: ['link', 'autolink', 'lists'],
        toolbar: 'undo redo | bold italic | link | bullist',
        promotion: false,
        menubar: 'null',
        statusbar: false
    };
    
    constructor(protected controlName: string){
        this.fb = inject(FormBuilder);
    }

    ngOnInit(): void {
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