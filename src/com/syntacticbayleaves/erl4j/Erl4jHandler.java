package com.syntacticbayleaves.erl4j;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.syntacticbayleaves.erl4j.Erl4jRequest;

public interface Erl4jHandler {
    public OtpErlangObject respond(Erl4jRequest request);
}