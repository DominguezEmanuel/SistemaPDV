import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { Auth } from '../../core/services/auth';
import { Login } from './login';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authService: jasmine.SpyObj<Auth>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('Auth', ['login']);

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [{ provide: Auth, useValue: authSpy }],
    }).compileComponents();

    authService = TestBed.inject(Auth) as jasmine.SpyObj<Auth>;

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the auth service with the submitted credentials', () => {
    authService.login.and.returnValue(
      of({
        idUsuario: 1,
        nombre: 'Admin',
        apellido: 'User',
        username: 'admin',
        activo: true,
        rol: 'admin',
      }),
    );

    component.username = 'admin';
    component.password = '1234';
    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith({
      username: 'admin',
      password: '1234',
    });
  });
});
