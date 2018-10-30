/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.KeyManagementException;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class KeyAgreementException
extends KeyManagementException
implements Serializable {
    private Throwable cause;

    public Throwable getCause() {
        return this.cause;
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (this.cause != null) {
            this.cause.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (this.cause != null) {
            this.cause.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (this.cause != null) {
            this.cause.printStackTrace(pw);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(this.getClass().getName()).append(": ").append(super.toString());
        if (this.cause != null) {
            sb.append("; caused by: ").append(this.cause.toString());
        }
        return sb.toString();
    }

    private final /* synthetic */ void this() {
        this.cause = null;
    }

    public KeyAgreementException() {
        this.this();
    }

    public KeyAgreementException(String detail) {
        super(detail);
        this.this();
    }

    public KeyAgreementException(String detail, Throwable cause) {
        super(detail);
        this.this();
        this.cause = cause;
    }
}

