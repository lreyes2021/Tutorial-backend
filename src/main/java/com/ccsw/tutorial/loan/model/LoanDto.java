package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.game.model.Game;

import java.time.LocalDate;

public class LoanDto {
    private Long id;

    private Game game;

    private Client client;

    private LocalDate loanDate;

    private LocalDate returnDate;

    public void setId(Long id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public Client getClient() {
        return client;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
}
