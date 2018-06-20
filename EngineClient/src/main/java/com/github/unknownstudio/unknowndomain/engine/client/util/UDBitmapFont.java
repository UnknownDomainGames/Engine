package com.github.unknownstudio.unknowndomain.engine.client.util;


import com.github.unknownstudio.unknowndomain.engine.client.resource.Texture2D;
import org.apache.commons.io.Charsets;
import org.joml.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * a bitmap format for fonts, with font data.
 *
 * format:
 * <br/>HEADER
 * <br/>-bitmap size
 * <br/>-font data (*)
 * <br/>BITMAP DATA (raw data compressed by GZIP)
 * <br/>
 * <br/>(*)font data consists of followings: all characters available in this bitmap, and its corresponding position and size
 */
public class UDBitmapFont {
    public static final byte[] MARKER = {0x55, 0x44, 0x62, 0x66};
    public static final byte[] ZERO_MARKER = {0,0,0,0,0,0,0,0};
    private Map<Character, Rectangle> fontData;

    private int width;

    private int height;

    /**
     * Texture of this UDBitmapFont. Only be used in Unknown Domain Game itself, not for Creator Kit
     */
    private Texture2D texture;

    private BufferedImage data;


    public UDBitmapFont(BufferedImage bitmap, Map<Character, Rectangle> data){
        texture = new Texture2D(bitmap);
        this.data = bitmap;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        fontData = data;
    }

    public static UDBitmapFont readFont(File file){
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            byte[] bytes = new byte[4];
            fileInputStream.read(bytes);
            if(bytes != MARKER){
                throw new IOException("This file is probably not a valid UDBitmapFont file!");
            }else{
                int wid = 0;
                int hei = 0;
                fileInputStream.read(bytes);
                wid = ByteBuffer.wrap(bytes).getInt();
                fileInputStream.read(bytes);
                hei = ByteBuffer.wrap(bytes).getInt();

                byte[] bytes1 = new byte[8];
                fileInputStream.read(bytes1);
                Map<Character, Rectangle> fontdata = new HashMap<>();
                while (bytes1 != ZERO_MARKER){
                    ByteBuffer code = ByteBuffer.wrap(bytes1, 0, 4);
                    int x = ByteBuffer.wrap(bytes1, 4, 8).getInt();
                    fileInputStream.read(bytes);
                    int y = ByteBuffer.wrap(bytes).getInt();
                    fileInputStream.read(bytes);
                    int w = ByteBuffer.wrap(bytes).getInt();
                    fileInputStream.read(bytes);
                    int h = ByteBuffer.wrap(bytes).getInt();
                    fontdata.put(StandardCharsets.UTF_16BE.decode(code).get(), new Rectangle(x,y,w,h));
                    fileInputStream.read(bytes1);
                }
                int bi = 0;
                ByteBuffer bb = ByteBuffer.allocate((int)file.length());
                while ((bi = fileInputStream.read()) != -1){
                    bb.put((byte)bi);
                }
                ByteArrayOutputStream a = new ByteArrayOutputStream();
                try(GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bb.array()))){
                    int count;
                    byte[] chunk = new byte[1024];
                    while((count = gzip.read(chunk, 0, 1024)) != -1){
                        a.write(chunk, 0, count);
                    }
                }
                bb = ByteBuffer.wrap(a.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
                BufferedImage img = new BufferedImage(wid, hei, BufferedImage.TYPE_4BYTE_ABGR);
                int[] array = new int[bb.remaining()];
                bb.asIntBuffer().get(array);
                img.setRGB(0,0,wid, hei, array, 0, wid);

                return new UDBitmapFont(img, fontdata);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeInto(File file){
        writeFont(this, file);
    }

    public static void writeFont(UDBitmapFont font, File file){
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            fileOutputStream.write(MARKER);
            fileOutputStream.write(font.width);
            fileOutputStream.write(font.height);
            for (Map.Entry<Character, Rectangle> entry:font.fontData.entrySet()) {
                byte[] bytes = new byte[4];
                ByteBuffer buf = StandardCharsets.UTF_16BE.encode(String.valueOf(entry.getKey().charValue()));
                buf.get(bytes, 4 - buf.limit(), buf.limit());
                fileOutputStream.write(bytes);
                fileOutputStream.write(ByteBuffer.allocate(4).putInt(entry.getValue().x).array());
                fileOutputStream.write(ByteBuffer.allocate(4).putInt(entry.getValue().y).array());
                fileOutputStream.write(ByteBuffer.allocate(4).putInt(entry.getValue().width).array());
                fileOutputStream.write(ByteBuffer.allocate(4).putInt(entry.getValue().height).array());
            }
            fileOutputStream.write(new byte[]{0,0,0,0,0,0,0,0});
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try(GZIPOutputStream gzip = new GZIPOutputStream(baos)){
                ByteBuffer b1 = GLHelper.getByteBufferFromImage(font.data);
                byte[] by1 = b1.array();
                gzip.write(by1, 0, by1.length);
                b1.clear();
            }
            fileOutputStream.write(baos.toByteArray());
            baos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
