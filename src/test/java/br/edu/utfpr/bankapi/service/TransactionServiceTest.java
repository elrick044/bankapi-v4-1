package br.edu.utfpr.bankapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.utfpr.bankapi.dto.DepositDTO;
import br.edu.utfpr.bankapi.dto.TransferDTO;
import br.edu.utfpr.bankapi.dto.WithdrawDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.model.TransactionType;
import br.edu.utfpr.bankapi.repository.AccountRepository;
import br.edu.utfpr.bankapi.repository.TransactionRepository;
import br.edu.utfpr.bankapi.validations.AvailableAccountValidation;
import br.edu.utfpr.bankapi.validations.AvailableBalanceValidation;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	AvailableAccountValidation availableAccountValidation;

	@Mock
	AvailableBalanceValidation availableBalanceValidation;

	@Mock
	AccountRepository accountRepository;

	@Mock
	TransactionRepository transactionRepository;

	@InjectMocks
	TransactionService service;

	@Mock
	Transaction transaction;

	@Captor
	ArgumentCaptor<Transaction> transactionCaptor;

	DepositDTO depositDTO;
	WithdrawDTO withdrawDTO;
	TransferDTO transferDTO;

	@Mock
	Account sourceAccount, receiverAccount;

	/**
	 * @throws NotFoundException
	 * 
	 */
	@Test
	void deveriaDepositar() throws NotFoundException {
		// Garantir que as validações sejam executadas
		// Garantir que a transação foi salva

		// ### ARRANGE ###
		double saldoInicial = 150.85;

		depositDTO = new DepositDTO(12345, 1000);
		receiverAccount = new Account("John Smith", 12345, saldoInicial, 0);

		// Comportamento do availableAccountValidation
		BDDMockito.given(availableAccountValidation.validate(depositDTO.receiverAccountNumber()))
				.willReturn(receiverAccount);

		// Comportamento do receiverAccount
		// BDDMockito.given(receiverAccount.getBalance()).willReturn(1000D);
		// BDDMockito.given(receiverAccount.getSpecialLimit()).willReturn(0D);

		// ### ACT ###
		service.deposit(depositDTO);

		// ### ASSERT ###
		// BDDMockito.then(transactionRepository).should().save(BDDMockito.any());
		BDDMockito.then(transactionRepository).should().save(transactionCaptor.capture());
		Transaction transactionSalva = transactionCaptor.getValue();

		// A conta destinatária deveria ser a mesma na transação
		Assertions.assertEquals(receiverAccount, transactionSalva.getReceiverAccount());
		// O valor de depósito deveria ser o mesmo na transação
		Assertions.assertEquals(depositDTO.amount(), transactionSalva.getAmount());
		// O tipo de operação deveria ser DEPOSIT na transação
		Assertions.assertEquals(TransactionType.DEPOSIT, transactionSalva.getType());
		// O saldo na conta de destino deveria ser acrescido com o valor da transação
		Assertions.assertEquals(saldoInicial + depositDTO.amount(), transactionSalva.getReceiverAccount().getBalance());
	}

	/**
	 * @throws NotFoundException
	 * 
	 */
	@Test
	void deveriaSacar() throws NotFoundException {
		// Garantir que as validações sejam executadas
		// Garantir que a transação foi salva

		// ### ARRANGE ###
		double saldoInicial = 150.85;

		withdrawDTO = new WithdrawDTO(12345, 100);
		sourceAccount = new Account("John Smith", 12345, saldoInicial, 0);

		// Comportamento do availableAccountValidation
		BDDMockito.given(availableAccountValidation.validate(withdrawDTO.sourceAccountNumber()))
				.willReturn(sourceAccount);

		// ### ACT ###
		service.withdraw(withdrawDTO);

		// ### ASSERT ###
		// BDDMockito.then(transactionRepository).should().save(BDDMockito.any());
		BDDMockito.then(transactionRepository).should().save(transactionCaptor.capture());
		Transaction transactionSalva = transactionCaptor.getValue();

		// A conta destinatária deveria ser a mesma na transação
		Assertions.assertEquals(sourceAccount, transactionSalva.getSourceAccount());
		// O valor de depósito deveria ser o mesmo na transação
		Assertions.assertEquals(withdrawDTO.amount(), transactionSalva.getAmount());
		// O tipo de operação deveria ser WITHDRAW na transação
		Assertions.assertEquals(TransactionType.WITHDRAW, transactionSalva.getType());
		// O saldo na conta de destino deveria ser acrescido com o valor da transação
		Assertions.assertEquals(saldoInicial - withdrawDTO.amount(),
				transactionSalva.getSourceAccount().getBalance());
	}

	/**
	 * @throws NotFoundException
	 * 
	 */
	@Test
	void deveriaTransferir() throws NotFoundException {
		// Garantir que as validações sejam executadas
		// Garantir que a transação foi salva

		// ### ARRANGE ###
		double saldoInicialSource = 500.00;
		double saldoInicialReceiver = 200.00;
		double valorTransferencia = 150.00;

		transferDTO = new TransferDTO(12345, 777, valorTransferencia);

		sourceAccount = new Account("José", 12345, saldoInicialSource, 0);
		receiverAccount = new Account("Juca Jones", 777, saldoInicialReceiver, 0);

		// Configurar validações
		BDDMockito.given(availableAccountValidation.validate(transferDTO.sourceAccountNumber()))
				.willReturn(sourceAccount);
		BDDMockito.given(availableAccountValidation.validate(transferDTO.receiverAccountNumber()))
				.willReturn(receiverAccount);

		// ### ACT ###
		service.transfer(transferDTO);

		// ### ASSERT ###
		BDDMockito.then(transactionRepository).should().save(transactionCaptor.capture());
		Transaction transactionSalva = transactionCaptor.getValue();

		// Verificar transação
		Assertions.assertEquals(sourceAccount, transactionSalva.getSourceAccount());
		Assertions.assertEquals(receiverAccount, transactionSalva.getReceiverAccount());
		Assertions.assertEquals(valorTransferencia, transactionSalva.getAmount());
		Assertions.assertEquals(TransactionType.TRANSFER, transactionSalva.getType());

		// Verificar saldos
		Assertions.assertEquals(saldoInicialSource - valorTransferencia, sourceAccount.getBalance());
		Assertions.assertEquals(saldoInicialReceiver + valorTransferencia, receiverAccount.getBalance());
	}

}