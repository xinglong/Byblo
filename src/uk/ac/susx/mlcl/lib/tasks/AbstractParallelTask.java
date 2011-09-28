/*
 * Copyright (c) 2010-2011, University of Sussex
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 *  * Neither the name of the University of Sussex nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.ac.susx.mlcl.lib.tasks;

import com.beust.jcommander.Parameter;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hiam20
 */
public abstract class AbstractParallelTask extends AbstractTask {

    private static final Logger LOG = Logger.getLogger(
            AbstractParallelTask.class.getName());

    protected static final int DEFAULT_NUM_THREADS =
            Runtime.getRuntime().availableProcessors();

    @Parameter(names = {"-t", "--threads"},
               description= "Number of threads to use.")
    private int nThreads = DEFAULT_NUM_THREADS;

    private ExecutorService executor = null;

    private Queue<Future<? extends Task>> futureQueue;

    public AbstractParallelTask() {
    }

    public void setNumThreads(int nThreads) {
        if (nThreads < 1)
            throw new IllegalArgumentException("nThreads < 1");
        if (nThreads > Runtime.getRuntime().availableProcessors())
            LOG.log(Level.WARNING, "nThreads ({0}) > availableProcessors ({0})",
                    new Object[]{nThreads, Runtime.getRuntime().
                        availableProcessors()});
        if (nThreads != this.nThreads) {
            this.nThreads = nThreads;
        }
    }

    public final int getNumThreads() {
        return nThreads;
    }

    protected synchronized final ExecutorService getExecutor() {
        if (executor == null) {
            LOG.log(Level.INFO,
                    "Initialising executor with {0} concurrent work threads.",
                    nThreads);
            // Create a new thread pool using an unbounded queue - throttling will
            // be handled by a semaphore
            final ThreadPoolExecutor tpe = new ThreadPoolExecutor(
                    nThreads, nThreads,
                    1L, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>());

            tpe.setRejectedExecutionHandler(
                    new ThreadPoolExecutor.AbortPolicy());

            this.executor = Executors.unconfigurableExecutorService(tpe);
        }
        return executor;
    }

    @Override
    protected void initialiseTask() throws Exception {
        getExecutor();
    }

    @Override
    protected void finaliseTask() throws Exception {
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
            if (!executor.isTerminated()) {
                List<Runnable> runnables = executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
            catchException(ex);
        } finally {
            executor.shutdownNow();
        }
    }

    @Override
    public void run() {
        try {
            LOG.log(Level.FINE, "Initialising task {0}. ({1})",
                    new Object[]{this, Thread.currentThread().getName()});

            initialiseTask();

            LOG.log(Level.FINE, "Running task {0}. ({1})",
                    new Object[]{this, Thread.currentThread().getName()});

            runTask();

        } catch (Exception ex) {
            catchException(ex);
        } catch (Throwable t) {
            catchException(new RuntimeException(t));
        } finally {
            try {
                finaliseTask();
            } catch (Exception ex) {
                catchException(ex);
            }
            LOG.log(Level.FINE, "Completed task {0}. ({1})",
                    new Object[]{this, Thread.currentThread().getName()});
        }
    }

    protected synchronized final Queue<Future<? extends Task>> getFutureQueue() {
        if (futureQueue == null)
            futureQueue = new ArrayDeque<Future<? extends Task>>();
        return futureQueue;
    }

    protected <T extends Task> Future<T> submitTask(T task) {
        if (task == null)
            throw new NullPointerException("task is null");
        Future<T> future = getExecutor().submit(task, task);
        getFutureQueue().offer(future);
        return future;
    }
}
