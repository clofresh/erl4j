package com.syntacticbayleaves.erl4j;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;


public class Erl4jRequest {
    private OtpErlangTuple raw_request;
    
    public Erl4jRequest(OtpErlangTuple raw_request) {
        this.raw_request = raw_request;
    }
    
    public OtpErlangTuple getSender() {
        return (OtpErlangTuple) this.raw_request.elementAt(1);
    }
    
    public OtpErlangPid getPid() {
        return (OtpErlangPid) this.getSender().elementAt(0);
    }
    
    public OtpErlangObject getRef() {
        return this.getSender().elementAt(1);
    }
    
    public OtpErlangTuple makeResponse(OtpErlangObject raw_response) {
        return new OtpErlangTuple(new OtpErlangObject[] {this.getRef(), raw_response});
    }
}