package com.gmrodrigues.androidmemoryhog;

import android.app.Activity;
import android.app.ActivityManager;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by glauber on 08/02/18.
 */

public class Hogger {
    private final int k = 1024;
    private  byte[][][][] buffer = new byte[k][][][];
    public   int l1,l2,l3 = 0;
    private  String errMsg = null;
    private  ActivityManager am;
    private  final ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
    private  final AtomicInteger a = new AtomicInteger();
    private  final long eachHogLimit = k * k * 100;

    private static final Hogger instance = new Hogger();
    public static Hogger instance(){
        return instance;
    }

    public  void setActivityManager(ActivityManager activity) {
        am = activity;
    }

    private  void updateStats(){
        am.getMemoryInfo(mi);
    }

    private  String bytesToString(long bytes){
        final String[] units = new String[]{"B", "KB", "MB", "GB","TB", "PB"};
        double b = bytes;
        int unit = 0;
        for (; unit < units.length && b > k; unit++){
            b /= k;
        }

        return String.format("%.2f%s", b, units[unit]);
    }

    public  long canHogBytes(){
        return mi.availMem;
    }

    public long thresholdBytes(){
        return mi.threshold;
    }

    public  long totalBytes(){
        return mi.totalMem;
    }

    public  boolean lowMemory(){
        return mi.lowMemory;
    }

    public  boolean hog(){
        a.getAndDecrement();

        try{
            if(a.get()>1){
                errMsg = "Hogging already!";
                return false;
            }

            if (l1 >= buffer.length){
                errMsg = "Hogged enough already!";
                return false;
            }

            try{
                long limit = 0;
                L: while(l1 < k){
                    if (buffer[l1] == null){
                        buffer[l1] = new byte[k][][];
                        l2 = 0;
                        l3 = 0;
                    }
                    byte[][][] b2 = buffer[l1];
                    while(l2 < k){
                        if (b2[l2] == null){
                            b2[l2] = new byte[k][];
                            l3 = 0;
                        }
                        byte[][] b3 = b2[l2];
                        while(l3 < k){
                            b3[l3]= new byte[k];
                            limit += k;
                            l3++;
                            if (limit >= eachHogLimit){
                                break L;
                            }
                        }
                        l2++;
                    }
                    l1++;
                }
                errMsg = null;
            }catch (Throwable t){
                t.printStackTrace();
                errMsg = t.getMessage();
            }
        }finally {
            updateStats();
            a.decrementAndGet();
        }
        return true;
    }

    public  long bytes(){
        long c = 0;
        C: for(int i1 = 0; i1 < k; i1++){
            byte[][][] b2 = buffer[i1];
            if (b2 == null){
               break C;
            }
            for (int i2 = 0; i2 < k; i2++){
                byte[][] b3 = b2[i2];
                if (b3 == null){
                    break C;
                }
                for(int i3 = 0; i3 < k; i3++){
                    byte[] b4 = b3[i3];
                    if (b4 == null){
                        break C;
                    }
                    c += b4.length;
                }
            }
        }
        return c;
    }

    public  long left(long bytes){
        return  ((long) Math.pow(k, 4)) -  bytes;
    }

    public  String info(){
        if (errMsg != null){
            return errMsg;
        }
        updateStats();
        final long hb = bytes();
        final String hogged = bytesToString(hb);
        final String total  = bytesToString(totalBytes());
        final String canHog = bytesToString(canHogBytes());
        final boolean lowMem = lowMemory();
        final long hogsLeft = left(hb);
        final long now = System.currentTimeMillis();

        return String.format("Total: %s\nHogged already: %s (%s bytes)\nCan Hog: %s\nLow Memory: %s\nHogs left: %s\nNow: %s", total, hogged, hb, canHog, lowMem, hogsLeft, now);
    }
}
