/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import exception.InputDataValidationException;
import exception.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author keith
 */
@Local
public interface BookSessionBeanLocal {

    public List<Book> retrieveBookByTitle(String title);

    public Long createNewBook(Book newBook) throws UnknownPersistenceException, InputDataValidationException;

    public List<Book> searchBookByTitle(String name);

    public List<Book> searchBookById(String id);

    public List<Book> searchBookByIsbn(String isbn);

    public List<Book> searchBookByAuthor(String author);
    
}
