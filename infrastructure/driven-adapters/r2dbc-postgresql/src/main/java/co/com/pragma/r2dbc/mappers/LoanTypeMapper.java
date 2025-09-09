package co.com.pragma.r2dbc.mappers;

import co.com.pragma.model.application.LoanType;
import co.com.pragma.r2dbc.entities.LoanTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanTypeMapper {
    LoanType toModel(LoanTypeEntity entity);
    LoanTypeEntity toEntity(LoanType model);
}