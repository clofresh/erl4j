package com.syntacticbayleaves.erl4j.sample;

import com.ericsson.otp.erlang.OtpErlangObject;

import com.syntacticbayleaves.erl4j.Erl4jHandler;
import com.syntacticbayleaves.erl4j.Erl4jRequest;
import com.syntacticbayleaves.erl4j.sample.SampleErl4jHandler;

public class SampleErl4jDispatcher implements Erl4jHandler {
    public OtpErlangObject respond(Erl4jRequest request) {
        return this.getHandler(request).respond(request);
    }
    
    private Erl4jHandler getHandler(Erl4jRequest request) {
        return new SampleErl4jHandler();
    }
}

