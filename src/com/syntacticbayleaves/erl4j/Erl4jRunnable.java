package com.syntacticbayleaves.erl4j;

import java.lang.Runnable;

// Erlang's jinterface
import com.ericsson.otp.erlang.OtpConnection;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;

import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpAuthException;

import com.syntacticbayleaves.erl4j.Erl4jRequest;
import com.syntacticbayleaves.erl4j.Erl4jDispatcher;

public class Erl4jRunnable implements Runnable {
    private OtpConnection connection;
    private Erl4jDispatcher dispatcher;
    private int timeout;
    
    public Erl4jRunnable(OtpConnection connection, Erl4jDispatcher dispatcher, int timeout) {
        this.connection = connection;
        this.timeout = timeout;
        this.dispatcher = dispatcher;
    }
    
    public void run() {
        Erl4jRequest request;
        OtpErlangTuple response;
        
        try {
            while(this.connection != null) {
                try {
                    request = new Erl4jRequest((OtpErlangTuple) this.connection.receive(this.timeout));
                    response = request.makeResponse(this.respond(request));

                    this.connection.send(request.getPid(), response);
                } catch (OtpAuthException e) {
                    // log me
                } catch (java.lang.InterruptedException e) {
                    // log me
                }
            }
        } catch (java.io.IOException e) {
            // log me
        } catch (OtpErlangExit e) {
            // log me
        }
    }

    public OtpErlangObject respond(Erl4jRequest request) {
        return this.dispatcher.respond(request);
    }
    
    public void stop() {
        this.connection = null;
    }
}

