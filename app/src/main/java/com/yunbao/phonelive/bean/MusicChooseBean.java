package com.yunbao.phonelive.bean;

public class MusicChooseBean
{
    private String artist;
    private long duration;
    private String durationString;
    private String name;
    private String path;
    private String title;

    public MusicChooseBean()
    {
    }

    public MusicChooseBean(String path,  String title, String name, String artist, long duration) {
        this.artist = artist;
        this.duration = duration;
        this.name = name;
        this.path = path;
        this.title = title;
    }

    public static String castDurationString(long paramLong)
    {
        int i = (int)(paramLong / 3600000L);
        int j = (int)(paramLong % 3600000L / 60000L);
        int k = (int)(paramLong % 60000L / 1000L);
        String durationStr = "";
        if (i > 0)
            if (i < 10)
            {
                StringBuilder localObject = new StringBuilder();
                localObject.append("");
                localObject.append("0");
                localObject.append(i);
                localObject.append(":");
                durationStr = localObject.toString();
            }
            else
            {
                StringBuilder localObject = new StringBuilder();
                localObject.append("");
                localObject.append(i);
                localObject.append(":");
                durationStr = localObject.toString();
            }
        if (j > 0)
        {
            if (j < 10)
            {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(durationStr);
                localStringBuilder.append("0");
                localStringBuilder.append(j);
                localStringBuilder.append(":");
                durationStr = localStringBuilder.toString();
            }
            else
            {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(durationStr);
                localStringBuilder.append(j);
                localStringBuilder.append(":");
                durationStr = localStringBuilder.toString();
            }
        }
        else
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(durationStr);
            localStringBuilder.append("00:");
            durationStr = localStringBuilder.toString();
        }
        if (k > 0)
        {
            if (k < 10)
            {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(durationStr);
                localStringBuilder.append("0");
                localStringBuilder.append(k);
                return localStringBuilder.toString();
            }
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(durationStr);
            localStringBuilder.append(k);
            return localStringBuilder.toString();
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(durationStr);
        localStringBuilder.append("00");
        return localStringBuilder.toString();
    }

    public String getArtist()
    {
        return this.artist;
    }

    public long getDuration()
    {
        return this.duration;
    }

    public String getDurationString()
    {
        return this.durationString;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPath()
    {
        return this.path;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setArtist(String paramString)
    {
        this.artist = paramString;
    }

    public void setDuration(long paramLong)
    {
        this.duration = paramLong;
    }

    public void setDurationString(String paramString)
    {
        this.durationString = paramString;
    }

    public void setName(String paramString)
    {
        this.name = paramString;
    }

    public void setPath(String paramString)
    {
        this.path = paramString;
    }

    public void setTitle(String paramString)
    {
        this.title = paramString;
    }
}
