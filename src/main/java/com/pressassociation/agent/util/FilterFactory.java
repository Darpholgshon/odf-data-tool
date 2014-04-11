package com.pressassociation.agent.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author <a href="ralph.hodgson@pressassociation.com">Ralph Hodgson</a>
 * @since 10/04/2014 14:56
 */
public class FilterFactory
{
    public static FileFilter matches(final String regex) {
        return FilterFactory.matches(regex, false);
    }

    public static FileFilter matches(final String regex, final boolean includeDirectories)
    {
        return new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory() ? includeDirectories : f.getName().matches(regex);
            }
        };
    }

    public static FileFilter ignores(final String regex) {
        return FilterFactory.ignores(regex, false);
    }

    public static FileFilter ignores(final String regex, final boolean includeDirectories)
    {
        return new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory() ? includeDirectories : !f.getName().matches(regex);
            }
        };
    }

    public static FileFilter directories()
    {
        return new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory();
            }
        };
    }
}