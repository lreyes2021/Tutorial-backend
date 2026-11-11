package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.game.model.Game;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * @author ccsw
 *
 */
@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "loanDate")
    private LocalDate loanDate;

    @Column(name = "returnDate")
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
