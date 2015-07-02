package org.apache.mesos.hdfs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import org.apache.mesos.hdfs.scheduler.HdfsScheduler;
import org.apache.mesos.hdfs.config.HdfsFrameworkConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

public class DnsResolver {
  private final Log log = LogFactory.getLog(HdfsScheduler.class);

  static final int NN_TIMER_PERIOD = 10000;

  private final HdfsScheduler scheduler;
  private final HdfsFrameworkConfig conf;

  public DnsResolver(HdfsScheduler scheduler, HdfsFrameworkConfig conf) {
    this.scheduler = scheduler;
    this.conf = conf;
  }

  public boolean journalNodesResolvable() {
    if (!conf.usingMesosDns()) { return true; } //short circuit since Mesos handles this otherwise
    Set<String> hosts = new HashSet<>();
    for (int i = 1; i <= conf.getJournalNodeCount(); i++) {
      hosts.add(HDFSConstants.JOURNAL_NODE_ID + i + "." + conf.getFrameworkName() + "." + conf.getMesosDnsDomain());
    }
    boolean success = true;
    for (String host : hosts) {
      log.info("Resolving DNS for " + host);
      try {
        InetAddress.getByName(host);
        log.info("Successfully found " + host);
      } catch (SecurityException | IOException e) {
        log.warn("Couldn't resolve host " + host);
        success = false;
        break;
      }
    }
    return success;
  }

  public boolean nameNodesResolvable() {
    if (!conf.usingMesosDns()) { return true; } //short circuit since Mesos handles this otherwise
    Set<String> hosts = new HashSet<>();
    for (int i = 1; i <= HDFSConstants.TOTAL_NAME_NODES; i++) {
      hosts.add(HDFSConstants.NAME_NODE_ID + i + "." + conf.getFrameworkName() + "." + conf.getMesosDnsDomain());
    }
    boolean success = true;
    for (String host : hosts) {
      log.info("Resolving DNS for " + host);
      try {
        InetAddress.getByName(host);
        log.info("Successfully found " + host);
      } catch (SecurityException | IOException e) {
        log.warn("Couldn't resolve host " + host);
        success = false;
        break;
      }
    }
    return success;
  }

  public void sendMessageAfterNNResolvable(final SchedulerDriver driver,
      final Protos.TaskID taskId, final Protos.SlaveID slaveID, final String message) {
    if (!conf.usingMesosDns()) {
      // short circuit since Mesos handles this otherwise
      scheduler.sendMessageTo(driver, taskId, slaveID, message);
      return;
    }
    FutureMessage futureMessage = new FutureMessage(driver, scheduler, taskId, slaveID, message);
    Timer timer = new Timer();
    PreNNInitTask task = new PreNNInitTask(futureMessage, this);
    timer.scheduleAtFixedRate(task, 0, NN_TIMER_PERIOD);
  }
}
