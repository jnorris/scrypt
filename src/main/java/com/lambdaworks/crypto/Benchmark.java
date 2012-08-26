package com.lambdaworks.crypto;

import static com.lambdaworks.crypto.SCrypt.scryptJ;
import static com.lambdaworks.crypto.SCrypt.scryptN;

import java.security.GeneralSecurityException;

import com.lambdaworks.codec.Base64;

public class Benchmark {

    private static final byte[] BYTE_16 = new byte[16];
    private static final int BLOCK = 50;

    private static final Block NATIVE = new Block() {
        @Override
        public void run() throws GeneralSecurityException {
            check(scryptN(BYTE_16, BYTE_16, 16384, 8, 1, 32));
        }
    };

    private static final Block JAVA = new Block() {
        @Override
        public void run() throws GeneralSecurityException {
            check(scryptJ(BYTE_16, BYTE_16, 16384, 8, 1, 32));
        }
    };

    static interface Block {
        void run() throws Exception;
    }

    public static final String ANS = "qvz1XkWUdv9r5vB2UucTp/UVJ5rkQ51uGEZfRdILG7M=";

    public static void main(String[] args) throws Exception {
        System.out.println("native:");

        double nat = Math.min(
                time(repeatedly(NATIVE)),
                time(repeatedly(NATIVE)));

        System.out.println("java:");
        double jav = Math.min(
                time(repeatedly(JAVA)),
                time(repeatedly(JAVA)));

        System.out.println("Speedup: " + (jav/nat));
    }

    private static Block repeatedly(final Block in) {
        return new Block() {
            @Override
            public void run() throws Exception {
                for (int i = 0; i < BLOCK; ++i)
                    in.run();
            }
        };
    }

    private static void check(byte[] res) {
        if (!ANS.equals(new String(Base64.encode(res))))
            throw new IllegalStateException();
    }

    private static double time(Block block) throws Exception {
        final long start = System.nanoTime();
        block.run();
        double spkdf = (System.nanoTime() - start) / 1e9 / BLOCK;
        System.out.println(spkdf + " seconds per kdf");
        Thread.sleep(100);
        return spkdf;
    }
}
