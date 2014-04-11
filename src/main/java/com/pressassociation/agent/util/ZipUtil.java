package com.pressassociation.agent.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="ralph.hodgson@pressassociation.com">Ralph Hodgson</a>
 * @since 10/04/2014 15:18
 */
public class ZipUtil
{
    protected static final Logger LOG = LoggerFactory.getLogger(ZipUtil.class);
    public static final String ZIP_EXT = ".zip";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void unzip(File input, File targetDirectory)
            throws IOException
    {
        ZipFile zip = new ZipFile(input);

        // Check target folder exists or create it.
        if (!targetDirectory.exists())
        {
            targetDirectory.mkdirs();
        }

        LOG.debug("Extracting files...");
        for (Enumeration<?> entries = zip.entries(); entries.hasMoreElements(); )
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String path = targetDirectory + File.separator + entry.getName();

            if (entry.isDirectory())
            {
                // Assume directories are stored parents first then children.
                LOG.debug("Extracting directory: " + path);
                FileUtils.forceMkdir(new File(targetDirectory, entry.getName()));
            }
            else
            {
                LOG.debug("Extracting file: " + path);
                FileUtils.copyInputStreamToFile(zip.getInputStream(entry), new File(path));
            }
        }
        LOG.debug("Extraction complete.");
    }

    public static final void zip(File directory)
            throws IOException
    {
        if (hasFiles(directory))
        {
            LOG.debug("Zip directory: " + directory.getName());
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(directory.getAbsolutePath() + ZIP_EXT));
            zipChild(directory.getName() + "/", zos, directory);
            zos.close();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static boolean hasFiles(File directory)
    {
        for (File file : directory.listFiles())
        {
            if (!file.isDirectory() || hasFiles(file))
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private static void zipChild(String path, ZipOutputStream zos, File directory)
            throws IOException
    {
        for (File file : directory.listFiles())
        {
            // if the file is directory, use recursion
            if (file.isDirectory())
            {
                LOG.debug("Adding directory: " + directory.getName());
                String child = path + file.getName() + "/";
                zos.putNextEntry(new ZipEntry(child));
                zipChild(child, zos, file);
                continue;
            }

            LOG.debug("Adding file: " + file.getName());

            FileInputStream fis = new FileInputStream(file);
            zos.putNextEntry(new ZipEntry(path + file.getName()));

            // Create variables for buffering.
            int length;
            byte[] buffer = new byte[1024];

            // Read the files in 10Kb at a time.
            while ((length = fis.read(buffer)) > 0)
            {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }
}