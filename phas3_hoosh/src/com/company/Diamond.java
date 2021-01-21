package com.company;

public class Diamond {

    public static final char GREEN  = '0';
    public static final char BLUE   = '1';
    public static final char RED    = '2';
    public static final char YELLOW = '3';
    public static final char GRAY   = '4';

    int id;
    public char sid;
    public int value;
    int row;
    int column;


    public Diamond()
    {
    }

    public Diamond(int id)
    {
        this.id  = id;
    }

    public Diamond(Diamond diamond)
    {
        this.id = diamond.id;
        this.sid = diamond.sid;
        this.row = diamond.row;
        this.column = diamond.column;
        this.value = diamond.value;
    }

    public Diamond(int id, char sid , int row , int column)
    {
        this.id = id;
        this.sid = sid;
        this.row = row;
        this.column = column;
    }

    public void generate_value()
    {
        switch(this.sid)
        {
            case GREEN:  // green
                value =2;
                break;
            case BLUE:  //blue
                value =5;
                break;
            case RED:  //red
                value =3;
                break;
            case YELLOW:  //yellow
                value =1;
                break;
            case GRAY:  //gray
                value =10;
                break;
        }
    }

}
