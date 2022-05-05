package com.imuve.cristian.imuve;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Cristian on 01/09/2016.
 */
public class Utility {


    public static byte[] compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    public static byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(bytes);
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }


    public static byte[] decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 1024*1024;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int out_data_size = compressed.length*5;
        byte[] out_data = new byte[out_data_size];
        int out_data_len = 0;
        int bytesRead = 0;

        while ((bytesRead = gis.read(data)) != -1) {
            // string.append(new String(data, 0, bytesRead));
            if (out_data_len+data.length > out_data_size) {
                byte[] old_data = out_data;
                int old_out_data_size = out_data_len;
                out_data_size = out_data_len+data.length * 3;
                out_data = new byte[out_data_size];
                System.arraycopy(old_data, 0, out_data, 0, old_out_data_size);
                old_data = null;
                System.err.println("Resizing bytes[] from " + old_out_data_size + " to " + out_data_size);
            }

            System.arraycopy(data, 0, out_data, out_data_len, data.length);
            out_data_len+=data.length;
        }

        gis.close();
        is.close();

        // return string.toString();
        // return out_data.toString();
        return out_data;
    }

}
