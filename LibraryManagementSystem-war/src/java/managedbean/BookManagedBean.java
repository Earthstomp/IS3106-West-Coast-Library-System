/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Book;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import session.BookSessionBeanLocal;

/**
 *
 * @author keith
 */
@Named(value = "bookManagedBean")
@ViewScoped
public class BookManagedBean implements Serializable {

    @EJB
    private BookSessionBeanLocal bookSessionBeanLocal;

    private Long id;
    private String title;
    private String isbn;
    private String author;

    private List<Book> books;
    private Book selectedBook;
    private int rows = 5;

    private String searchType = "TITLE";
    private String searchString;

    public BookManagedBean() {
    }

    @PostConstruct
    public void init() {
        if (searchString == null || searchString.equals("")) {
            books = bookSessionBeanLocal.searchBookByTitle(null);
        } else {
            switch (getSearchType()) {
                case "TITLE": {
                    books = (bookSessionBeanLocal.searchBookByTitle(searchString));
                    break;
                }
                
                case "ID": {
                    books = (bookSessionBeanLocal.searchBookById(searchString));
                    break;
                }
                
                case "ISBN": {
                    books = (bookSessionBeanLocal.searchBookByIsbn(searchString));
                    break;
                }
                
                case "AUTHOR": {
                    books = (bookSessionBeanLocal.searchBookByAuthor(searchString));
                    break;
                }
                default:
                    break;
            }
        }
    }
    
//    public void loadSelectedMember() {
//        FacesContext context = FacesContext.getCurrentInstance();
//
//        try {
//            this.selectedMember = memberSessionBeanLocal.retrieveMemberByIdentityNumber(id);
//            firstName = this.selectedMember.getFirstName();
//            lastName = this.selectedMember.getLastName();
//            gender = this.selectedMember.getGender();
//            
//            age = this.selectedMember.getAge();
//            id = this.selectedMember.getIdentityNo();
//            phone = this.selectedMember.getPhone();
//            address = this.selectedMember.getAddress();
//            
//        } catch (Exception e) {
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! Unable to load customer", ""));
//        }
//    } //end loadSelectedCustomer

    public void handleSearch() {
        init();
    } //end handleSearch

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @param isbn the isbn to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the books
     */
    public List<Book> getBooks() {
        return books;
    }

    /**
     * @param books the books to set
     */
    public void setBooks(List<Book> books) {
        this.books = books;
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
    
}
