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
public class BookNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>BookNotFoundException</code> without
     * detail message.
     */
    public BookNotFoundException() {
    }

    /**
     * Constructs an instance of <code>BookNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BookNotFoundException(String msg) {
        super(msg);
    }
}
