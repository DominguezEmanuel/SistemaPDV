import { TestBed } from '@angular/core/testing';

import { VarianteService } from './variante-service';

describe('VarianteService', () => {
  let service: VarianteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VarianteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
