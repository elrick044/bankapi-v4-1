package br.edu.utfpr.bankapi.validations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.repository.AccountRepository;

class AvailableAccountValidationTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AvailableAccountValidation availableAccountValidation;

    private Account account;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Inicializar a conta mockada
        account = new Account();
        account.setId(1L);
        account.setName("John Doe");
        account.setNumber(12345L);
        account.setBalance(1000.0);
        account.setSpecialLimit(500.0);
    }

    @Test
    void deveriaValidarContaExistente() throws NotFoundException {
        // ### ARRANGE ###
        long numeroConta = 12345L;
        BDDMockito.given(accountRepository.getByNumber(numeroConta))
                  .willReturn(Optional.of(account));

        // ### ACT ###
        Account resultado = availableAccountValidation.validate(numeroConta);

        // ### ASSERT ###
        assertNotNull(resultado);
        assertEquals(account.getNumber(), resultado.getNumber());
        assertEquals(account.getName(), resultado.getName());
    }

    @Test
    void deveriaLancarExcecaoQuandoContaNaoExiste() {
        // ### ARRANGE ###
        long numeroContaInexistente = 54321L;
        BDDMockito.given(accountRepository.getByNumber(numeroContaInexistente))
                  .willReturn(Optional.empty());

        // ### ACT & ASSERT ###
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> availableAccountValidation.validate(numeroContaInexistente));

        assertEquals("Conta " + numeroContaInexistente + " inexistente", exception.getMessage());
    }
}
