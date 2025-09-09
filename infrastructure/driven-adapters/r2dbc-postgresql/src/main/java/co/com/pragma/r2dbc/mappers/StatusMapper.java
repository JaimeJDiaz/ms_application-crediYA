package co.com.pragma.r2dbc.mappers;

import co.com.pragma.model.application.Status;
import co.com.pragma.r2dbc.entities.StatusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    Status toModel(StatusEntity entity);
    StatusEntity toEntity(Status model);
}