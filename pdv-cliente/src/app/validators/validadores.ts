import { ValidationErrors, AbstractControl } from '@angular/forms';

export class Validadores {
  static validarPrimerLetra(c: AbstractControl): ValidationErrors | null {
    const nombre = c.value as string;

    if (!nombre) {
      return null;
    }

    if (nombre.charAt(0) != nombre.charAt(0).toUpperCase()) {
      return { sinPrimerLetraMayuscula: true };
    }

    return null;
  }

  static validarPassword(c: AbstractControl): ValidationErrors | null {
    let password: string = String(c.value);

    if (!password) {
      return null;
    }

    // Verificar los errores del password
    const errores: ValidationErrors = {
      ...(!/[A-Z]/.test(password) && { sinMayuscula: true }),
      ...(!/[a-z]/.test(password) && { sinMinuscula: true }),
      ...(!/[0-9]/.test(password) && { sinNumero: true }),
    };

    //Si hay errores los devuelve, sino devuelve null
    return Object.keys(errores).length ? errores : null;
  }

  // Ejemplo de validador
  static passwordsIguales(form: AbstractControl): ValidationErrors | null {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordsMismatch: true };
  }

  // Validar un array no vacio
  static minLengthArray(min: number) {
    return (formArray: any) => {
      return formArray && formArray.length >= min
        ? null
        : { minLengthArray: true };
    };
  }
}
