package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.dto.PageResponse;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.application.gateways.CatalogCachePort;
import co.com.pragma.model.application.gateways.LoanTypeRepository;
import co.com.pragma.model.application.gateways.UserService;
import co.com.pragma.model.application.LoanType;
import co.com.pragma.model.application.User;
import co.com.pragma.usecase.application.exception.ApplicationNotFoundException;
import co.com.pragma.usecase.application.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationUseCaseTest {
    private ApplicationRepository applicationRepository;
    private CatalogCachePort catalogCachePort;
    private LoanTypeRepository loanTypeRepository;
    private UserService userService;
    private ApplicationValidator validator;
    private ApplicationUseCase applicationUseCase;

    @BeforeEach
    void setUp() {
        applicationRepository = mock(ApplicationRepository.class);
        catalogCachePort = mock(CatalogCachePort.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        userService = mock(UserService.class);
        validator = mock(ApplicationValidator.class);
        applicationUseCase = new ApplicationUseCase(
                applicationRepository,
                loanTypeRepository,
                userService,
                validator,
                catalogCachePort
        );
    }

    @Test
    void whenPageIsNull_thenThrowValidationException() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                applicationUseCase.findApplications(null, 10, null)
        );
        assertTrue(ex.getErrors().contains("Page must be greater than or equal to 0"));
    }

    @Test
    void whenPageIsNegative_thenThrowValidationException() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                applicationUseCase.findApplications(-1, 10, null)
        );
        assertTrue(ex.getErrors().contains("Page must be greater than or equal to 0"));
    }

    @Test
    void whenSizeIsNull_thenThrowValidationException() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                applicationUseCase.findApplications(0, null, null)
        );
        assertTrue(ex.getErrors().contains("Size must be greater than 0"));
    }

    @Test
    void whenSizeIsZeroOrNegative_thenThrowValidationException() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                applicationUseCase.findApplications(0, 0, null)
        );
        assertTrue(ex.getErrors().contains("Size must be greater than 0"));
    }

    @Test
    void whenStatusIsNotNullAndNotFound_thenThrowValidationException() {
        when(catalogCachePort.getStatusIdByName("REJECTED")).thenReturn(null);
        ValidationException ex = assertThrows(ValidationException.class, () ->
                applicationUseCase.findApplications(0, 10, "REJECTED")
        );
        assertTrue(ex.getErrors().contains("Status not found"));
    }

    @Test
    void whenStatusIsNull_thenFindAllWithNullStatusId() {
        PageResponse<Application> pageResponse = PageResponse.<Application>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0L)
                .totalPages(0)
                .build();
        when(applicationRepository.findAll(0, 10, null)).thenReturn(Mono.just(pageResponse));
        Mono<PageResponse<Application>> result = applicationUseCase.findApplications(0, 10, null);
        assertEquals(pageResponse, result.block());
        verify(applicationRepository).findAll(0, 10, null);
    }

    @Test
    void whenStatusIsValid_thenFindAllWithStatusId() {
        when(catalogCachePort.getStatusIdByName("APPROVED")).thenReturn(2L);
        PageResponse<Application> pageResponse = PageResponse.<Application>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0L)
                .totalPages(0)
                .build();
        when(applicationRepository.findAll(0, 10, 2L)).thenReturn(Mono.just(pageResponse));
        Mono<PageResponse<Application>> result = applicationUseCase.findApplications(0, 10, "APPROVED");
        assertEquals(pageResponse, result.block());
        verify(applicationRepository).findAll(0, 10, 2L);
    }

    @Test
    void saveApplication_success() {
        Application app = new Application();
        app.setType(1L);
        LoanType loanType = new LoanType();
        User user = new User();
        user.setId(BigInteger.valueOf(123L));
        when(catalogCachePort.getStatusIdByName("PENDING")).thenReturn(1L);
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(userService.getUserByIdentification("userId")).thenReturn(Mono.just(user));
        when(applicationRepository.saveApplication(any())).thenReturn(Mono.just(app));
        Mono<Application> result = applicationUseCase.saveApplication(app, "userId");
        assertEquals(app, result.block());
        verify(validator).validateFields(app);
        verify(validator).validateAmount(app, loanType);
        verify(applicationRepository).saveApplication(app);
    }

    @Test
    void saveApplication_loanTypeNotFound() {
        Application app = new Application();
        app.setType(1L);
        when(catalogCachePort.getStatusIdByName("PENDING")).thenReturn(1L);
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.empty());
        Mono<Application> mono = applicationUseCase.saveApplication(app, "userId");
        ValidationException ex = assertThrows(ValidationException.class, mono::block);
        assertTrue(ex.getErrors().contains("Loan Type not found"));
    }

    @Test
    void saveApplication_userNotFound() {
        Application app = new Application();
        app.setType(1L);
        LoanType loanType = new LoanType();
        when(catalogCachePort.getStatusIdByName("PENDING")).thenReturn(1L);
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(userService.getUserByIdentification("userId")).thenReturn(Mono.empty());
        Mono<Application> mono = applicationUseCase.saveApplication(app, "userId");
        ValidationException ex = assertThrows(ValidationException.class, mono::block);
        assertTrue(ex.getErrors().contains("User not found"));
    }

    @Test
    void getApplication_success() {
        Application app = new Application();
        when(applicationRepository.findById(BigInteger.ONE)).thenReturn(Mono.just(app));
        Mono<Application> result = applicationUseCase.getApplication(BigInteger.ONE);
        assertEquals(app, result.block());
    }

    @Test
    void getApplication_nullId() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                applicationUseCase.getApplication(null)
        );
        assertTrue(ex.getErrors().contains("Id is required"));
    }

    @Test
    void getApplication_notFound() {
        when(applicationRepository.findById(BigInteger.ONE)).thenReturn(Mono.empty());
        Mono<Application> mono = applicationUseCase.getApplication(BigInteger.ONE);
        ApplicationNotFoundException ex = assertThrows(ApplicationNotFoundException.class, mono::block);
        assertEquals("ERROR_FETCHING_USER_BY_ID", ex.getMessage());
    }
}
