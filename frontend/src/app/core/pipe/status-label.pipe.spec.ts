import { StatusLabelPipe } from './status-label.pipe';
import { TranslatorService } from '@core/services/translator.service';

describe('StatusLabelPipe', () => {
  it('should call translatorService.translateJobStatus and return its result', () => {
    const mockTranslatorService: TranslatorService = {
      translateJobStatus: jest.fn().mockImplementation((status: string) => `translated-${status}`)
    } as any;

    const pipe = new StatusLabelPipe(mockTranslatorService);

    const result = pipe.transform('IN_PROGRESS');
    expect(mockTranslatorService.translateJobStatus).toHaveBeenCalledWith('IN_PROGRESS');
    expect(result).toBe('translated-IN_PROGRESS');
  });
});
