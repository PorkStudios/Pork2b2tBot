/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import java.io.PrintStream;
import java.io.PrintWriter;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class TransformerException
extends Exception {
    private Throwable _exception;

    public Throwable getCause() {
        return this._exception;
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (this._exception != null) {
            this._exception.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (this._exception != null) {
            this._exception.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (this._exception != null) {
            this._exception.printStackTrace(pw);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(this.getClass().getName()).append(": ").append(super.toString());
        if (this._exception != null) {
            sb.append("; caused by: ").append(this._exception.toString());
        }
        return sb.toString();
    }

    private final /* synthetic */ void this() {
        this._exception = null;
    }

    public TransformerException() {
        this.this();
    }

    public TransformerException(String details) {
        super(details);
        this.this();
    }

    public TransformerException(Throwable cause) {
        this.this();
        this._exception = cause;
    }

    public TransformerException(String details, Throwable cause) {
        super(details);
        this.this();
        this._exception = cause;
    }
}

