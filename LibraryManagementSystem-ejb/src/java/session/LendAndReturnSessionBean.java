/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import entity.LendAndReturn;
import entity.Member;
import exception.BookNotFoundException;
import exception.BookOnLoanException;
import exception.FineNotPaidException;
import exception.InputDataValidationException;
import exception.LendingNotFoundException;
import exception.LoanIdExistsException;
import exception.MemberIdExistException;
import exception.MemberNotFoundException;
import exception.UnknownPersistenceException;
import exception.UnreturnedBooksException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author keith
 */
@Stateless
public class LendAndReturnSessionBean implements LendAndReturnSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method"
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public LendAndReturnSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public Long createNewLoan(LendAndReturn loan) throws LoanIdExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<LendAndReturn>> constraintViolations = validator.validate(loan);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(loan);
                em.flush();
                return loan.getLendId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    throw new UnknownPersistenceException(ex.getMessage());
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<LendAndReturn> retrieveOngoingLoansByBookId(Long bookId) {
        Query query = em.createQuery("SELECT l FROM LendAndReturn l WHERE l.bookId = :inBookId AND l.returnDate IS NULL");
        query.setParameter("inBookId", bookId);
        return query.getResultList();
    }

    @Override
//    public List<LendAndReturn> retrieveLoansByMemberId(String memberIdentityNo) {
////        Query query = em.createQuery("SELECT l FROM LendAndReturn l");
//        Query query = em.createQuery("SELECT l FROM LendAndReturn l JOIN l.member m WHERE m.identityNo LIKE :inMemberIdentityNo");
//        query.setParameter("inMemberIdentityNo", "%" + memberIdentityNo + "%");
//        return query.getResultList();
//    }
//
//    @Override
//    public List<LendAndReturn> retrieveLoansByBookTitle(String title) {
//        Query query = em.createQuery("SELECT l FROM LendAndReturn l WHERE l.book.title LIKE :inTitle");
//        query.setParameter("inTitle", "%" + title + "%");
//        return query.getResultList();
//    }

    public List<LendAndReturn> retrieveLoans() {
        Query query = em.createQuery("SELECT l FROM LendAndReturn l");
        return query.getResultList();
    }

//    public List<LendAndReturn> retrieveOngoingLoanByBookIdAndMember(Long bookId, Long memberId) {
//        Query query = em.createQuery("SELECT l FROM LendAndReturn l WHERE l.returnDate IS NULL AND l.memberId = :memberId");
//        query.setParameter("memberId", memberId);
//        return query.getResultList();
//    } 
    public List<LendAndReturn> retrieveOverdueLoansByMemberByDate(Long memberId, Date lendDate) {

        Calendar c = Calendar.getInstance();
        c.setTime(lendDate);

        // checking if day of month is smaller than 14. change month if necessary. 0 is last day of month
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        if (dayOfMonth < 15) {
            int daysInPrevMonth = 14 - dayOfMonth;
            // move back by one month
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
            // finding last day of the month
            int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            c.set(Calendar.DAY_OF_MONTH, daysInMonth - daysInPrevMonth);
        } else {
            c.add(Calendar.DATE, -14);
        }

        Date comparisonDate = c.getTime();
        // finding loans where loan taker is member, loan is overdue and loan has not been paid
        Query query = em.createQuery("SELECT l FROM LendAndReturn l WHERE l.lendDate < :supposedReturnDate AND l.memberId = :memberId AND l.returnDate IS NULL");
        query.setParameter("supposedReturnDate", comparisonDate);
        query.setParameter("memberId", memberId);
        return query.getResultList();
    }

    public LendAndReturn lendBook(LendAndReturn loan) throws BookNotFoundException, MemberNotFoundException, BookOnLoanException, UnreturnedBooksException, LoanIdExistsException, UnknownPersistenceException, InputDataValidationException { // need to get current date
        if (loan.getMember() == null) {
            throw new MemberNotFoundException("No member was chosen to take the loan");
        } else if (loan.getBook() == null) {
            throw new BookNotFoundException("No book was chosen to be borrowed");
        }

        if (retrieveOngoingLoansByBookId(loan.getBookId()).size() > 0) { // need a lend operation which checks validity of loan
            throw new BookOnLoanException("Book is still on loan and cannot be borrowed");
        } else if (retrieveOverdueLoansByMemberByDate(loan.getMemberId(), loan.getLendDate()).size() > 0) {
            throw new UnreturnedBooksException("Member is unable to borrow books until other overdue loans are resolved");
        } else {
            try {
                Long loanId = createNewLoan(loan);
                Member member = loan.getMember();
                Book book = loan.getBook();
                member.getRecords().add(loan);
                book.getRecords().add(loan);
                em.merge(book);
                em.merge(member);
                return em.find(LendAndReturn.class, loanId);
            } catch (LoanIdExistsException ex) {
                throw new LoanIdExistsException("This loan already exists");
            } catch (UnknownPersistenceException ex) {
                throw new UnknownPersistenceException("");
            } catch (InputDataValidationException ex) {
                throw new InputDataValidationException("");

            }
        }
    }

    public Double calculateFineAmount(LendAndReturn loan, Date returnDate) {

        LocalDate currentTime = returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate lendDate = loan.getLendDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (ChronoUnit.DAYS.between(lendDate, currentTime) - 14) * 0.50;
    }

    public void returnBook(LendAndReturn loan) {
        em.merge(loan);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<LendAndReturn>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
