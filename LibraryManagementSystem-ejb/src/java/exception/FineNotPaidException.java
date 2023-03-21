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
public class FineNotPaidException extends Exception {

    /**
     * Creates a new instance of <code>FineNotPaidException</code> without
     * detail message.
     */
    public FineNotPaidException() {
    }

    /**
     * Constructs an instance of <code>FineNotPaidException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FineNotPaidException(String msg) {
        super(msg);
    }
}
