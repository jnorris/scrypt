package com.lambdaworks.crypto;

import java.security.GeneralSecurityException;

import com.lambdaworks.codec.Base64;

public class Benchmark {

    private static final int BLOCK = 10;

    static interface Block {
        void run() throws Exception;
    }

    public static final String ANS = "qvz1XkWUdv9r5vB2UucTp/UVJ5rkQ51uGEZfRdILG7M=";

    static class Timer {
        final long start = System.nanoTime();
        void print() {
            System.out.println((System.nanoTime() - start) / 1e9 / BLOCK + " seconds per hash");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("java:");
        final int runs = 5;
        for (int i = 0; i < runs; ++i) {
            time(new Block() {
                @Override
                public void run() throws Exception {
                    runJava();
                }
            });
        }

        System.out.println("native:");
        for (int i = 0; i < runs; ++i) {
            time(new Block() {
                @Override
                public void run() throws Exception {
                    runNative();
                }
            });
        }
    }

    private static void runNative() throws Exception {
        lots(new Block() {
            @Override
            public void run() throws GeneralSecurityException {
                if (!ANS.equals(new String(Base64.encode(SCrypt.scryptN(new byte[16], new byte[16], 16384, 8, 1, 32)))))
                    throw new IllegalStateException();
            }
        });
    }

    private static void runJava() throws Exception {
        lots(new Block() {
            @Override
            public void run() throws GeneralSecurityException {
                if (!ANS.equals(new String(Base64.encode(SCrypt.scryptJ(new byte[16], new byte[16], 16384, 8, 1, 32)))))
                    throw new IllegalStateException();
            }
        });
    }

    private static void lots(Block block) throws Exception {
        for (int i = 0; i < BLOCK; ++i) {
            block.run();
        }
    }

    private static void time(Block block) throws Exception {
        final Timer t = new Timer();
        block.run();
        t.print();
        Thread.sleep(100);
    }
}
