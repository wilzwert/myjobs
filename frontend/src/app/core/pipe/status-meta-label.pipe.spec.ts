import { StatusMetaLabelPipe } from './status-meta-label.pipe';
import { TranslatorService } from '@core/services/translator.service';

describe('StatusMetaLabelPipe', () => {
  it('should call translatorService.translateJobStatusFilter and return its result', () => {
    const mockTranslatorService: TranslatorService = {
      translateJobStatusMeta: jest.fn().mockImplementation((status: string) => `translated-${status}`)
    } as any;

    const pipe = new StatusMetaLabelPipe(mockTranslatorService);

    const result = pipe.transform('IN_PROGRESS');
    expect(mockTranslatorService.translateJobStatusMeta).toHaveBeenCalledWith('IN_PROGRESS');
    expect(result).toBe('translated-IN_PROGRESS');
  });
});
