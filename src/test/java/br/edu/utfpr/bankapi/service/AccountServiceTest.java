package br.edu.utfpr.bankapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.bankapi.dto.AccountDTO;
import br.edu.utfpr.bankapi.dto.DepositDTO;
import br.edu.utfpr.bankapi.dto.TransferDTO;
import br.edu.utfpr.bankapi.dto.WithdrawDTO;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.repository.AccountRepository;
import br.edu.utfpr.bankapi.repository.TransactionRepository;
import br.edu.utfpr.bankapi.validations.AvailableAccountValidation;
import br.edu.utfpr.bankapi.validations.AvailableBalanceValidation;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private Account account2;

    @BeforeEach
    void setUp() {
        account = new Account("John Doe", 12345, 500.00, 100.00);
        account2 = new Account("Jane Doe", 67890, 1000.00, 200.00);
    }

    @Test
    void deveriaAtualizarConta() throws Exception {
        // ### ARRANGE ###
        long id = 1L;
        Account contaAtualizada = new Account("Jane Doe", 54321L, 100.0, 1000.0);
        AccountDTO novaContaDTO = new AccountDTO("Jane Doe", 54321L, 100.0, 1000.0);

        BDDMockito.given(accountRepository.findById(id)).willReturn(Optional.of(account));
        BDDMockito.given(accountRepository.save(BDDMockito.any(Account.class))).willReturn(contaAtualizada);

        // ### ACT ###
        Account resultado = accountService.update(id, novaContaDTO);

        // ### ASSERT ###
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(novaContaDTO.name(), resultado.getName());
        Assertions.assertEquals(novaContaDTO.number(), resultado.getNumber());
        Assertions.assertEquals(novaContaDTO.specialLimit(), resultado.getSpecialLimit());
    }

    @Test
    void deveriaLancarExcecaoQuandoContaNaoExistir() throws Exception{
        // ### ARRANGE ###
        long id = 999L;
        AccountDTO novaContaDTO = new AccountDTO("Jane Doe", 54321L, 1000.00, 1000.0);

        BDDMockito.given(accountRepository.findById(id)).willReturn(Optional.empty());

        // ### ACT & ASSERT ###
        Assertions.assertThrows(Exception.class, () -> accountService.update(id, novaContaDTO));
    }

    @Test
    void deveriaRetornarTodasAsContas() {
        // ### ARRANGE ###
        List<Account> accounts = Arrays.asList(account, account2);

        BDDMockito.given(accountRepository.findAll()).willReturn(accounts);

        // ### ACT ###
        List<Account> result = accountService.getAll();

        // ### ASSERT ###
        // Verificar que o tamanho da lista é o esperado
        Assertions.assertEquals(2, result.size());

        // Verificar que os objetos retornados são os mesmos
        Assertions.assertEquals(account, result.get(0));
        Assertions.assertEquals(account2, result.get(1));
    }

    @Test
    void deveriaRetornaObterPeloNumero() throws Exception{
        // ### ARRANGE ###
        BDDMockito.given(accountRepository.getByNumber(12345L)).willReturn(Optional.of(account));

        // ### ACT ###
        Optional<Account> result = accountService.getByNumber(12345L);

        // ### ASSERT ###
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
    }

    @Test
    void deveriaRetornaErroaoObterPeloNumero() throws Exception{
        // ### ARRANGE ###
        //BDDMockito.given(accountRepository.getByNumber(-999)).willReturn(Optional.of(account));

        // ### ACT ###
        Optional<Account> result = accountService.getByNumber(-999);

        // ### ASSERT ###
        assertFalse(result.isPresent());
        
        //assertEquals(account, result.get());
    }

    @Test
    void deveriaSalvarConta() {
        // ### ARRANGE ###
        Account accountEsperada = new Account();
        AccountDTO accountDTO = new AccountDTO(account.getName(), account.getNumber(), account.getBalance(), account.getSpecialLimit());
        accountEsperada.setName(accountDTO.name());
        accountEsperada.setNumber(accountDTO.number());
        accountEsperada.setSpecialLimit(accountDTO.specialLimit());
        accountEsperada.setBalance(0.00);

        BDDMockito.given(accountRepository.save(BDDMockito.any(Account.class))).willReturn(account);

        // ### ACT ###
        Account contaSalva = accountService.save(accountDTO);

        // ### ASSERT ###
        Assertions.assertNotNull(contaSalva);
        Assertions.assertEquals(accountDTO.name(), contaSalva.getName());
        Assertions.assertEquals(accountDTO.number(), contaSalva.getNumber());
        Assertions.assertEquals(accountDTO.specialLimit(), contaSalva.getSpecialLimit());
        //Assertions.assertEquals(0.00, contaSalva.getBalance());
    }

}
