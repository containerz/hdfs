package org.apache.mesos.hdfs.executor;

/**
 * A invalid condition exist within the executor.
 */
public class ExecutorException extends RuntimeException {
  public ExecutorException() {
  }

  public ExecutorException(String message) {
    super(message);
  }
}
