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
public class UnreturnedBooksException extends Exception {

    /**
     * Creates a new instance of <code>UnreturnedBooksException</code> without
     * detail message.
     */
    public UnreturnedBooksException() {
    }

    /**
     * Constructs an instance of <code>UnreturnedBooksException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnreturnedBooksException(String msg) {
        super(msg);
    }
}
