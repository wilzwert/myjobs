import { ActivityLabelPipe } from './activity-label.pipe';
import { TranslatorService } from '../services/translator.service';

describe('ActivityLabelPipe', () => {
  it('should call translatorService.translateActivityType and return its result', () => {
    const mockTranslatorService: TranslatorService = {
      translateActivityType: jest.fn().mockImplementation((type: string) => `translated-${type}`)
    } as any;

    const pipe = new ActivityLabelPipe(mockTranslatorService);

    const result = pipe.transform('SEARCH');
    expect(mockTranslatorService.translateActivityType).toHaveBeenCalledWith('SEARCH');
    expect(result).toBe('translated-SEARCH');
  });
});
