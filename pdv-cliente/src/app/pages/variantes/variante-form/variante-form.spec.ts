import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VarianteForm } from './variante-form';

describe('VarianteForm', () => {
  let component: VarianteForm;
  let fixture: ComponentFixture<VarianteForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VarianteForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VarianteForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
