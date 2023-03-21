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
import exception.BookNotFoundException;
import exception.InputDataValidationException;
import exception.InvalidLoginCredentialException;
import exception.MemberNotFoundException;
import exception.StaffNotFoundException;
import exception.UnknownPersistenceException;
import exception.BookOnLoanException;
import exception.LoanIdExistsException;
import exception.UnreturnedBooksException;
import java.util.Calendar;
import java.util.Date;
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
public class StaffSessionBean implements StaffSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private BookSessionBeanLocal bookSessionBeanLocal;
    private LendAndReturnSessionBeanLocal lendAndReturnSessionBeanLocal;

    public StaffSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

    }

// create staff
    @Override
    public Long createNewStaff(Staff newStaff) throws UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<Staff>> constraintViolations = validator.validate(newStaff);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newStaff);
                em.flush();

                return newStaff.getStaffId();
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

    @Override
    public Staff retrieveStaffByUsername(String username) throws StaffNotFoundException {
        Query query = em.createQuery("SELECT s FROM Staff s WHERE s.userName = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (Staff) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new StaffNotFoundException("Staff Username " + username + " does not exist!");
        }
    }

    @Override
    public Staff staffLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            Staff staff = retrieveStaffByUsername(username);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + staff.getSalt()));

            if (staff.getPassword().equals(password)) { // removed salt
                return staff;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (StaffNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    

    
    // lend book to member
    // check fine amount
    // return book
    // logout

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Staff>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
