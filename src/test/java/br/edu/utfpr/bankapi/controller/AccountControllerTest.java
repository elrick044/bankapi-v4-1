//TESTE 02
package br.edu.utfpr.bankapi.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.edu.utfpr.bankapi.dto.AccountDTO;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.service.AccountService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class AccountControllerTest {
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
    void deveriaRetorna200ParaRequisicaoOkParaObterTodosUsuarios() throws Exception{
        // ARRANGE

        var json = """
                {}
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.get("/account"))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void deveriaRetorna200ParaRequisicaoOkParaObterUsuarioPorId() throws Exception{
        // ARRANGE

        var json = """
                {}
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.get("/account/12347"))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void deveriaRetorna404ParaUsuarioNaoEncontrado() throws Exception{
        // ARRANGE

        var json = """
                {}
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.get("/account/-99999"))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(404, res.getStatus());
        //Assertions.assertEquals("application/json", res.getContentType());
    }
    

    ///TESTE 02
    @Test
    void deveriaRetornar201ParaUsuarioCadastrado() throws Exception{
        // ARRANGE

        var json = """
                {
                    "name": "Marcos Muller",
                    "number": 12349,
                    "balance": 500,
                    "specialLimit": 0
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/account")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void deveriaRetornar400ParaUsuarioCadastradoEJsonInvalido() throws Exception{
        // ARRANGE

        var json = """
                {}
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/account")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        //Assertions.assertEquals("application/json", res.getContentType());
    }

    //update

    @Test
    void deveriaRetornar201ParaUsuarioEditado() throws Exception{
        // ARRANGE

        var json = """
                {
                    "name": "Marcos Muller Moura",
                    "number": 12349,
                    "balance": 1000,
                    "specialLimit": 0
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.put("/account/1")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void deveriaRetornar400ParaUsuarioEditadoEJsonInvalido() throws Exception{
        // ARRANGE

        var json = """
                {}
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.put("/account/1")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        //Assertions.assertEquals("application/json", res.getContentType());
    }


    @Test
    void deveriaRetornar404ParaUsuarioEditadoNaoEncontrado() throws Exception{
        // ARRANGE

        var json = """
                {
                    "name": "Marcos Muller Moura",
                    "number": 12349,
                    "balance": 1000,
                    "specialLimit": 0
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.put("/account/-99")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(404, res.getStatus());
        //Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void deveriaRetornarJsonCorretoParaUsuarioEditado() throws Exception{
        // ARRANGE

        var json = """
                {
                    "name": "Marcos Muller Moura",
                    "number": 12349,
                    "balance": 500,
                    "specialLimit": 100
                }
                    """;

        // ACT + ASSERT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/account/1")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.id",
                        Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.name",
                        Matchers.equalTo("Marcos Muller Moura")))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.number",
                        Matchers.equalTo(12349)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.balance",
                        Matchers.equalTo(500)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.specialLimit",
                        Matchers.equalTo(100)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.amount", Matchers.equalTo(200.0)));
    }

}
