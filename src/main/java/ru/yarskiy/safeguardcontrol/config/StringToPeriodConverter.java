package ru.yarskiy.safeguardcontrol.config;

import org.springframework.core.convert.converter.Converter;
import java.time.Period;

/**
 * Конвертирует строку (например, "P6M") в java.time.Period.
 * Используется Spring при привязке параметров, например, в DTO.
 */
public class StringToPeriodConverter implements Converter<String, Period> {

    @Override
    public Period convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return Period.parse(source.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Невозможно преобразовать '" + source + "' в Period. Используйте формат ISO-8601, например: P6M, P1Y");
        }
    }
}