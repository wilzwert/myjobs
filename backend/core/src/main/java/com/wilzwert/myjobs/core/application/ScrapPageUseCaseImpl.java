package com.wilzwert.myjobs.core.application;


import com.wilzwert.myjobs.core.domain.model.ScrappedPage;
import com.wilzwert.myjobs.core.domain.ports.driving.ScrapPageUseCase;

import java.time.LocalDateTime;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:27
 */
public class ScrapPageUseCaseImpl implements ScrapPageUseCase {
    @Override
    public ScrappedPage scrap(String url) {
        return new ScrappedPage("", "", "", LocalDateTime.MIN, LocalDateTime.MAX, "", "", "");
    }
}
