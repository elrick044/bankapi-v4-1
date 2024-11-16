package br.edu.utfpr.bankapi.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.edu.utfpr.bankapi.model.Account;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class TransactionControllerTest {

    @Autowired
    MockMvc mvc;

    // Gerenciador de persistência para os testes des classe
    @Autowired
    TestEntityManager entityManager;

    Account account; // Conta para os testes
    Account account2;

    @BeforeEach
    void setup() {
        account = new Account("Lauro Lima",
                12347, 1000, 0);
        entityManager.persist(account); // salvando uma conta

        account2 = new Account("Pedro Pina",
                12348, 1000, 0);
        entityManager.persist(account2); // salvando uma conta
    }

    @Test
    void deveriaRetornarStatus400ParaRequisicaoInvalida() throws Exception {
        // ARRANGE
        var json = "{}"; // Body inválido

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }

    @Test
    void deveriaRetornarStatus201ParaRequisicaoOK() throws Exception {
        // ARRANGE

        var json = """
                {
                    "receiverAccountNumber": 12347,
                    "amount": 200
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void deveriaRetornarDadosCorretosNoJson() throws Exception {
        // ARRANGE
        var json = """
                {
                    "receiverAccountNumber": 12347,
                    "amount": 200
                }
                    """;

        // ACT + ASSERT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.receiverAccount.number",
                        Matchers.equalTo(12347)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.amount", Matchers.equalTo(200.0)));
    }

    @Test
    void deveriaRetornarStatus201ParaSaqueOK() throws Exception {
        // ARRANGE

        var json = """
                {
                    "sourceAccountNumber": 12347,
                    "amount": 200
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void deveriaRetornarStatus400ParaSaqueMaiorQueSaldo() throws Exception {
        // ARRANGE

        var json = """
                {
                    "sourceAccountNumber": 12347,
                    "amount": 200000
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", res.getContentType());
    }

    @Test
    void deveriaRetornarStatus400ParaSaqueInvalido() throws Exception {
        // ARRANGE

        var json = """
                {}
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", res.getContentType());
    }

    @Test
    void deveriaRetornarDadosCorretosNoJsonSaque() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12347,
                    "amount": 200
                }
                    """;

        // ACT + ASSERT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.sourceAccount.number",
                        Matchers.equalTo(12347)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.amount", Matchers.equalTo(200.0)));
    }


    //transfer

    @Test
    void deveriaRetornarStatus201ParaTransferenciaOK() throws Exception {
        // ARRANGE

        var json = """
                {
                    "sourceAccountNumber": 12347,
                    "receiverAccountNumber": 12348,
                    "amount": 200
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    
    @Test
    void deveriaRetornarStatus400ParaTransferenciaMaiorQueSaldo() throws Exception {
        // ARRANGE

        var json = """
                {
                    "sourceAccountNumber": 12347,
                    "receiverAccountNumber": 12348,
                    "amount": 200000
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", res.getContentType());
    }

    

    @Test
    void deveriaRetornarStatus400ParaTranferenciaInvalida() throws Exception {
        // ARRANGE

        var json = """
                {
                    "sourceAccountNumber": 12347,
                    "receiverAccountNumber": 12348,
                    "amount": 200000
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", res.getContentType());
    }

    
    @Test
    void deveriaRetornarDadosCorretosNoJsonTranferencia() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12347,
                    "receiverAccountNumber": 12348,
                    "amount": 200
                }
                    """;


        // ACT + ASSERT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.sourceAccount.number",
                        Matchers.equalTo(12347)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.receiverAccount.number",
                        Matchers.equalTo(12348)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.amount", Matchers.equalTo(200.0)));
    }

}
