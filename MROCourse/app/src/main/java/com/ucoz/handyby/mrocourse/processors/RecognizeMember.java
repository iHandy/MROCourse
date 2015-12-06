package com.ucoz.handyby.mrocourse.processors;

import java.util.ArrayList;

/**
 * Created by Handy on 29.11.2015.
 */
public class RecognizeMember {
    public int neighbor5Count;
    public int neighbor4Count;
    public int neighbor3Count;
    public int endsCount;

    public int neighbor5Count2;
    public int neighbor4Count2;
    public int neighbor3Count2;
    public int endsCount2;

    public ArrayList<Integer> A4;
    public ArrayList<Integer> A8;
    public ArrayList<Integer> Cn;

    public int centerBlack;
    public int center1Black;
    public int center2Black;

    public double R;

    private PartImageMember mPretendent;

    public RecognizeMember(PartImageMember pretendent) {
        neighbor5Count = 0;
        neighbor4Count = 0;
        neighbor3Count = 0;
        endsCount = 0;
        neighbor5Count2 = 0;
        neighbor4Count2 = 0;
        neighbor3Count2 = 0;
        endsCount2 = 0;
        A4 = new ArrayList<>();
        A8 = new ArrayList<>();
        Cn = new ArrayList<>();
        mPretendent = pretendent;
        centerBlack = 0;
        center1Black = 0;
        center2Black = 0;
    }

    public PartImageMember getPretendent() {
        return mPretendent;
    }

    public double equalsR(RecognizeMember o) {
        return Math.sqrt(Math.pow(this.endsCount - o.endsCount, 2)
                + Math.pow(this.endsCount2 - o.endsCount2, 2));
    }
}
