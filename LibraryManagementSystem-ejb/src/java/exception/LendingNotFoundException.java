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
public class LendingNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>LendingNotFoundException</code> without
     * detail message.
     */
    public LendingNotFoundException() {
    }

    /**
     * Constructs an instance of <code>LendingNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public LendingNotFoundException(String msg) {
        super(msg);
    }
}
