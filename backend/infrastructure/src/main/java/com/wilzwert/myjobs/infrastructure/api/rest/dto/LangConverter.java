package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.util.StdConverter;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import org.springframework.stereotype.Component;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Component
class LangConverter extends StdConverter<String, Lang> {

    @Override
    public Lang convert(String s) throws NumberFormatException {
        return Lang.valueOf(s.toUpperCase());
    }
}
