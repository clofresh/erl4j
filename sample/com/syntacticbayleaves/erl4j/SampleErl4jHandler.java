package com.syntacticbayleaves.erl4j;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;

import com.syntacticbayleaves.erl4j.Erl4jRequest;

public class SampleErl4jHandler implements Erl4jHandler {
    public OtpErlangObject respond(Erl4jRequest request) {
        return new OtpErlangLong(99);
    }
}