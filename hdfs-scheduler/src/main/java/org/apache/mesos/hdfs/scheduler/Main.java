package org.apache.mesos.hdfs.scheduler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.mesos.hdfs.config.ConfigServer;

/**
 * Main entry point for the Scheduler.
 */
public final class Main {

  private SchedulerExceptionHandler schedulerExceptionHandler = new SchedulerExceptionHandler();

  // utility class
  private Main() {
  }

  public static void main(String[] args)  {
    new Main().start();
  }

  private void start() {
    Injector injector = Guice.createInjector();
    getSchedulerThread(injector).start();

    injector.getInstance(ConfigServer.class);
  }

  private Thread getSchedulerThread(Injector injector) {
    Thread scheduler = new Thread(injector.getInstance(Scheduler.class));
    scheduler.setName("HdfsScheduler");

    scheduler.setUncaughtExceptionHandler(schedulerExceptionHandler);
    return scheduler;
  }
}