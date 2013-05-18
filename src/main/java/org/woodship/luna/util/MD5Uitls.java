package org.woodship.luna.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: Genkyo Lee
 */
public class MD5Uitls
{
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Gets the MD5 hash of the given byte array.
     *
     * @param b byte array for which an MD5 hash is desired.
     * @return Array of 16 bytes, the hash of all updated bytes.
     */
    public static byte[] getHash(byte[] b)
    {
        MessageDigest md5 = getInstance("MD5");
        return md5.digest(b);
    }

    /**
     * Gets the MD5 hash of the given byte array.
     *
     * @param b byte array for which an MD5 hash is desired.
     * @return 32-character hex representation the data's MD5 hash.
     */
    public static String getHashString(byte[] b)
    {
        return toHexString(getHash(b));
    }

    /**
     * Gets the MD5 hash the data on the given InputStream.
     *
     * @param in byte array for which an MD5 hash is desired.
     * @return Array of 16 bytes, the hash of all updated bytes.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public static byte[] getHash(InputStream in) throws IOException
    {
        MessageDigest md5 = getInstance("MD5");
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            md5.update(buffer, 0, read);
        }
        in.close();
        return md5.digest();
    }

    /**
     * Gets the MD5 hash the data on the given InputStream.
     *
     * @param in byte array for which an MD5 hash is desired.
     * @return 32-character hex representation the data's MD5 hash.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public static String getHashString(InputStream in) throws IOException
    {
        return toHexString(getHash(in));
    }

    /**
     * Gets the MD5 hash of the given file.
     *
     * @param f file for which an MD5 hash is desired.
     * @return Array of 16 bytes, the hash of all updated bytes.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public static byte[] getHash(File f) throws IOException
    {
        return getHash(new FileInputStream(f));
    }

    /**
     * Gets the MD5 hash of the given file.
     *
     * @param f file array for which an MD5 hash is desired.
     * @return 32-character hex representation the data's MD5 hash.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public static String getHashString(File f) throws IOException
    {
        return getHashString(new FileInputStream(f));
    }

    /**
     * Gets the MD5 hash of the given String. The string is converted to bytes using the current platform's default character encoding.
     *
     * @param s String for which an MD5 hash is desired.
     * @return Array of 16 bytes, the hash of all updated bytes.
     */
    public static byte[] getHash(String s)
    {
        return getHash(s.getBytes());
    }

    /**
     * Gets the MD5 hash of the given String. The string is converted to bytes using the current platform's default character encoding.
     *
     * @param s String for which an MD5 hash is desired.
     * @return 32-character hex representation the data's MD5 hash.
     */
    public static String getHashString(String s)
    {
        return getHashString(s.getBytes());
    }

    /**
     * Gets the MD5 hash of the given String.
     *
     * @param s   String for which an MD5 hash is desired.
     * @param enc The name of a supported character encoding.
     * @return Array of 16 bytes, the hash of all updated bytes.
     * @throws java.io.UnsupportedEncodingException If the named encoding is not supported.
     */
    public static byte[] getHash(String s, String enc) throws UnsupportedEncodingException
    {
        return getHash(s.getBytes(enc));
    }

    /**
     * Gets the MD5 hash of the given String.
     *
     * @param s   String for which an MD5 hash is desired.
     * @param enc The name of a supported character encoding.
     * @return 32-character hex representation the data's MD5 hash.
     * @throws java.io.UnsupportedEncodingException If the named encoding is not supported.
     */
    public static String getHashString(String s, String enc) throws UnsupportedEncodingException
    {
        return getHashString(s.getBytes(enc));
    }

    private static MessageDigest getInstance(String algorithm)
    {
        try
        {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static String toHexString(byte[] bytes)
    {
        StringBuffer sb = new StringBuffer(32);
        for (byte b : bytes)
        {
            int j = b;
            if (j < 0)
            {
                j += 256;
            }
            sb.append(HEX_DIGITS[j / 16]).append(HEX_DIGITS[j % 16]);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            System.out.println("MD5 Test suite:");
            System.out.println("MD5(\"\"):" + getHashString(""));
            System.out.println("MD5(\"a\"):" + getHashString("a"));
            System.out.println("MD5(\"abc\"):" + getHashString("abc"));
            System.out.println("MD5(\"message digest\"):" + getHashString("message digest"));
            System.out.println("MD5(\"abcdefghijklmnopqrstuvwxyz\"):" + getHashString("abcdefghijklmnopqrstuvwxyz"));
            System.out.println("MD5(\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\"):"
                    + getHashString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
        }
        else if (args.length == 1)
        {
            System.out.println("MD5(" + args[0] + ")=" + getHashString(args[0]));
        }
        else if (args.length == 2)
        {
            System.out.println("MD5(" + args[1] + ")=" + getHashString(new File(args[1])));
        }
    }
} // end class