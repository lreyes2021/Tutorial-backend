package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;
    
    @Test
    public void saveWithOverlappedGameShouldThrowException() {

        Game game = new Game();
        game.setId(1L);

        Client client = new Client();
        client.setId(2L);

        Loan existingLoan = new Loan();
        existingLoan.setId(1L);
        existingLoan.setGame(game);
        existingLoan.setClient(client);
        existingLoan.setLoanDate(LocalDate.of(2026, 11, 1));
        existingLoan.setReturnDate(LocalDate.of(2026, 11, 10));

        when(loanRepository.findByGame_Id(1L)).thenReturn(List.of(existingLoan));

        LoanDto dto = new LoanDto();

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(LocalDate.of(2026, 11, 5));

        dto.setReturnDate(LocalDate.of(2026, 11, 8));

        assertThrows(LoanException.class, () -> loanService.save(null, dto));
    }

    @Test
    public void saveWithMoreThanTwoActiveLoansShouldThrowException() {

        Client client = new Client();
        client.setId(1L);

        Game game1 = new Game();
        game1.setId(1L);

        Game game2 = new Game();
        game2.setId(2L);

        Loan loan1 = new Loan();
        loan1.setId(1L);
        loan1.setGame(game1);
        loan1.setClient(client);
        loan1.setLoanDate(LocalDate.of(2026, 11, 1));
        loan1.setReturnDate(LocalDate.of(2026, 11, 10));

        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setGame(game2);
        loan2.setClient(client);
        loan2.setLoanDate(LocalDate.of(2026, 11, 1));
        loan2.setReturnDate(LocalDate.of(2026, 11, 10));

        when(loanRepository.findByClient_Id(1L)).thenReturn(List.of(loan1, loan2));

        when(loanRepository.findByGame_Id(3L)).thenReturn(Collections.emptyList());

        LoanDto dto = new LoanDto();

        Game game3 = new Game();
        game3.setId(3L);

        dto.setGame(game3);
        dto.setClient(client);

        dto.setLoanDate(LocalDate.of(2026, 11, 5));

        dto.setReturnDate(LocalDate.of(2026, 11, 8));

        assertThrows(LoanException.class, () -> loanService.save(null, dto));
    }
}