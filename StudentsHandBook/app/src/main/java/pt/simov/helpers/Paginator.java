package pt.simov.helpers;

import java.util.LinkedList;

/**
 * Created by João on 16/11/2016.
 */

public class Paginator {
    private int totalItems, itemsPerPage = 15;
    private int itemsRemaining = totalItems % itemsPerPage;
    private int lastPage = totalItems/itemsPerPage;

    public LinkedList<Object> generatePage(int currentPage, LinkedList<Object> channels) {
        int startItem = currentPage*itemsPerPage;
        int numOfData = itemsPerPage;

        LinkedList<Object> pageData = new LinkedList<>();

        if(currentPage == lastPage && itemsRemaining > 0) {
            for(int i=startItem; i<startItem+itemsRemaining; i++) {
                pageData.add(channels.get(i));
            }
        } else {
            for(int i=startItem; i<startItem+numOfData; i++) {
                pageData.add(channels.get(i));
            }
        }
        return pageData;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
        itemsRemaining = totalItems % itemsPerPage;
        lastPage = totalItems/itemsPerPage;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getItemsRemaining() {
        return itemsRemaining;
    }

    public void setItemsRemaining(int itemsRemaining) {
        this.itemsRemaining = itemsRemaining;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
