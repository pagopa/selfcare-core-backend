package it.pagopa.selfcare.product.dao.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OffsetDateTimeToStringConverter implements Converter<OffsetDateTime, String> {

    @Override
    public String convert(OffsetDateTime source) {
        return source.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
