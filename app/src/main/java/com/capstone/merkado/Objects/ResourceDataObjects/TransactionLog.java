package com.capstone.merkado.Objects.ResourceDataObjects;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionLog {
    Integer buyer;
    Integer seller;
    Float amount;
    Long timestamp;
    TransactionLog next;

    public TransactionLog() {
    }

    public TransactionLog(Integer buyer, Integer seller, Float amount, Long timestamp, TransactionLog next) {
        this.buyer = buyer;
        this.seller = seller;
        this.amount = amount;
        this.timestamp = timestamp;
        this.next = next;
    }

    public TransactionLog(String transactionString) {
        toTransactionLog(transactionString);
    }

    public Integer getBuyer() {
        return buyer;
    }

    public void setBuyer(Integer buyer) {
        this.buyer = buyer;
    }

    public Integer getSeller() {
        return seller;
    }

    public void setSeller(Integer seller) {
        this.seller = seller;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionLog getNext() {
        return next;
    }

    public void setNext(TransactionLog next) {
        this.next = next;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "buyer:%d,seller:%d,amount:%.2f,timestamp:%d%s",
                buyer, seller, amount, timestamp, next == null ? "" : String.format("->%s", next));
    }

    private TransactionLog toTransactionLog(String str) {
        String objectStr = "";
        String trailingStr = "";
        if (str.contains("->")) {
            String[] strings = str.split("->", 1);
            objectStr = strings[0];
            trailingStr = strings[1];
        } else {
            objectStr = str;
        }
        parseObjectString(objectStr);
        if (!trailingStr.isEmpty()) this.next = toTransactionLog(trailingStr);
        return this;
    }

    private void parseObjectString(String objectString) {
        // objectString = buyer:1,seller:1,amount:10.00,timestamp:123456789
        String[] parts = objectString.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            switch (key) {
                case "buyer":
                    this.buyer = Integer.parseInt(value);
                    break;
                case "seller":
                    this.seller = Integer.parseInt(value);
                    break;
                case "amount":
                    this.amount = Float.parseFloat(value);
                    break;
                case "timestamp":
                    this.timestamp = Long.parseLong(value);
                    break;
            }
        }
    }

    public CalculateGDP calculateGDP(Long beforeTimestamp) {
        return new CalculateGDP(this, beforeTimestamp);
    }

    public static class CalculateGDP {
        static List<Float> addedValues;

        public CalculateGDP(TransactionLog transactionLog, Long beforeTimestamp) {
            addedValues = new ArrayList<>();
            getGDP(transactionLog, beforeTimestamp);
        }

        public Float getGDP() {
            return addedValues.stream().reduce(0.0f, Float::sum);
        }

        public List<Float> getDetailedGDP() {
            return addedValues;
        }

        private static void getGDP(TransactionLog transactionLog, Long beforeTimestamp) {
            TransactionLog tl = transactionLog;
            while (tl.getNext() != null) {
                Float addedValue = getAddedValue(transactionLog.getAmount(), transactionLog.getNext().getAmount());
                addedValues.add(addedValue);
                tl = tl.getNext();
            }
        }

        private static Float getAddedValue(Float input, Float output) {
            return output - input;
        }
    }

    /*
    APPROACH 1:
        each resources comes from the factories.

        then, yung group of resources, mahahati-hati. so parang nodes ang ganap.
        so tree ang data structure...

        each resource will reference their place in this node. this is through the node id.
        id rin siguro ang  magkakabit ng bawat node.

        everytime a resource is BOUGHT, its node reference will change.

        CONS:
            - Too many fetches will happen. di ito sql para mapadali ang ganiyan, json format 'to.
            - Too much data is being saved for every movement!
        PROS:
            - Allows accurate representation for gdp.
     APPROACH 2:
        each resource group has two attributes, bought and sold. bought is retained throughout the
        process, whereas sold changes for every transaction.

        how about merging two groups of the same resources with different bought/sold?
            sub-quantities? like aside dun sa quantity na nakalagay sa resource ng kung magkano
            unang binili yung certain quantity tapos kung magkano huling binili yun??? tapos check
            nalang if same lang ang bought and sold attributes para add nalang sa quantity???

        how will this be retrieved for gdp calculation?
     */
}
