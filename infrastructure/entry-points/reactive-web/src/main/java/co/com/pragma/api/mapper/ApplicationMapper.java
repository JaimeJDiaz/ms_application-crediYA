package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CreateApplicationDto;
import co.com.pragma.api.dto.ResponseApplicationDto;
import co.com.pragma.api.dto.UpdateApplicationDto;
import co.com.pragma.model.application.Application;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    ResponseApplicationDto toResponseApplicationDto(Application application);

    Application toApplication(CreateApplicationDto createApplicationDto);

    Application toApplication(UpdateApplicationDto updateApplicationDto);

    List<ResponseApplicationDto> toResponseApplicationDto(List<Application> applications);

    }
}
