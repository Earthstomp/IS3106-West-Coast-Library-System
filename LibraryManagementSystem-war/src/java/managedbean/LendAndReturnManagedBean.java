/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Book;
import entity.LendAndReturn;
import entity.Member;
import exception.BookNotFoundException;
import exception.BookOnLoanException;
import exception.LoanIdExistsException;
import exception.MemberNotFoundException;
import exception.UnreturnedBooksException;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import session.LendAndReturnSessionBeanLocal;

/**
 *
 * @author keith
 */
@Named(value = "lendAndReturnManagedBean")
@ViewScoped
public class LendAndReturnManagedBean implements Serializable {

    @EJB
    private LendAndReturnSessionBeanLocal lendAndReturnSessionBeanLocal;

    private Member selectedMember;
    private Book selectedBook;
    private Member member;
    private Long memberId;
    private Book book;
    private Long bookId;
    private Date lendDate;
    private Date returnDate;
    private Double fineAmount;
    private LendAndReturn loan;

    private List<LendAndReturn> loans;
    private int rows = 5;

    private String searchType = "MEMBER ID";
    private String searchString;

    /**
     * Creates a new instance of LendAndReturnManagedBean
     */
    public LendAndReturnManagedBean() {
    }

//    @PostConstruct
//    public void init() {
////        if (searchString == null || searchString.equals("")) {
////            loans = lendAndReturnSessionBeanLocal.retrieveLoansByMemberId(null);
//        loans = lendAndReturnSessionBeanLocal.retrieveLoans();
////        } else {
////            switch (getSearchType()) {
////                case "MEMBER ID": {
////                    loans = lendAndReturnSessionBeanLocal.retrieveLoansByMemberId(searchString);
////                    break;
////                }
////
////                case "BOOK TITLE": {
////                    loans = (lendAndReturnSessionBeanLocal.retrieveLoansByBookTitle(searchString));
////                    break;
////                }
////
////                default:
////                    break;
////            }
////        }
//    }

    public void createNewLoan(ActionEvent evt) {

        if (selectedMember == null || selectedBook == null || lendDate == null) {
            if (selectedMember == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No member was chosen", ""));

            }

            if (selectedBook == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No book was chosen", ""));

            }
            if (lendDate == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No date was chosen", ""));

            }
            return;
        }

        LendAndReturn newLoan = new LendAndReturn(selectedMember, selectedMember.getId(), selectedBook, selectedBook.getBookId(), lendDate);

        try {
            getLendAndReturnSessionBeanLocal().lendBook(newLoan);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Loan created", lendDate.toString()));
            member = null;
            memberId = null;
            book = null;
            bookId = null;
            lendDate = null;
            returnDate = null;
            fineAmount = null;
            selectedMember = null;
            selectedBook = null;

            return;

        } catch (MemberNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No member was chosen", ""));
        } catch (BookNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No book", ""));
        } catch (BookOnLoanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! Book is still on loan and cannot be borrowed", ""));

        } catch (UnreturnedBooksException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! Member is unable to borrow books until fines are paid", ""));
        } catch (LoanIdExistsException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! This Loan already exists in the system", ""));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! Something else", ""));

        }
    }

