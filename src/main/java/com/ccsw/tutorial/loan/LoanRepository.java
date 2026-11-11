package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @loan ccsw
 *
 */
public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    /**
     * Método para recuperar un listado paginado de {@link Loan}
     *
     * @param pageable pageable
     * @return {@link Page} de {@link Loan}
     */
    Page<Loan> findAll(Pageable pageable);

    List<Loan> findByGame_Id(Long gameId);

    List<Loan> findByClient_Id(Long clientId);
}