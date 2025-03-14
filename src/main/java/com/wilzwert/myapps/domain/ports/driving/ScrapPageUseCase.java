package com.wilzwert.myapps.domain.ports.driving;


import com.wilzwert.myapps.domain.model.ScrappedPage;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:27
 */
public interface ScrapPageUseCase {

    ScrappedPage scrap(String url);
}