//    public void handleSearch() {
//        init();
//    }

    public void checkIfLoanExists(ActionEvent evt) {

        if (selectedMember == null || selectedBook == null || returnDate == null) {
            if (selectedMember == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No member was chosen", ""));

            }

            if (selectedBook == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No book was chosen", ""));

            }
            if (returnDate == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! No date was chosen", ""));

            }
            return;
        }

        // creating a pseudo loan for easier reference. lendDate will be null
        LendAndReturn loanToReturn = new LendAndReturn(selectedMember, selectedMember.getId(), selectedBook, selectedBook.getBookId(), lendDate);

        List<LendAndReturn> loans = getLendAndReturnSessionBeanLocal().retrieveOngoingLoansByBookId(loanToReturn.getBookId());

        // no such loan 
        if (loans.size() < 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "No record of such a loan under this Member"));
            return;
        } else if (loans.get(0).getMemberId() != selectedMember.getId()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "No record of such a loan under this Member"));
            return;
        } else if (loans.get(0).getLendDate().after(returnDate)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Return date cannot be before borrow date"));
            return;
        }

        loan = loans.get(0); // there should only be one loan per book at once 
        fineAmount = getLendAndReturnSessionBeanLocal().calculateFineAmount(loan, returnDate);

        PrimeFaces current = PrimeFaces.current();
        // fine needs to be paid
        if (fineAmount > 0) {
            current.executeScript("PF('fineDialog').show();");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fine Amount:", "$" + fineAmount.toString()));

            return;
        } else {
            current.executeScript("PF('returnDialog').show();");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No Fine incurred", "Loan returned before due date"));

            return;
        }
    }

    public void displayConfirmation(ActionEvent evt) {
        PrimeFaces current = PrimeFaces.current();
        current.executeScript("PF('fineDialog').hide();");
        current.executeScript("PF('returnDialog').show();");

    }

    public void returnBook(ActionEvent evt) {

        if (fineAmount < 0) {
            loan.setFineAmount(0.0);
        } else {
            loan.setFineAmount(fineAmount);

        }
        loan.setReturnDate(returnDate);
        getLendAndReturnSessionBeanLocal().returnBook(loan);

        String message = selectedMember.getFirstName() + " " + selectedMember.getLastName() + " has returned " + selectedBook.getTitle() + " on " + returnDate.toString();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Loan returned", message));
        member = null;
        memberId = null;
        book = null;
        bookId = null;
        lendDate = null;
        returnDate = null;
        fineAmount = null;
        selectedMember = null;
        selectedBook = null;
        loan = null;
        PrimeFaces current = PrimeFaces.current();
        current.executeScript("PF('returnDialog').hide();");
    }

    /**
     * @return the memberId
     */
    /**
     * @return the memberId
     */
    public Long getMemberId() {
        return memberId;
    }

    /**
     * @param memberId the memberId to set
     */
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    /**
     * @return the bookId
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * @param bookId the bookId to set
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * @return the lendDate
     */
    public Date getLendDate() {
        return lendDate;
    }

    /**
     * @param lendDate the lendDate to set
     */
    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    /**
     * @return the returnDate
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * @param returnDate the returnDate to set
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * @return the fineAmount
     */
    public Double getFineAmount() {
        return fineAmount;
    }

    /**
     * @param fineAmount the fineAmount to set
     */
    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    /**
     * @return the member
     */
    public Member getMember() {
        return member;
    }

    /**
     * @param member the member to set
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * @return the book
     */
    public Book getBook() {
        return book;
    }

    /**
     * @param book the book to set
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * @return the selectedMember
     */
    public Member getSelectedMember() {
        return selectedMember;
    }

    /**
     * @param selectedMember the selectedMember to set
     */
    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Member Selected:", selectedMember.getFirstName() + " " + selectedMember.getLastName()));

    }

    /**
     * @return the selectedBook
     */
    public Book getSelectedBook() {
        return selectedBook;
    }

    /**
     * @param selectedBook the selectedBook to set
     */
    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Selected:", selectedBook.getTitle()));

    }

    /**
     * @return the lendAndReturnSessionBeanLocal
     */
    public LendAndReturnSessionBeanLocal getLendAndReturnSessionBeanLocal() {
        return lendAndReturnSessionBeanLocal;
    }

    /**
     * @param lendAndReturnSessionBeanLocal the lendAndReturnSessionBeanLocal to
     * set
     */
    public void setLendAndReturnSessionBeanLocal(LendAndReturnSessionBeanLocal lendAndReturnSessionBeanLocal) {
        this.lendAndReturnSessionBeanLocal = lendAndReturnSessionBeanLocal;
    }

    /**
     * @return the loans
     */
    public List<LendAndReturn> getLoans() {
        return loans;
    }

    /**
     * @param loans the loans to set
     */
    public void setLoans(List<LendAndReturn> loans) {
        this.loans = loans;
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return the searchType
     */
    public String getSearchType() {
        return searchType;
    }

    /**
     * @param searchType the searchType to set
     */
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @return the date
     */
}
