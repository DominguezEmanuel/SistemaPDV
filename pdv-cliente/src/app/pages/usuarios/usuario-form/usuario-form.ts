import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, FormControl } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Validadores } from '../../../validators/validadores';
import { UsuarioResponse } from '../../../models/UsuarioResponse';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './usuario-form.html',
  styleUrls: ['./usuario-form.css'],
})
export class UsuarioForm implements OnChanges {
  @Input() usuario: UsuarioResponse | null = null;
  @Input() visible = false;
  @Output() cerrar = new EventEmitter<void>();

  formUsuario!: FormGroup;
  mostrarPassword: boolean = false;
  mostrarConfirmPassword: boolean = false;

  constructor(
    private toastr: ToastrService,
    private fb: FormBuilder,
  ) {
    // Inicializar formulario
    this.formUsuario = this.fb.group(this.getControlesFormulario(), {
      validators: Validadores.passwordsIguales,
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      if (!this.usuario) {
        this.formUsuario.reset();

        this.formUsuario.markAsPristine();
        this.formUsuario.markAsUntouched();

        this.mostrarPassword = false;
        this.mostrarConfirmPassword = false;
      }
    }
  }

  private getControlesFormulario() {
    return {
      nombres: new FormControl<string>('', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.minLength(3),
          Validators.pattern('^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$'),
          Validadores.validarPrimerLetra,
        ],
      }),
      apellido: new FormControl<string>('', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.minLength(3),
          Validators.pattern('^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$'),
          Validadores.validarPrimerLetra,
        ],
      }),
      username: new FormControl<string>('', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.minLength(4),
          Validators.pattern('^[a-zA-Z0-9_]+$'),
        ],
      }),
      password: new FormControl<string>('', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.minLength(8),
          Validadores.validarPassword,
        ],
      }),
      confirmPassword: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
    };
  }

  procesarFormulario() {
    if (this.formUsuario.invalid) {
      this.formUsuario.markAllAsTouched();
      return;
    }
    this.asignarValores();
  }

  asignarValores() {
    console.log('Formulario válido');
    console.log(this.formUsuario.value);
  }

  cambioPassword() {
    this.mostrarPassword = !this.mostrarPassword;
  }

  detenerPropagacion(evento: MouseEvent): void {
    evento.stopPropagation();
  }

  cerrarModal(): void {
    // Reinicia el form
    this.formUsuario.reset();

    // Hacen que el form vuelva a su estado inicial
    this.formUsuario.markAsPristine();
    this.formUsuario.markAsUntouched();

    this.mostrarPassword = false;
    this.mostrarConfirmPassword = false;
    this.cerrar.emit();
  }
}
