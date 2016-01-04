package hudson.scm;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.scm.IntegritySCM.DescriptorImpl;
import hudson.scm.api.session.ISessionPool;

/**
 * This class implements the onDeleted event when a build is deleted The sole purpose is to ensure
 * that the Integrity SCM cache tables are in line with the number of builds registered with
 * Jenkins.
 */

@Extension
public class IntegrityRunListenerImpl<R extends Run<?, ?>> extends RunListener<R>
{
  private static final Logger LOGGER = Logger.getLogger(IntegritySCM.class.getSimpleName());

  @DataBoundConstructor
  public IntegrityRunListenerImpl()
  {}

  @Override
  public void onDeleted(R run)
  {
    LOGGER.fine("RunListenerImpl.onDeleted() invoked");
    super.onDeleted(run);

    // Perform some clean up on old cache tables
    Job<?, ?> job = run.getParent();
    try
    {
      DerbyUtils.cleanupProjectCache(DescriptorImpl.INTEGRITY_DESCRIPTOR.getDataSource(),
          job.getName(), run.getNumber());
    } catch (SQLException sqlex)
    {
      LOGGER.severe("SQL Exception caught...");
      LOGGER.log(Level.SEVERE, "SQLException", sqlex);
    }

    LOGGER.fine("RunListenerImpl.onDeleted() execution complete!");
  }

  /*
   * Clear the session pool of APISession objects post the build run
   * 
   * (non-Javadoc)
   * 
   * @see hudson.model.listeners.RunListener#onCompleted(hudson.model.Run,
   * hudson.model.TaskListener)
   */
  @Override
  public void onCompleted(R r, TaskListener listener)
  {
    // TODO Auto-generated method stub
    super.onCompleted(r, listener);

    try
    {
      int activeSessions = ISessionPool.getInstance().getPool().getNumActive();
      // Empty the pool if there are no active sessions
      if (activeSessions == 0)
      {
        LOGGER.log(Level.FINEST, "Clearing Integrity Session Pool");
        ISessionPool.getInstance().getPool().clear();
      }
    } catch (UnsupportedOperationException e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } catch (Exception e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    };
  }

}
