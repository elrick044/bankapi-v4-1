package br.edu.utfpr.bankapi.validations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import br.edu.utfpr.bankapi.exception.WithoutBalanceException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;

class AvailableBalanceValidationTest {

    @Mock
    private Account sourceAccount;

    @InjectMocks
    private AvailableBalanceValidation availableBalanceValidation;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Inicializa a conta de origem
        sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setName("John Doe");
        sourceAccount.setNumber(12345L);
        sourceAccount.setBalance(1000.0);
        sourceAccount.setSpecialLimit(500.0);

        // Inicializa a transação
        transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setAmount(500.0);
    }

    @Test
    void deveriaValidarSaldoSuficiente() {
        // ### ACT ###
        assertDoesNotThrow(() -> availableBalanceValidation.validate(transaction));
    }

    @Test
    void deveriaLancarExcecaoQuandoSaldoInsuficiente() {
        // Alterar o saldo para que a transação exceda o limite
        transaction.setAmount(1600.0); // Valor maior que o saldo com limite

        // ### ACT & ASSERT ###
        WithoutBalanceException exception = assertThrows(WithoutBalanceException.class, 
            () -> availableBalanceValidation.validate(transaction));

        assertNotNull(exception);
    }
}
