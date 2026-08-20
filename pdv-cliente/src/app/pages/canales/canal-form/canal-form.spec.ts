import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CanalForm } from './canal-form';

describe('CanalForm', () => {
  let component: CanalForm;
  let fixture: ComponentFixture<CanalForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CanalForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CanalForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
