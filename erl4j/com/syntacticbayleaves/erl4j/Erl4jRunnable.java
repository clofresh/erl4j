package com.syntacticbayleaves.erl4j;

// Erlang's jinterface
import com.ericsson.otp.erlang.OtpConnection;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;

import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpAuthException;

public class Erl4jRunnable implements Runnable {
    private OtpConnection connection;
    private Erl4jHandler dispatcher;
    private int timeout;
    
    public Erl4jRunnable(OtpConnection connection, Erl4jHandler dispatcher, int timeout) {
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
                    // Bad cookie
                } catch (java.lang.InterruptedException e) {
                    // Timeout
                }
            }
        } catch (java.io.IOException e) {
            // Something went wrong with the connection
        } catch (OtpErlangExit e) {
            // Received an exit signal
        }
    }

    public OtpErlangObject respond(Erl4jRequest request) {
        return this.dispatcher.respond(request);
    }
    
    public void stop() {
        this.connection = null;
    }
}

