import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovimientoRecord } from './movimiento-record';

describe('MovimientoRecord', () => {
  let component: MovimientoRecord;
  let fixture: ComponentFixture<MovimientoRecord>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovimientoRecord]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MovimientoRecord);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
