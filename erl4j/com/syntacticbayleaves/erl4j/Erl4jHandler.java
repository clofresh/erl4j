package com.syntacticbayleaves.erl4j;

import com.ericsson.otp.erlang.OtpErlangObject;

public interface Erl4jHandler {
    public OtpErlangObject respond(Erl4jRequest request);
}