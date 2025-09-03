package co.com.pragma.config;

import co.com.pragma.r2dbc.Slf4jLogAdapter;
import co.com.pragma.usecase.application.ApplicationUseCase;
import co.com.pragma.usecase.application.ApplicationValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.pragma.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public ApplicationValidator applicationValidator(){ return new ApplicationValidator();}

    @Bean
    public Slf4jLogAdapter slf4jLogAdapter() {
        return new Slf4jLogAdapter(ApplicationUseCase.class);
    }

}
