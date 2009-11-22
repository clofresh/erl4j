package com.syntacticbayleaves.erl4j;

import com.ericsson.otp.erlang.OtpSelf;
import com.ericsson.otp.erlang.OtpPeer;
import com.ericsson.otp.erlang.OtpConnection;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

// Exceptions
import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpErlangExit;

import com.syntacticbayleaves.erl4j.Erl4jRequest;

public class Erl4jConnection {
    private OtpSelf me;
    private OtpPeer other;
    private OtpConnection connection;

    public Erl4jConnection(OtpSelf me, OtpPeer other) {
        this.me = me;
        this.other = other;
        this.connection = null;
    }
    
    public void connect() {
        if (this.connection == null) {
            try {
                this.connection = this.me.connect(this.other);
            } catch (java.io.IOException e) {
            } catch (OtpAuthException e) {
            }
        }
    }
    
    public Erl4jRequest receive() {
        if (this.connection == null) {
            this.connect();
        }
        
        OtpErlangTuple request = null;
        try {
            request = (OtpErlangTuple)this.connection.receive();
        
        } catch (java.io.IOException e) {
            
        } catch (OtpAuthException e) {
            
        } catch (OtpErlangExit e) {
            
        }

        return new Erl4jRequest(request);
    }
    
    public void reply(Erl4jRequest request, OtpErlangObject raw_response) {        
        if (this.connection == null) {
            this.connect();
        }

        try {
            this.connection.send(request.getPid(), request.makeResponse(raw_response));
         } catch (java.io.IOException e) {
         }
   }
}
