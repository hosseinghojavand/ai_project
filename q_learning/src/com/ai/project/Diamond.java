package com.ai.project;

public class Diamond {
    int id;
    public char sid;
    public int value;
    int row;
    int column;

    public Diamond()
    {

    }

    public Diamond(Diamond diamond)
    {
        this.id = diamond.id;
        this.sid = diamond.sid;
        this.row = diamond.row;
        this.column = diamond.column;
        this.value = diamond.value;
    }

    public Diamond( int id, char sid , int row , int column)
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
            case '0':  // green
                value =2;
                break;
            case '1':  //blue
                value =5;
                break;
            case '2':  //red
                value =3;
                break;
            case '3':  //yellow
                value =1;
                break;
            case '4':  //gray
                value =10;
                break;
        }
    }

}
