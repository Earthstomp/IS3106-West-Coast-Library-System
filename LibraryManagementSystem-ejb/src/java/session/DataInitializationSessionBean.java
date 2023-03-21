/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import entity.Member;
import entity.Staff;
import exception.InputDataValidationException;
import exception.MemberIdExistException;
import exception.StaffNotFoundException;
import exception.UnknownPersistenceException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author keith
 */
@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private BookSessionBeanLocal bookSessionBeanLocal;

    @EJB
    private MemberSessionBeanLocal memberSessionBeanLocal;

    @EJB
    private StaffSessionBeanLocal staffSessionBeanLocal;

    public DataInitializationSessionBean() {

    }

    @PostConstruct
    public void postConstruct() {
        
        try{
                    Staff eric = staffSessionBeanLocal.retrieveStaffByUsername("eric");

        } catch (StaffNotFoundException ex) {
                        initializeData();

        }
        
//        
//        
//        if (eric == null) {
//        }

    }

    private void initializeData() {

        // create staff
        try {
            Long staffEric = staffSessionBeanLocal.createNewStaff(new Staff("Eric", "Some", "eric", "password"));
            Long staffSarah = staffSessionBeanLocal.createNewStaff(new Staff("Sarah", "Brightman", "sarah", "password"));
        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            ex.printStackTrace();
        }

        // create members
        try {
            Long memberTony = memberSessionBeanLocal.createNewMember(new Member("Tony", "Shade", 'M', 31, "S8900678A", "83722773", "13 Jurong East, Ave 3"));
            Long memberDewi = memberSessionBeanLocal.createNewMember(new Member("Dewi", "Tan", 'F', 35, "S8581028X", "94602711", "15 Computing Dr"));
        } catch (InputDataValidationException | MemberIdExistException | UnknownPersistenceException ex) {
            ex.printStackTrace();
        }

        //create books
        try {
            Long book1 = bookSessionBeanLocal.createNewBook(new Book("Anna Karenina", "0451528611", "Leo Tolstoy"));
            Long book2 = bookSessionBeanLocal.createNewBook(new Book("Madame Bovary", "979-8649042031", "Gustave Flaubert"));
            Long book3 = bookSessionBeanLocal.createNewBook(new Book("Hamlet", "1980625026", "William Shakespeare"));
            Long book4 = bookSessionBeanLocal.createNewBook(new Book("The Hobbit", "9780007458424", "J R R Tolkien"));
            Long book5 = bookSessionBeanLocal.createNewBook(new Book("Great Expectations", "1521853592", "Charles Dickens"));
            Long book6 = bookSessionBeanLocal.createNewBook(new Book("Pride and Prejudice", "979-8653642272", "Jane Austen"));
            Long book7 = bookSessionBeanLocal.createNewBook(new Book("Wuthering Heights", "3961300224", "Emily Brontë"));

        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            ex.printStackTrace();
        }

    }
}
