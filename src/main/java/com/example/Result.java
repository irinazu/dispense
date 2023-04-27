package com.example;


import java.io.Serializable;

public class Result implements Serializable {
    String sum;
    String queue;
    String port;

    @Override
    public String toString() {
        return "Result{" +
                "sum=" + sum +
                ", queue='" + queue + '\'' +
                '}';
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
