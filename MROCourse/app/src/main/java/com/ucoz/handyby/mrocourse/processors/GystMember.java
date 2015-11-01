package com.ucoz.handyby.mrocourse.processors;

import java.io.Serializable;

/**
 * Created by Handy on 01.11.2015.
 */
public class GystMember implements Serializable {
    public int grayValue;
    public int count;

    public GystMember(int grayValue) {
        this.grayValue = grayValue;
        this.count = 0;
    }

    public void add() {
        count++;
    }
}