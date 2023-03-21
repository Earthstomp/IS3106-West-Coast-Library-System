/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import entity.LendAndReturn;
import entity.Member;
import entity.Staff;
import exception.InputDataValidationException;
import exception.InvalidLoginCredentialException;
import exception.StaffNotFoundException;
import exception.UnknownPersistenceException;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.security.CryptographicHelper;

/**
 *
 * @author keith
 */
@Stateless
public class BookSessionBean implements BookSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public BookSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

// create staff
    @Override
    public Long createNewBook(Book newBook) throws UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<Book>> constraintViolations = validator.validate(newBook);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newBook);
                em.flush();

                return newBook.getBookId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
//                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
//                        throw new StaffUsernameExistException();
//                    } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override // can return an empty list
    public List<Book> retrieveBookByTitle(String title) {
        Query query = em.createQuery("SELECT b FROM Book b WHERE b.title LIKE :inTitle");
        query.setParameter("inTitle", "%" + title + "%");

        return query.getResultList();
    }

    @Override
    public List<Book> searchBookByTitle(String name) {
        Query q;
        if (name != null) {
            q = em.createQuery("SELECT b FROM Book b WHERE LOWER(b.title) LIKE :inName");
            q.setParameter("inName", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT b FROM Book b");
        }

        return q.getResultList();
    } //end searchCustomers

    public List<Book> searchBookById(String id) {
        Long idLong = Long.parseLong(id);
        Query q = em.createQuery("SELECT b FROM Book b  WHERE b.bookId = :inId ");
        q.setParameter("inId", idLong);
        return q.getResultList();
    }

    @Override
    public List<Book> searchBookByIsbn(String isbn) {
        Query q = em.createQuery("SELECT b FROM Book b WHERE LOWER(b.isbn) LIKE :inIsbn");
        q.setParameter("inIsbn", "%" + isbn.toLowerCase() + "%");
        return q.getResultList();
    }

    @Override
    public List<Book> searchBookByAuthor(String author) {
        Query q = em.createQuery("SELECT b FROM Book b WHERE LOWER(b.author) LIKE :inAuthor");
        q.setParameter("inAuthor", "%" + author.toLowerCase() + "%");
        return q.getResultList();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Book>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
