package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {

        Specification<Loan> specification = Specification.where(null);

        if (dto.getGameId() != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("game").get("id"), dto.getGameId()));
        }

        if (dto.getClientId() != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("client").get("id"), dto.getClientId()));
        }

        if (dto.getDate() != null) {
            specification = specification.and((root, query, cb) -> cb.and(cb.lessThanOrEqualTo(root.get("loanDate"), dto.getDate()), cb.greaterThanOrEqualTo(root.get("returnDate"), dto.getDate())));
        }

        return this.loanRepository.findAll(specification, dto.getPageable().getPageable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, LoanDto data) {

        if (data.getReturnDate().isBefore(data.getLoanDate())) {
            throw new LoanException("La fecha de devolución no puede ser anterior a la fecha de préstamo");
        }

        if (data.getReturnDate().isAfter(data.getLoanDate().plusDays(14))) {
            throw new LoanException("El préstamo no puede superar los 14 días");
        }

        List<Loan> gameLoans = this.loanRepository.findByGame_Id(data.getGame().getId());

        for (Loan existingLoan : gameLoans) {

            if (id != null && existingLoan.getId().equals(id)) {
                continue;
            }

            boolean overlap = !data.getReturnDate().isBefore(existingLoan.getLoanDate()) && !data.getLoanDate().isAfter(existingLoan.getReturnDate());
            if (overlap) {
                throw new LoanException("El juego ya está prestado en las fechas seleccionadas");
            }
        }

        List<Loan> clientLoans = this.loanRepository.findByClient_Id(data.getClient().getId());

        LocalDate currentDate = data.getLoanDate();

        while (!currentDate.isAfter(data.getReturnDate())) {

            int activeLoans = 0;

            for (Loan existingLoan : clientLoans) {

                if (id != null && existingLoan.getId().equals(id)) {
                    continue;
                }

                boolean active = !currentDate.isBefore(existingLoan.getLoanDate()) && !currentDate.isAfter(existingLoan.getReturnDate());
                if (active) {
                    activeLoans++;
                }
            }

            if (activeLoans >= 2) {
                throw new LoanException("El cliente ya tiene dos préstamos activos");
            }

            currentDate = currentDate.plusDays(1);
        }

        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.loanRepository.findById(id).orElse(null);
        }

        BeanUtils.copyProperties(data, loan, "id");

        this.loanRepository.save(loan);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {

        if (this.loanRepository.findById(id).orElse(null) == null) {
            throw new Exception("Not exists");
        }

        this.loanRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Loan> findAll() {

        return (List<Loan>) this.loanRepository.findAll();
    }

}
