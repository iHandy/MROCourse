package com.ucoz.handyby.mrocourse.processors;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Handy on 15.11.2015.
 */
public class SpacesHolder implements Serializable {

    private int minValue;
    private int minPos;
    private ArrayList<GystMember> gystMembers;

    public SpacesHolder(ArrayList<GystMember> gystMembers) {
        this.gystMembers = new ArrayList<>(gystMembers);
        initialize();
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMinPos() {
        return minPos;
    }

    public boolean isInWordSpace(int countOfPixels) {
        for (int i = 0; i <= minPos; i++) {
            GystMember member = gystMembers.get(i);
            if (member.grayValue == countOfPixels) {
                return true;
            }
        }
        return false;
    }

    private void initialize() {
        int max1 = Integer.MIN_VALUE, max2 = Integer.MIN_VALUE;
        int iter = -1, max1pos = 0, max2pos = 0;
        boolean firstFound = false;
        for (GystMember member : gystMembers) {
            iter++;

            if (!firstFound) {
                if (member.count > max1) {
                    max1 = member.count;
                } else {
                    max1pos = iter - 1;
                    firstFound = true;
                }
            } else {
                if (member.count > max2) {
                    max2 = member.count;
                } else {
                    max2pos = iter - 1;
                    break;
                }
            }
        }

        int min = Integer.MAX_VALUE, minpos = 0;
        for (int i = max1pos; i < max2pos; i++) {
            GystMember member = gystMembers.get(i);
            if (member.count < min) {
                min = member.count;
                minpos = i;
            }
        }

        minValue = min;
        minPos = minpos;
    }
}
