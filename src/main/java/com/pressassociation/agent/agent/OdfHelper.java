package com.pressassociation.agent.agent;

import com.pressassociation.agent.util.FilterFactory;
import com.pressassociation.agent.util.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="ralph.hodgson@pressassociation.com">Ralph Hodgson</a>
 * @since 10/04/2014 13:45
 */
public class OdfHelper
{
    protected static final Logger LOG = LoggerFactory.getLogger(OdfHelper.class);

    // If the file name contains one of the following then keep else delete.
    private static final String regex = "^.*?(_GM|_GN|_SCHEDULE|_PARTIC|_RESULT|_START_LIST|_MEDALLISTS|_STANDING|_RECORD|_BIO).*?\\.xml";

    // The folder to recursively search through.
    private static final String folder =
            "C:\\Users\\ralphho\\Documents\\Work Documents\\projects_current\\2014-commonwealth-games\\Test Data\\Multi Sport Test #1";

    private File workingDirectory;

    public OdfHelper withWorkingDirectory(String folder)
    {
        this.workingDirectory = new File(folder);
        return this;
    }

    public OdfHelper unpack() throws IOException, InterruptedException
    {
        for (File file : workingDirectory.listFiles( FilterFactory.matches(".*\\.(7z|zip)$") ) )
        {
            LOG.info("Unpack => " + file.getName());
            ZipUtil.unzip(file, workingDirectory);
        }
        return this;
    }

    public OdfHelper ransack()
    {
        LOG.info("Ransack => " + workingDirectory);
        recursiveRansack(workingDirectory);
        return this;
    }

    public OdfHelper pack() throws IOException
    {
        for (File file : workingDirectory.listFiles( FilterFactory.directories() ) )
        {
            LOG.info("Pack => " + file.getName());
            ZipUtil.zip(file);
        }
        return this;
    }

    public OdfHelper cleanup() throws IOException, InterruptedException
    {
        for (File file : workingDirectory.listFiles( FilterFactory.directories() ) )
        {
            LOG.info("Clean-up => " + file.getName());
            FileUtils.deleteDirectory(file);
        }
        return this;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    private void recursiveRansack(File directory)
    {
        for (File file : directory.listFiles( FilterFactory.ignores(".*\\.(7z|zip)$", true) ) )
        {
            if (file.isDirectory())
            {
                recursiveRansack(file);
            }
            else if (!file.getName().matches(regex))
            {
                LOG.debug("Discarded => " + file.getAbsolutePath().substring(folder.length()));
                file.delete();
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        new OdfHelper()
                .withWorkingDirectory(folder)
                .unpack()
                .ransack()
                .pack()
                .cleanup();
    }
}