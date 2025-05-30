package com.wilzwert.myjobs.infrastructure.utility;


import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * FIXME : this dummy mapper prevents compilation warnings on test-compile
 * as we're passing the -Amapstruct.unmappedTargetPolicy=IGNORE arg to the mapstruct processor
 * -> creating this dummy mapper forces mapstruct processor to load and thus accept the arg
 *
 */
@Mapper
public class DummyMapper {
}
