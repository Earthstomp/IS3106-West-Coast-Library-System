/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Staff;
import exception.InputDataValidationException;
import exception.InvalidLoginCredentialException;
import exception.StaffNotFoundException;
import exception.UnknownPersistenceException;
import javax.ejb.Local;

/**
 *
 * @author keith
 */
@Local
public interface StaffSessionBeanLocal {

    public Long createNewStaff(Staff newStaff) throws UnknownPersistenceException, InputDataValidationException;

    public Staff retrieveStaffByUsername(String username) throws StaffNotFoundException;

    public Staff staffLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
