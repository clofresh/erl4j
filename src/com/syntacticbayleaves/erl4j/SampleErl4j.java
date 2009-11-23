package com.syntacticbayleaves.erl4j;

import com.syntacticbayleaves.erl4j.Erl4j;
import com.syntacticbayleaves.erl4j.Erl4jRequest;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;

public class SampleErl4j extends Erl4j {
    public OtpErlangObject respond(Erl4jRequest request) {
        return new OtpErlangLong(42);
    }
}

