/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import java.io.PrintStream;

public class Version {
    public static void main(String[] args) {
        System.out.println(Version.getVersion());
    }

    public static String getVersion() {
        String version = Version.class.getPackage().getImplementationVersion();
        if (version != null) {
            return "trove4j version " + version;
        }
        return "Sorry no Implementation-Version manifest attribute available";
    }
}

