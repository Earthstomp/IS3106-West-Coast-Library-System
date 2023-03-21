
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Member;
import exception.InputDataValidationException;
import exception.MemberIdExistException;
import exception.MemberNotFoundException;
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

/**
 *
 * @author keith
 */
@Stateless
public class MemberSessionBean implements MemberSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public MemberSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

// create staff
    @Override
    public Member retrieveMemberByIdentityNumber(String identityNo) throws MemberNotFoundException {
        Query query = em.createQuery("SELECT m FROM Member m WHERE m.identityNo = :inIdentityNo");
        query.setParameter("inIdentityNo", identityNo);

        try {
            return (Member) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new MemberNotFoundException("Member Identity Number " + identityNo + " does not exist!");
        }
    }
    
    @Override
    public List<Member> searchMemberbyIdentityNumber(String identityNo) {
        Query q = em.createQuery("SELECT m FROM Member m  WHERE LOWER(m.identityNo) LIKE :inIdentityNo");
            q.setParameter("inIdentityNo", "%" + identityNo.toLowerCase() + "%");
            return q.getResultList();
    }
    
      @Override
    // need to make changes to search by last name too
    public List<Member> searchMemberByFirstName(String name) {
        Query q;
        if (name != null) {
            q = em.createQuery("SELECT m FROM Member m WHERE LOWER(m.firstName) LIKE :inName");
            q.setParameter("inName", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT m FROM Member m");
        }

        return q.getResultList();
    } //end searchCustomers
    
    public List<Member> searchMemberByLastName(String name) {
        Query q;
        if (name != null) {
            q = em.createQuery("SELECT m FROM Member m WHERE LOWER(m.lastName) LIKE :name");
            q.setParameter("name", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT m FROM Member m");
        }

        return q.getResultList();
    }
    
    @Override
    public List<Member> searchMemberbyPhone(String phone) {
        Query q = em.createQuery("SELECT m FROM Member m  WHERE m.phone LIKE :inPhone");
            q.setParameter("inPhone", "%" + phone + "%");
            return q.getResultList();
    }

    public Long createNewMember(Member newMember) throws MemberIdExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<Member>> constraintViolations = validator.validate(newMember);

        if (constraintViolations.isEmpty()) {
            try {
                Member oldMember = retrieveMemberByIdentityNumber(newMember.getIdentityNo());
                throw new MemberIdExistException("Member with this Identity Number already exists");
            
            } catch (MemberNotFoundException ex1) {
                try {
                    em.persist(newMember);
                    em.flush();
                    return newMember.getId();
                    
                } catch (PersistenceException ex) {
                    if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                        if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                            throw new MemberIdExistException("Member already has an account with the same ID");
                        } else {
                            throw new UnknownPersistenceException(ex.getMessage());
                        }
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }

            }

        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
  

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Member>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
