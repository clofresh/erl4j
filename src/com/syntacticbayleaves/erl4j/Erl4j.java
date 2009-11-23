package com.syntacticbayleaves.erl4j;

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
import java.io.IOException;
import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpErlangExit;

import com.syntacticbayleaves.erl4j.Erl4jHandler;
import com.syntacticbayleaves.erl4j.Erl4jRunnable;

public class Erl4j implements Daemon {
    private Class dispatcherClass;
    private ArrayList runnables, threads;
    private OtpSelf me;
    private int timeout;
    
    public Erl4j() {}
    
    public void init(DaemonContext context) throws IOException, ClassNotFoundException {
        String[] args = context.getArguments();

        this.timeout = Integer.parseInt(args[2]);
        this.runnables = new ArrayList();
        this.threads = new ArrayList();

        ClassLoader loader = Erl4j.class.getClassLoader();
        this.dispatcherClass = loader.loadClass(args[1]);
        
        this.me = new OtpSelf(args[0]);
        this.me.publishPort();
    }
    
    public void start() {
        OtpConnection connection;
        Erl4jRunnable runnable;
        Thread thread;
        Erl4jHandler dispatcher;
        
        try {
            while(this.me != null) {
                try {
                    dispatcher = (Erl4jHandler) this.dispatcherClass.newInstance();
                    connection = this.me.accept();
                    runnable = new Erl4jRunnable(connection, dispatcher, this.timeout);
                    thread = new Thread(runnable);
                    thread.start();
                    this.runnables.add(runnable);
                    this.threads.add(thread);
                } catch (IOException e) {
                    // Remote node tried to connect but couldn't find a common protocol
                } catch (OtpAuthException e) {
                    // Unauthorized connection 
                }
            
            }
        } catch (IllegalAccessException e) {
            // Something wrong with the dispatcherClass
        } catch (InstantiationException e) {
            // Something wrong with the dispatcherClass
        }
    }
    
    public void stop() {
        for(Object runnable:this.runnables) {
            ((Erl4jRunnable) runnable).stop();
        }

        for(Object thread:this.threads) {
            try {
                ((Thread) thread).join(10000);
            } catch (InterruptedException e) {
                
            }
        }
    }
    
    public void destroy() {
        this.me.unPublishPort();
        this.me = null;
    }
    
}

