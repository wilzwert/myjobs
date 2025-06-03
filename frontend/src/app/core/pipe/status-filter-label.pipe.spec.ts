import { StatusFilterLabelPipe } from './status-filter-label.pipe';
import { TranslatorService } from '@core/services/translator.service';

describe('StatusLabelFilterPipe', () => {
  it('should call translatorService.translateJobStatusFilter and return its result', () => {
    const mockTranslatorService: TranslatorService = {
      translateJobStatusFilter: jest.fn().mockImplementation((status: string) => `translated-${status}`)
    } as any;

    const pipe = new StatusFilterLabelPipe(mockTranslatorService);

    const result = pipe.transform('IN_PROGRESS');
    expect(mockTranslatorService.translateJobStatusFilter).toHaveBeenCalledWith('IN_PROGRESS');
    expect(result).toBe('translated-IN_PROGRESS');
  });
});
