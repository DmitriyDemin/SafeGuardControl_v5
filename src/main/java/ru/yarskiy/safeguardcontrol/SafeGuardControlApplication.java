package ru.yarskiy.safeguardcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yarskiy.safeguardcontrol.config.StringToPeriodConverter;

@SpringBootApplication
@EnableScheduling // ✅ Включаем выполнение задач по расписанию — необходимо для автоматического контроля сроков проверки СИЗ
public class SafeGuardControlApplication {
    public static void main(String[] args) {
        SpringApplication.run(SafeGuardControlApplication.class, args);
    }

    /**
     * Внутренний конфигурационный класс для настройки Spring MVC.
     * Позволяет регистрировать кастомные конвертеры типов.
     */
    @Configuration
    public static class WebConfig implements WebMvcConfigurer {

        /**
         * Регистрируем конвертер строк в Period (например, "P6M" → Period.ofMonths(6)).
         * Это может быть полезно при обработке периодичности проверок в будущем.
         *
         * @param registry Реестр форматтеров и конвертеров
         */
        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new StringToPeriodConverter());
        }
    }
}