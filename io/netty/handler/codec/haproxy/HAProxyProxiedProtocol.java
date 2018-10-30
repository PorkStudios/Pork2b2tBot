/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.haproxy;

public enum HAProxyProxiedProtocol {
    UNKNOWN(0, AddressFamily.AF_UNSPEC, TransportProtocol.UNSPEC),
    TCP4(17, AddressFamily.AF_IPv4, TransportProtocol.STREAM),
    TCP6(33, AddressFamily.AF_IPv6, TransportProtocol.STREAM),
    UDP4(18, AddressFamily.AF_IPv4, TransportProtocol.DGRAM),
    UDP6(34, AddressFamily.AF_IPv6, TransportProtocol.DGRAM),
    UNIX_STREAM(49, AddressFamily.AF_UNIX, TransportProtocol.STREAM),
    UNIX_DGRAM(50, AddressFamily.AF_UNIX, TransportProtocol.DGRAM);
    
    private final byte byteValue;
    private final AddressFamily addressFamily;
    private final TransportProtocol transportProtocol;

    private HAProxyProxiedProtocol(byte byteValue, AddressFamily addressFamily, TransportProtocol transportProtocol) {
        this.byteValue = byteValue;
        this.addressFamily = addressFamily;
        this.transportProtocol = transportProtocol;
    }

    public static HAProxyProxiedProtocol valueOf(byte tpafByte) {
        switch (tpafByte) {
            case 17: {
                return TCP4;
            }
            case 33: {
                return TCP6;
            }
            case 0: {
                return UNKNOWN;
            }
            case 18: {
                return UDP4;
            }
            case 34: {
                return UDP6;
            }
            case 49: {
                return UNIX_STREAM;
            }
            case 50: {
                return UNIX_DGRAM;
            }
        }
        throw new IllegalArgumentException("unknown transport protocol + address family: " + (tpafByte & 255));
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public AddressFamily addressFamily() {
        return this.addressFamily;
    }

    public TransportProtocol transportProtocol() {
        return this.transportProtocol;
    }

    public static enum TransportProtocol {
        UNSPEC(0),
        STREAM(1),
        DGRAM(2);
        
        private static final byte TRANSPORT_MASK = 15;
        private final byte transportByte;

        private TransportProtocol(byte transportByte) {
            this.transportByte = transportByte;
        }

        public static TransportProtocol valueOf(byte tpafByte) {
            int transportProtocol = tpafByte & 15;
            switch ((byte)transportProtocol) {
                case 1: {
                    return STREAM;
                }
                case 0: {
                    return UNSPEC;
                }
                case 2: {
                    return DGRAM;
                }
            }
            throw new IllegalArgumentException("unknown transport protocol: " + transportProtocol);
        }

        public byte byteValue() {
            return this.transportByte;
        }
    }

    public static enum AddressFamily {
        AF_UNSPEC(0),
        AF_IPv4(16),
        AF_IPv6(32),
        AF_UNIX(48);
        
        private static final byte FAMILY_MASK = -16;
        private final byte byteValue;

        private AddressFamily(byte byteValue) {
            this.byteValue = byteValue;
        }

        public static AddressFamily valueOf(byte tpafByte) {
            int addressFamily = tpafByte & -16;
            switch ((byte)addressFamily) {
                case 16: {
                    return AF_IPv4;
                }
                case 32: {
                    return AF_IPv6;
                }
                case 0: {
                    return AF_UNSPEC;
                }
                case 48: {
                    return AF_UNIX;
                }
            }
            throw new IllegalArgumentException("unknown address family: " + addressFamily);
        }

        public byte byteValue() {
            return this.byteValue;
        }
    }

}

