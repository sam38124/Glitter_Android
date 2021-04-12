/*******************************************************************************
 * Copyright (c) 2019, Semiconductor Components Industries, LLC
 * (d/b/a ON Semiconductor). All rights reserved.
 *
 * This code is the property of ON Semiconductor and may not be redistributed
 * in any form without prior written permission from ON Semiconductor.
 * The terms of use and warranty for this code are covered by contractual
 * agreements between ON Semiconductor and the licensee.
 *
 * This is Reusable Code.
 *
 * Class Name: Cobs
 ******************************************************************************/

package com.onsemi.protocol.cobs;

import java.util.ArrayList;
import java.util.List;

/**
 * Encoder/Decoder for COBS data.
 */

public class Cobs {
    /**
     * Returns the encoded representation of the given bytes.
     * Inefficient method, but easy to use and understand.
     *
     * @param src the bytes to encode
     * @return the encoded bytes.
     */
    public static byte[] encode(byte[] src) throws Exception {

        ArrayList<byte[]> dest = new ArrayList<>(maxEncodedSize(src.length));
        encode(src, 0, src.length, dest);
        //dest.trimExcess();
        return unboxByteArry(dest);
    }

    /**
     * Returns the decoded representation of the given bytes.
     * Inefficient method, but easy to use and understand.
     *
     * @param src the bytes to decode
     * @return the decoded bytes.
     */
    public static byte[] decode(byte[] src) throws Exception
    {
        ArrayList<byte[]> dest = new ArrayList<>(src.length);
        decode(src, 0, src.length, dest);
        //dest.TrimExcess();
        // return dest.asArray();
        return unboxByteArry(dest);
    }

    /**
     * Adds (appends) the encoded representation of the range &lt;code&gt;src[from..to)&lt;/code&gt;
     * to the given destination list.
     *
     * @param src the bytes to encode
     * @param from the first byte to encode (inclusive)
     * @param to the last byte to encode (exclusive)
     * @param dest the destination list to append to
     */
    private static void encode(byte[] src, int from, int to, List<byte[]> dest) throws Exception {
        checkRange(from, to, src);
        //dest.EnsureCapacity(dest.size() + maxEncodedSize(to - from)); // for performance ensure add() will never need to expand list
        int code = 1; // can't use unsigned byte arithmetic...
        int blockStart = -1;

        // find zero bytes
        while (from < to)
        {
            if (src[from] == 0)
            {
                finishBlock(code, src, blockStart, dest, from - blockStart);
                code = 1;
                blockStart = -1;
            }
            else
            {
                if (blockStart < 0)
                {
                    blockStart = from;
                }
                code++;
                if (code == 0xFF)
                {
                    finishBlock(code, src, blockStart, dest, from - blockStart + 1);
                    code = 1;
                    blockStart = -1;
                }
            }
            from++;
        }
        finishBlock(code, src, blockStart, dest, from - blockStart);
    }

    private static void finishBlock(int code, byte[] src, int blockStart, List<byte[]> dest, int length)
    {
        byte[] codeByteArray = new byte[1];
        codeByteArray[0] = (byte)code;

        dest.add(codeByteArray);                //code is the count to next 00 so add one byte array to list with code in

        if (blockStart >= 0)
        {
            byte[] myByteArray = new byte[length];

            System.arraycopy(src, blockStart, myByteArray, 0, length);
            dest.add(myByteArray);
        }
    }

    /**
     * Returns the maximum amount of bytes an encoding of &lt;code&gt;size&lt;/code&gt; bytes takes in the worst case.
     */
    private static int maxEncodedSize(int size)
    {
        return size + 1 + size / 254;
    }

    /**
     * Adds (appends) the decoded representation of the range &lt;code&gt;src[from..to)&lt;/code&gt;
     * to the given destination list.
     *
     * @param src the bytes to decode
     * @param from the first byte to decode (inclusive)
     * @param to the last byte to decode (exclusive)
     * @param dest the destination list to append to
     * @throws Exception if src data is corrupt (encoded erroneously)
     */
    private static void decode(byte[] src, int from, int to, List<byte[]> dest) throws Exception
    {

        checkRange(from, to, src);
        // dest.ensureCapacity(dest.size() + (to-from)); // for performance ensure add() will never need to expand list

        while (from < to)
        {
            int code = src[from++] & 0xFF;
            int len = code - 1;

            if (code == 0 || from + len > to) {
                throw new Exception("Corrupt COBS encoded data - bug in remote encoder?");
            }

            byte[] myByteArray = new byte[len];

            System.arraycopy(src, from, myByteArray, 0, len);
            dest.add(myByteArray);

            from += len;
            if (code < 0xFF && from < to)
            {
                // unnecessary to write last zero (is implicit anyway)
                byte[] zero = new byte[1];
                zero[0] = 0x00;
                dest.add(zero);
            }
        }
    }

    /**
     * Checks if the given range is within the contained array's bounds.
     */
    private static void checkRange(int from, int to, byte[] arr) throws Exception {
        if (from < 0 || from > to || to > arr.length)
        {
            throw new Exception("from:" + from + ", to: " + to + ", size: " + arr.length);
        }
    }

    private static byte[] unboxByteArry(ArrayList<byte[]> list ) {

        int arraySize = 0;
        int pos = 0;
        for(int i = 0; i < list.size(); i++) {
            arraySize += list.get(i).length;
        }

        byte[] array = new byte[arraySize];
        for(int i = 0; i < list.size(); i++) {
            byte[] tmpArray = list.get(i);
            for(int j = 0; j < tmpArray.length; j++) {
                array[pos] = tmpArray[j];
                pos++;
            }
        }
        return array;
    }
}
