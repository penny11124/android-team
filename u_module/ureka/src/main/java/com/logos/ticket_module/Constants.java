package com.logos.ticket_module;

import org.fidoalliance.fdo.protocol.Const;

public abstract class Constants {
    public static final String PROTOCOL_VERSION = "v1";

    public static final String UREKA_KEX_ALG_NAME = Const.ECDH384_ALG_NAME;

    // general ticket
    public static final int CBOR_TICKET_PROTOCOL_VERSION = 0;
    public static final int CBOR_TICKET_SESSION_NUMBER = 1;
    public static final int CBOR_TICKET_DEVICE_PUBLIC_KEY = 2;
    public static final int CBOR_TICKET_SIGNATURE = 3;

    // u-ticket
    public static final int CBOR_U_TICKET_OWNER_SIGNATURE = 3;
    public static final int CBOR_U_TICKET_HOLDER_PUBLIC_KEY = 4;
    public static final int CBOR_U_TICKET_TICKET_TYPE = 5;
    public static final int CBOR_U_TICKET_COMMAND = 6;
    public static final int CBOR_U_TICKET_VALID_CONDITION = 7;

    // r-ticket
    public static final int CBOR_R_TICKET_DEVICE_SIGNATURE = 3;
    public static final int CBOR_R_TICKET_HASH = 4;
    public static final int CBOR_R_TICKET_HASH_2 = 5;

    // session
    public static final int CBOR_SESSION_PROTOCOL_VERSION = 0;
    public static final int CBOR_SESSION_SESSION_NUMBER = 1;
    public static final int CBOR_SESSION_PROVE_HOLDER_NONCE = 2;
    public static final int CBOR_SESSION_COMMAND = 3;
    public static final int CBOR_SESSION_PROVE_DEVICE_NONCE = 4;
    public static final int CBOR_SESSION_SIGNATURE = 5;

    public static final int CBOR_REPLY_SESSION_PROTOCOL_VERSION = 0;
    public static final int CBOR_REPLY_SESSION_SESSION_NUMBER = 1;
    public static final int CBOR_REPLY_SESSION_PROVE_HOLDER_NONCE = 2;
    public static final int CBOR_REPLY_SESSION_PROVE_DEVICE_NONCE = 3;
    public static final int CBOR_REPLY_SESSION_SIGNATURE = 4;

    // message package
    public static final int HELLO_HOLDER = 0;
    public static final int APPLY_TICKET = 0;
    public static final int HELLO_DEVICE = 1;
    public static final int PROVE_HOLDER = 2;
    public static final int PROVE_DEVICE = 3;
    public static final int SESSION_PACK = 4;
    public static final int REPLY_SESSION_PACK = 5;
    public static final int PENDING = 6;
    public static final int DONE = 254;
    public static final int ERROR = 255;

    // device hello
    public static final int DEVICE_HELLO_NONCE = Const.FIRST_KEY;

    // prove holder
    public static final int PROVE_HOLDER_NONCE = Const.FIRST_KEY;
    public static final int PROVE_DEVICE_NONCE_FIRST = Const.FIRST_KEY;
    public static final int PROVE_DEVICE_NONCE = Const.SECOND_KEY;
    public static final int PROVE_HOLDER_NONCE_SECOND = Const.SECOND_KEY;
    public static final int PROVE_HOLDER_KEY_EXCHANGE_A = Const.THIRD_KEY;
    public static final int PROVE_HOLDER_KEY_EXCHANGE_B = Const.THIRD_KEY;

    // prove device
    public static final int R_TICKET_IN_PROVE_DEVICE = Const.SECOND_KEY;
}
