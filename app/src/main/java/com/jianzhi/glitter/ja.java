package com.jianzhi.glitter;

import static com.orange.jzchi.jzframework.tool.FormatConvert.StringHexToByte;

public class ja {
    public static String getBit(String a) {
        byte data[] = StringHexToByte(a);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append((data[i] >> 7) & 0x1);
            sb.append((data[i] >> 6) & 0x1);
            sb.append((data[i] >> 5) & 0x1);
            sb.append((data[i] >> 4) & 0x1);
            sb.append((data[i] >> 3) & 0x1);
            sb.append((data[i] >> 2) & 0x1);
            sb.append((data[i] >> 1) & 0x1);
            sb.append((data[i] >> 0) & 0x1);
        }

        return sb.toString();
    }
}
