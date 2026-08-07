import { TestBed } from '@angular/core/testing';

import { ProductoCanalService } from './producto-canal-service';

describe('ProductoCanalService', () => {
  let service: ProductoCanalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductoCanalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
