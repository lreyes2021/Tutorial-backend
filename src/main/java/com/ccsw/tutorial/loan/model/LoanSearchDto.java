package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;

import java.time.LocalDate;

public class LoanSearchDto {
    private Long gameId;

    private Long clientId;

    private LocalDate date;

    private PageableRequest pageable;

    public Long getGameId() {
        return gameId;
    }

    public Long getClientId() {
        return clientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public PageableRequest getPageable() {
        return pageable;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }
}
