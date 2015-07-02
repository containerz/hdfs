package org.apache.mesos.hdfs.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mesos.hdfs.config.ConfigServer;

/**
 * Handlers special case exceptions for the scheduler (which is on another thread).
 */
public class SchedulerExceptionHandler implements Thread.UncaughtExceptionHandler {
  private final Log log = LogFactory.getLog(ConfigServer.class);

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    log.error("scheduler exiting due to uncaught exception", e);
    System.exit(2);
  }
}
