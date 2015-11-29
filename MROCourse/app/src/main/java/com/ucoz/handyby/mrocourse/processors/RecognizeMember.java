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
        mPretendent = pretendent;
    }

    public PartImageMember getPretendent() {
        return mPretendent;
    }

    public double equalsR1(RecognizeMember o) {
        return Math.sqrt(Math.pow(this.endsCount - o.endsCount, 2)
                + Math.pow(this.neighbor5Count - o.neighbor5Count, 2)
                + Math.pow(this.neighbor4Count - o.neighbor4Count, 2)
                + Math.pow(this.neighbor3Count - o.neighbor3Count, 2));
    }

    public double equalsR2(RecognizeMember o) {
        return Math.sqrt(Math.pow(this.endsCount2 - o.endsCount2, 2)
                + Math.pow(this.neighbor5Count2 - o.neighbor5Count2, 2)
                + Math.pow(this.neighbor4Count2 - o.neighbor4Count2, 2)
                + Math.pow(this.neighbor3Count2 - o.neighbor3Count2, 2));
    }
}
