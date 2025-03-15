package com.wilzwert.myjobs.domain.ports.driving;


import com.wilzwert.myjobs.domain.model.ScrappedPage;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:27
 */
public interface ScrapPageUseCase {

    ScrappedPage scrap(String url);
}
