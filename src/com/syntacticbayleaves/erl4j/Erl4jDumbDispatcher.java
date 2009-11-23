package com.syntacticbayleaves.erl4j;

import com.ericsson.otp.erlang.OtpErlangObject;

import com.syntacticbayleaves.erl4j.Erl4jHandler;
import com.syntacticbayleaves.erl4j.Erl4jRequest;

public class Erl4jDumbDispatcher implements Erl4jDispatcher {
    public OtpErlangObject respond(Erl4jRequest request) {
        return this.getHandler(request).respond(request);
    }
    
    private Erl4jHandler getHandler(Erl4jRequest request) {
        return new Erl4jHandler();
    }
}

