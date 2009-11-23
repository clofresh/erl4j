package com.syntacticbayleaves.erl4j;

import java.lang.Thread;
import java.lang.ClassLoader;
import java.util.ArrayList;
import java.security.SecureClassLoader;

// Erlang's jinterface
import com.ericsson.otp.erlang.OtpSelf;
import com.ericsson.otp.erlang.OtpPeer;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpConnection;

// Apache's Daemon
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

// Exceptions
import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpErlangExit;

import com.syntacticbayleaves.erl4j.Erl4jDispatcher;
import com.syntacticbayleaves.erl4j.Erl4jRunnable;

public class Erl4j implements Daemon {
    private Class dispatcherClass;
    private ArrayList runnables, threads;
    private OtpSelf me;
    private int timeout;
    
    public Erl4j() {}
    
    public void init(DaemonContext context) {
        this.timeout = 2000;
        this.runnables = new ArrayList();
        this.threads = new ArrayList();

        try {
            ClassLoader loader = Erl4j.class.getClassLoader();
            this.dispatcherClass = loader.loadClass("com.syntacticbayleaves.erl4j.Erl4jDumbDispatcher");
        } catch (java.lang.ClassNotFoundException e) {
            
        }
        
        try {
            this.me = new OtpSelf("jruby_node@ccabanilla-mac");
            this.me.publishPort();
        } catch (java.io.IOException e) {
            // log me
        }
    }
    
    public void start() {
        OtpConnection connection;
        Erl4jRunnable runnable;
        Thread thread;
        Erl4jDispatcher dispatcher;
        
        while(this.me != null) {
            try {
                dispatcher = (Erl4jDispatcher) this.dispatcherClass.newInstance();
                connection = this.me.accept();
                runnable = new Erl4jRunnable(connection, dispatcher, this.timeout);
                thread = new Thread(runnable);
                thread.start();
                this.runnables.add(runnable);
                this.threads.add(thread);
            } catch (java.lang.IllegalAccessException e) {
                // log me
            } catch (java.lang.InstantiationException e) {
                // log me
            } catch (java.io.IOException e) {
                // log me
            } catch (OtpAuthException e) {
                // log me
            }
            
        }
        
    }
    
    public void stop() {
        for(Object runnable:this.runnables) {
            ((Erl4jRunnable) runnable).stop();
        }

        for(Object thread:this.threads) {
            try {
                ((Thread) thread).join(10000);
            } catch (java.lang.InterruptedException e) {
                
            }
        }
    }
    
    public void destroy() {
        this.me.unPublishPort();
        this.me = null;
    }
    
}

