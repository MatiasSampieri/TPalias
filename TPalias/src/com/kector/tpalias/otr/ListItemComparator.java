package com.kector.tpalias.otr;

import java.util.Comparator;

public class ListItemComparator implements Comparator<ListItem> {
    @Override
    public int compare(ListItem i1, ListItem i2) {
        return Integer.compare(i2.count, i1.count);
    }
}
