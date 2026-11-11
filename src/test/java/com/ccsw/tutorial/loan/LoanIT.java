package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    public static final Long DELETE_LOAN_ID = 3L;
    public static final Long MODIFY_LOAN_ID = 2L;

    private static final int TOTAL_LOANS = 3;
    private static final int PAGE_SIZE = 2;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };

    ParameterizedTypeReference<List<LoanDto>> responseTypeList = new ParameterizedTypeReference<List<LoanDto>>() {
    };

    @Test
    public void findFirstPageShouldReturnFirstResults() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    @Test
    public void findSecondPageShouldReturnLastResults() {

        int elementsCount = TOTAL_LOANS - PAGE_SIZE;

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(1, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(elementsCount, response.getBody().getContent().size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewLoan() {

        LoanDto dto = new LoanDto();

        Game game = new Game();
        game.setId(1L);

        Client client = new Client();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(LocalDate.of(2026, 11, 1));

        dto.setReturnDate(LocalDate.of(2026, 11, 10));

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS + 1));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertEquals(TOTAL_LOANS + 1, response.getBody().getTotalElements());
    }

    @Test
    public void modifyWithExistIdShouldModifyLoan() {

        LoanDto dto = new LoanDto();

        Game game = new Game();
        game.setId(3L);

        Client client = new Client();
        client.setId(2L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(LocalDate.of(2026, 6, 10));

        dto.setReturnDate(LocalDate.of(2026, 6, 20));

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_LOAN_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        LoanDto loan = response.getBody().getContent().stream().filter(item -> item.getId().equals(MODIFY_LOAN_ID)).findFirst().orElse(null);

        assertNotNull(loan);

        assertEquals(LocalDate.of(2026, 6, 20), loan.getReturnDate());
    }

    @Test
    public void modifyWithNotExistIdShouldThrowException() {

        long loanId = TOTAL_LOANS + 1;

        LoanDto dto = new LoanDto();

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + loanId, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void deleteWithExistsIdShouldDeleteLoan() {

        long newLoanSize = TOTAL_LOANS - 1;

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_LOAN_ID, HttpMethod.DELETE, null, Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertEquals(newLoanSize, response.getBody().getTotalElements());
    }

    @Test
    public void deleteWithNotExistsIdShouldThrowException() {

        long deleteLoanId = TOTAL_LOANS + 1;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + deleteLoanId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveWithReturnDateBeforeLoanDateShouldThrowException() {

        LoanDto dto = new LoanDto();

        Game game = new Game();
        game.setId(1L);

        Client client = new Client();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(LocalDate.of(2026, 11, 20));

        dto.setReturnDate(LocalDate.of(2026, 11, 10));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void saveWithMoreThanFourteenDaysShouldThrowException() {

        LoanDto dto = new LoanDto();

        Game game = new Game();
        game.setId(1L);

        Client client = new Client();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(LocalDate.of(2026, 11, 1));

        dto.setReturnDate(LocalDate.of(2026, 11, 20));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void findByGameShouldReturnOnlyFilteredResults() {

        LoanSearchDto searchDto = new LoanSearchDto();

        searchDto.setGameId(1L);

        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);

        response.getBody().getContent().forEach(loan -> assertEquals(1L, loan.getGame().getId()));
    }

    @Test
    public void findByClientShouldReturnOnlyFilteredResults() {

        LoanSearchDto searchDto = new LoanSearchDto();

        searchDto.setClientId(1L);

        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);

        response.getBody().getContent().forEach(loan -> assertEquals(1L, loan.getClient().getId()));
    }

}