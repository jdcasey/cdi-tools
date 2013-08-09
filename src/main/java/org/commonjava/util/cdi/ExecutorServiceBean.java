package org.commonjava.util.cdi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorServiceBean
    extends NamedInstanceBean<ExecutorService>
{

    public ExecutorServiceBean( final int threads, final boolean daemon, final int priority, final String named )
    {
        super( Executors.newFixedThreadPool( threads, new ThreadFactory()
        {
            private int idx = 0;

            @Override
            public Thread newThread( final Runnable r )
            {
                final Thread t = new Thread( r, named + ( idx++ ) );
                t.setDaemon( daemon );
                t.setPriority( priority );

                return t;
            }
        } ), ExecutorService.class, named );
    }

}
