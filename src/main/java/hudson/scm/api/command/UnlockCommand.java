/*******************************************************************************
 * Contributors:
 *     PTC 2016
 *******************************************************************************/


package hudson.scm.api.command;

import com.mks.api.Command;

import hudson.scm.IntegrityConfigurable;
import hudson.scm.api.option.APIOption;
import hudson.scm.api.option.IAPIOption;

/**
 *
 * @author Author: asen
 * @version $Revision: $
 */
public class UnlockCommand extends BasicAPICommand
{
    protected UnlockCommand(final IntegrityConfigurable serverConfig)
    {
	super(serverConfig);
	cmd = new Command(Command.SI, UNLOCK_COMMAND);
	
	// Initialize defaults
	cmd.addOption(new APIOption(IAPIOption.ACTION, IAPIOption.REMOVE));
	cmd.addOption(new APIOption(IAPIOption.RECURSE));
	cmd.addOption(new APIOption(IAPIOption.YES));
    }
}
