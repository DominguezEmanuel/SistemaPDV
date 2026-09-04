import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductoCanalForm } from './producto-canal-form';

describe('ProductoCanalForm', () => {
  let component: ProductoCanalForm;
  let fixture: ComponentFixture<ProductoCanalForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoCanalForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductoCanalForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
