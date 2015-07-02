package com.kaushiksamba.contacts;

public class EachContact
{
    int id;
    String name;
    String img_url;

    public EachContact()
    {

    }
    public EachContact(int id, String name, String img_url)
    {
        this.id = id;
        this.name = name;
        this.img_url = img_url;
    }

    public EachContact(String name, String img_url)
    {
        this.name = name;
        this.img_url = img_url;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImg_url()
    {
        return this.img_url;
    }

    public void setImg_url(String img_url)
    {
        this.img_url = img_url;
    }
}
