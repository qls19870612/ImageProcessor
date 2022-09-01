package com.ejjiu.image.utils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/03/05 10:34
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class UnZip
{
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                data.length);
        byte[] buffer = new byte[4096];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        byte[] output = outputStream.toByteArray();
        outputStream.close();
        return output;
    }



        public static void unzip(String zipFilePath, String destDir)
        {
            System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding")); //Error preventing Chinese from being in the file name
            //System.out.println(System.getProperty("sun.zip.encoding")); //ZIP encoding
            // System.out.println (System.getProperty ("sun.jnu.encoding")); // current file encoding
            //System.out.println(System.getProperty("file.encoding")); //This is the encoding method of the current file content.

            File dir = new File(destDir);
            // create output directory if it doesn't exist
            if (!dir.exists()) dir.mkdirs();
            FileInputStream fis;
            // buffer for read and write data to file
            byte[] buffer = new byte[1024];
            try
            {
                fis = new FileInputStream(zipFilePath);
                ZipInputStream zis = new ZipInputStream(fis);
                ZipEntry ze = zis.getNextEntry();
                while (ze != null)
                {
                    String fileName = ze.getName();
                    File newFile = new File(destDir + File.separator + fileName);
                    //System.out.println("Unzipping to " + newFile.getAbsolutePath());
                    // create directories for sub directories in zip
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0)
                    {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    // close this ZipEntry
                    zis.closeEntry();
                    ze = zis.getNextEntry();
                }
                // close last ZipEntry
                zis.closeEntry();
                zis.close();
                fis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }




}