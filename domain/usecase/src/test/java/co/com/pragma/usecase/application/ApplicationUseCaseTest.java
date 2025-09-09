package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.LoanType;
import co.com.pragma.model.application.User;
import co.com.pragma.model.application.gateways.*;
import co.com.pragma.usecase.application.exception.ApplicationNotFoundException;
import co.com.pragma.usecase.application.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private UserService userService;
    @Mock
    private ApplicationValidator validator;
    @Mock
    private LogPort log;

    @InjectMocks
    private ApplicationUseCase applicationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationUseCase = new ApplicationUseCase(
                applicationRepository,
                loanTypeRepository,
                userService,
                validator,
                log
        );
    }

    @Test
    void saveApplication_success() {
        Application app = new Application();
        app.setType(1L);
        // No se setea userId aquí

        LoanType loanType = new LoanType();
        String userIdentification = "10";
        User user = new User();
        user.setId(BigInteger.TWO);
        doNothing().when(validator).validateFields(app); // Mock del validador void
        doNothing().when(validator).validateAmount(app, loanType); // Mock del validador void
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(userService.getUserByIdentification(userIdentification)).thenReturn(Mono.just(user));
        when(applicationRepository.saveApplication(any(Application.class))).thenReturn(Mono.just(app));

        StepVerifier.create(applicationUseCase.saveApplication(app, userIdentification))
                .expectNext(app)
                .verifyComplete();

        verify(validator).validateFields(app);
        verify(validator).validateAmount(app, loanType);
    }

    @Test
    void saveApplication_loanTypeNotFound() {
        Application app = new Application();
        app.setType(2L);
        String userIdentification = "10";
        doNothing().when(validator).validateFields(app); // Mock del validador void

        when(loanTypeRepository.findById(2L)).thenReturn(Mono.empty());

        StepVerifier.create(applicationUseCase.saveApplication(app, userIdentification))
                .expectErrorSatisfies(e -> {
                    assertTrue(e instanceof ValidationException);
                    assertTrue(((ValidationException) e).getErrors().contains("Loan Type not found"));
                })
                .verify();
    }

    @Test
    void saveApplication_userNotFound() {
        Application app = new Application();
        app.setType(1L);
        String userIdentification = "10";
        doNothing().when(validator).validateFields(app); // Mock del validador void

        LoanType loanType = new LoanType();
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(userService.getUserByIdentification(userIdentification)).thenReturn(Mono.empty());

        StepVerifier.create(applicationUseCase.saveApplication(app, userIdentification))
                .expectErrorSatisfies(e -> {
                    assertTrue(e instanceof ValidationException);
                    assertTrue(((ValidationException) e).getErrors().contains("User not found"));
                })
                .verify();
    }

    @Test
    void getApplication_success() {
        BigInteger id = BigInteger.ONE;
        Application app = new Application();
        when(applicationRepository.findById(id)).thenReturn(Mono.just(app));

        StepVerifier.create(applicationUseCase.getApplication(id))
                .expectNext(app)
                .verifyComplete();
    }

    @Test
    void getApplication_notFound() {
        BigInteger id = BigInteger.TEN;
        when(applicationRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(applicationUseCase.getApplication(id))
                .expectError(ApplicationNotFoundException.class)
                .verify();
    }


}