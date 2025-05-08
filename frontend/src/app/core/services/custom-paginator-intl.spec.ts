import { CustomPaginatorIntl } from './custom-paginator-intl';

describe('CustomPaginatorIntl test', () => {
  let service: CustomPaginatorIntl;

  beforeEach(() => {
    service = new CustomPaginatorIntl();
  });

  it('should have localized labels defined', () => {
    expect(service.firstPageLabel).toBeDefined();
    expect(service.firstPageLabel).toBe("First page");

    expect(service.itemsPerPageLabel).toBeDefined();
    expect(service.itemsPerPageLabel).toBe("Items per page");

    expect(service.lastPageLabel).toBeDefined();
    expect(service.lastPageLabel).toBe("Last page");

    expect(service.nextPageLabel).toBeDefined();
    expect(service.nextPageLabel).toBe("Next page");

    expect(service.previousPageLabel).toBeDefined();
    expect(service.previousPageLabel).toBe("Previous page");
  });

  describe('getRangeLabel', () => {
    it('should return "Page 1 of 1" if length is 0', () => {
      expect(service.getRangeLabel(0, 10, 0)).toContain('Page 1 of 1');
    });

    it('should compute correct page and total pages', () => {
      const result = service.getRangeLabel(1, 10, 45);
      expect(result).toContain('Page 2 of 5'); // page 1 (0-based) -> Page 2
    });
  });
});
