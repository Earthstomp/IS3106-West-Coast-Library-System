/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.LendAndReturn;
import exception.BookNotFoundException;
import exception.BookOnLoanException;
import exception.FineNotPaidException;
import exception.InputDataValidationException;
import exception.LendingNotFoundException;
import exception.LoanIdExistsException;
import exception.MemberNotFoundException;
import exception.UnknownPersistenceException;
import exception.UnreturnedBooksException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author keith
 */
@Local
public interface LendAndReturnSessionBeanLocal {

    public List<LendAndReturn> retrieveOngoingLoansByBookId(Long bookId);

    public Long createNewLoan(LendAndReturn loan) throws LoanIdExistsException, UnknownPersistenceException, InputDataValidationException;

    public LendAndReturn lendBook(LendAndReturn loan) throws BookNotFoundException, MemberNotFoundException, BookOnLoanException, UnreturnedBooksException, LoanIdExistsException, UnknownPersistenceException, InputDataValidationException;

    public Double calculateFineAmount(LendAndReturn loan, Date returnDate);

    public void returnBook(LendAndReturn loan);

    public List<LendAndReturn> retrieveOverdueLoansByMemberByDate(Long memberId, Date lendDate);

    public List<LendAndReturn> retrieveLoans();
    
}
