/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author keith
 */
public class LoanIdExistsException extends Exception {

    /**
     * Creates a new instance of <code>LoanIdExistException</code> without
     * detail message.
     */
    public LoanIdExistsException() {
    }

    /**
     * Constructs an instance of <code>LoanIdExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public LoanIdExistsException(String msg) {
        super(msg);
    }
}
