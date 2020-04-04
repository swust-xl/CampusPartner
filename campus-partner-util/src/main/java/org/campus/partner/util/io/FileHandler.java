package org.campus.partner.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.MessageDigest;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.campus.partner.util.ExceptionFormater;

/***
 * 文件操作助手*
 * 
 * @author xl
 * @since 1.0.0
 */
public class FileHandler {
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String TEMP_UUID_FILE_TEMPLATE = TEMP_DIR + File.separator + "%s%s%s";
    public static final String TEMP_UUID_DIR_TEMPLATE = TEMP_DIR + File.separator + "%s";
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    private FileHandler() {}

    /**
     * 根据文件对象迭代创建文件.
     *
     * @param file
     *            文件对象
     * @return 成功－返回已创建的文件对象
     * @author xl
     * @throws NullPointerException
     *             传入file对象为null
     * @throws RuntimeException
     *             创建新文件异常
     * @since 1.0.0
     */
    public static File createFile(File file) {
        if (file == null) {
            throw new NullPointerException("parameter cannot null");
        }
        if (file.isFile()) {
            return file;
        }
        try {
            file.getParentFile()
                    .mkdirs();
            file.createNewFile();
            if (!file.isFile()) {
                throw new RuntimeException("cannot create file '" + file.getAbsolutePath() + "'");
            } else {
                return file;
            }
        } catch (IOException e) {
            throw new RuntimeException(ExceptionFormater.format(e));
        }
    }

    /**
     * 迭代创建文件.
     * 
     * @param existFile
     *            已存在的文件对象
     * @param isOnlyChangeExtension
     *            是否只改变扩展名来生成对应文件（默认：false，即：自动随机生成UUID无扩展名的文件名）
     * @param newExtension
     *            新的扩展名（如果为null,则扩展名默认采用 "tmp" ）
     * @return 成功－返回已创建的文件对象
     * @throws NullPointerException
     *             传入file对象为null
     * @throws IllegalArgumentException
     *             传入参数不合法
     * @author xl
     * @since 1.0.0
     */
    public static File createFile(File existFile, boolean isOnlyChangeExtension, String newExtension) {
        if (existFile == null) {
            throw new NullPointerException("parameter 'existFile' cannot null");
        }
        if (!existFile.isFile()) {
            throw new IllegalArgumentException("parameter [" + existFile.getAbsolutePath() + "] is not a file");
        }
        String ext = newExtension;
        if (ext == null) {
            ext = "tmp";
        }
        if (!isOnlyChangeExtension) {
            return createFile(new File(existFile.getParent(), UUID.randomUUID()
                    .toString() + "." + ext.replaceFirst("\\.", "")));
        }
        if (isOnlyChangeExtension) {// 只改变扩展名
            String parent = existFile.getParent();
            String fileName = existFile.getName();
            int lastPointIndex = fileName.lastIndexOf(".");
            if (lastPointIndex < 0 || lastPointIndex == fileName.length() - 1) {// 文件名无后缀
                return createFile(new File(parent, fileName + "." + ext.replaceFirst("\\.", "")));
            } else {
                return createFile(new File(parent, fileName.substring(0, lastPointIndex + 1)
                        .concat(ext.replaceFirst("\\.", ""))));
            }
        }
        throw new RuntimeException("unknown exception");
    }

    /**
     * 根据文件路径迭代创建文件.
     * 
     * @param filePath
     *            文件路径
     * @return 成功－返回已创建的文件路径
     * @throws NullPointerException
     *             传入file对象为null
     * @throws IllegalArgumentException
     *             传入参数不合法
     * @author xl
     * @since 1.0.0
     */
    public static String createFile(String filePath) {
        return createFile(new File(filePath)).getAbsolutePath();
    }

    /**
     * 根据文件路径迭代创建文件
     * 
     * @param existFilePath
     *            已存在的文件路径
     * @param isOnlyChangeExtension
     *            是否只改变扩展名来生成对应文件（默认：false，即：自动随机生成UUID无扩展名的文件名）
     * @param newExtension
     *            新的扩展名（如果为null,则扩展名默认采用 "tmp" ）
     * @return 成功－返回已创建的文件路径
     * @throws NullPointerException
     *             传入file对象为null
     * @throws IllegalArgumentException
     *             传入参数不合法
     * @author xl
     * @since 1.0.0
     */
    public static String createFile(String existFilePath, boolean isOnlyChangeExtension, String newExtension) {
        if (existFilePath == null) {
            throw new NullPointerException("parameter cannot null");
        }
        return createFile(new File(existFilePath), isOnlyChangeExtension, newExtension).getAbsolutePath();
    }

    /**
     * 根据已存在的文件迭代删除文件及文件夹.
     *
     * @param existFile
     *            已存在的文件/文件夹
     * @param force
     *            是否强制删除. <code>true</code> - 不管文件夹是否为空，都会迭代删除;
     *            <code>false</code> - 文件夹不为空，则不执行删除.文件夹为空，则执行迭代删除
     * @author xl
     * @since 1.0.0
     */
    public static void deleteFile(File existFile, boolean force) {
        if (existFile == null) {
            return;
        }
        if (existFile.isDirectory()) {
            File[] children = existFile.listFiles();
            if (children.length == 0) {// 删除空文件夹
                deleteSelfAndDeleteParentIfEmpty(existFile, force);
                return;
            }
            if (force) {
                for (File subFile : children) {
                    deleteFile(subFile, force);
                }
                deleteSelfAndDeleteParentIfEmpty(existFile, force);
            }
            return;
        } else if (existFile.isFile()) {// 删除文件
            deleteSelfAndDeleteParentIfEmpty(existFile, force);
            return;
        } else {
            return;
        }
    }

    // 删除文件或空文件夹自己，并检测父目录是否为空，为空则继续删除该空文件夹
    private static void deleteSelfAndDeleteParentIfEmpty(File existFile, boolean force) {
        existFile.delete();
        File emptyDir = existFile.getParentFile();
        if (emptyDir.isDirectory() && emptyDir.listFiles().length == 0) {
            deleteFile(existFile.getParentFile(), force);
        }
    }

    /**
     * 根据已存在的文件迭代删除文件及文件夹.
     *
     * @param existFilePath
     *            已存在的文件/文件夹路径
     * @param force
     *            是否强制删除. <code>true</code> - 不管文件夹是否为空，都会迭代删除;
     *            <code>false</code> - 文件夹不为空，则不执行删除.文件夹为空，则执行迭代删除
     * @author xl
     * @since 1.0.0
     */
    public static void deleteFile(String existFilePath, boolean force) {
        deleteFile(new File(existFilePath), force);
    }

    /**
     * 根据已存在的文件迭代删除文件及文件夹.
     * <p/>
     * 注：当且仅当文件夹为空时才允许删除.
     *
     * @param existFile
     *            已存在的文件/文件夹
     * @author xl
     * @since 1.0.0
     */
    public static void deleteFile(File existFile) {
        deleteFile(existFile, false);
    }

    /**
     * 根据已存在的文件路径迭代删除文件及文件夹.
     * <p/>
     * 注：当且仅当文件夹为空时才允许删除.
     *
     * @param existFile
     *            已存在的文件/文件夹路径
     * @author xl
     * @since 1.0.0
     */
    public static void deleteFile(String existFilePath) {
        deleteFile(new File(existFilePath));
    }

    /**
     * 迭代创建临时文件.
     * 
     * @param noExtensionFileName
     *            文件名（不带扩展名）,格式如："test"
     * @param extension
     *            扩展名（如果为null,则扩展名默认采用 "tmp" ）,格式如："doc"或".doc"
     * @return 成功－返回已创建的临时文件对象
     * @author xl
     * @since 1.0.0
     */
    public static File createTempFile(String noExtensionFileName, String extension) {
        String baseFileName = noExtensionFileName;
        if (baseFileName == null || baseFileName.isEmpty()) {
            baseFileName = "~" + UUID.randomUUID()
                    .toString();
        }
        String ext = extension;
        if (ext == null || ext.trim()
                .isEmpty() || ".".equals(ext.trim())) {
            ext = "tmp";
        }
        String defaultDivPoint = ".";
        int idx = baseFileName.indexOf(defaultDivPoint);
        if (idx >= 0) {
            baseFileName = baseFileName.substring(0, idx);
        }
        idx = ext.indexOf(defaultDivPoint);
        if (idx >= 0) {
            ext = ext.replaceFirst("\\.", "");
        }
        String path = String.format(TEMP_UUID_FILE_TEMPLATE, baseFileName, defaultDivPoint, ext);
        File tmpFile = createFile(new File(path));
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    /**
     * 迭代创建临时文件.
     * 
     * @param fileName
     *            文件名（可带扩展名）,格式如："test.doc"
     * @param isOnlyChangeExtension
     *            是否只改变扩展名来生成对应临时文件（默认：false）
     * @param newExtension
     *            新的扩展名（isOnlyChangeExtension为 true 时有效，如果为null,则扩展名默认采用 "tmp"
     *            ）,格式如："doc"或".doc"
     * @return 成功－返回已创建的临时文件对象
     * @author xl
     * @since 1.0.0
     */
    public static File createTempFile(String fileName, boolean isOnlyChangeExtension, String newExtension) {
        String ext = newExtension;
        if (fileName == null) {
            return createTempFile(null, ext);
        }
        int lastPointIndex = fileName.lastIndexOf(".");
        if (lastPointIndex < 0 || lastPointIndex == fileName.length() - 1) {// 文件名无后缀
            return createTempFile(fileName, null);
        } else {
            if (isOnlyChangeExtension) {// 只改变扩展名
                if (ext == null) {
                    ext = "tmp";
                }
                return createTempFile(fileName.substring(0, lastPointIndex), ext);
            }
        }
        String noExtensionName = fileName.substring(0, lastPointIndex);
        String extension = fileName.substring(lastPointIndex);
        return createTempFile(noExtensionName, extension);
    }

    /**
     * 迭代创建临时文件.
     * 
     * @param fileName
     *            文件名（可带扩展名）,格式如："test.doc"
     * @return 成功－返回已创建的临时文件对象
     * @author xl
     * @since 1.0.0
     */
    public static File createTempFile(String fileName) {
        return createTempFile(fileName, false, null);
    }

    /**
     * 迭代创建临时文件.
     * 
     * @param extension
     *            扩展名（如果为null,则扩展名默认采用 "tmp" ）,格式如："doc"或".doc"
     * @param isRandomUUIDName
     *            是否以UUID方式产生唯一名字，否则以当前Unix时间戳方式产生瞬时时间名字
     * @return 成功－返回已创建的临时文件对象
     * @author xl
     * @since 1.0.0
     */
    public static File createTempFile(String extension, boolean isRandomUUIDName) {
        if (extension == null) {
            return createTempFile();
        }
        if (isRandomUUIDName) {
            return createTempFile("~" + UUID.randomUUID()
                    .toString(), extension);
        } else {
            return createTempFile("~" + System.currentTimeMillis(), extension);
        }
    }

    /**
     * 迭代创建临时文件.
     * 
     * @return 成功－返回已创建的文件对象
     * @author xl
     * @since 1.0.0
     */
    public static File createTempFile() {
        return createTempFile("~" + UUID.randomUUID()
                .toString(), "tmp");
    }

    /**
     * 迭代创建临时文件夹.
     * 
     * @param isRandomUUIDName
     *            是否以UUID方式产生唯一名字，否则以当前Unix时间戳方式产生瞬时时间名字
     * @return 成功－返回已创建的文件夹的文件对象
     * @throws RuntimeException
     *             无法创建文件夹
     * @author xl
     * @since 1.0.0
     */
    public static File createTempDir(boolean isRandomUUIDName) {
        String subDir = null;
        if (isRandomUUIDName) {
            subDir = "~" + UUID.randomUUID()
                    .toString();
        } else {
            subDir = "~" + System.currentTimeMillis();
        }
        File dir = new File(TEMP_DIR, subDir);
        dir.deleteOnExit();
        if (dir.isDirectory()) {
            return dir;
        }
        boolean ok = dir.mkdirs();
        if (!ok) {
            throw new RuntimeException("Create dir [" + dir.getAbsolutePath() + "] fail!");
        }
        return dir;
    }

    /**
     * 迭代创建临时文件夹.
     * 
     * @return 成功－返回已创建的文件夹的文件对象
     * @throws RuntimeException
     *             无法创建文件夹
     * @author xl
     * @since 1.0.0
     */
    public static File createTempDir() {
        return createTempDir(true);
    }

    /**
     * 根据文件夹文件对象迭代创建文件夹.
     * 
     * @param dirFile
     *            文件夹文件对象
     * @return 成功－返回已创建的文件夹文件对象
     * @throws NullPointerException
     *             传入file对象为null
     * @throws RuntimeException
     *             无法创建文件夹
     * @author xl
     * @since 1.0.0
     */
    public static File createDir(File dirFile) {
        if (dirFile == null) {
            throw new NullPointerException("parameter 'dirFile' cannot null");
        }
        if (dirFile.isDirectory()) {
            return dirFile;
        }
        if (dirFile.mkdirs() && dirFile.isDirectory()) {
            return dirFile;
        } else {
            throw new RuntimeException("cannot create dir [" + dirFile.getAbsolutePath() + "]");
        }
    }

    /**
     * 根据文件夹路径迭代创建文件夹.
     * 
     * @param dirFilePath
     *            文件夹路径
     * @return 成功－返回已创建的文件夹路径
     * @throws NullPointerException
     *             传入file对象为null
     * @throws RuntimeException
     *             无法创建文件夹
     * @author xl
     * @since 1.0.0
     */
    public static String createDir(String dirFilePath) {
        return createDir(new File(dirFilePath)).getAbsolutePath();
    }

    /**
     * 根据已存在的文件夹迭代删除文件夹.
     * <p/>
     * 注：当且仅当文件夹为空时才允许删除.
     *
     * @param existDir
     *            已存在的文件夹
     * @author xl
     * @since 1.0.0
     */
    public static void deleteDir(File existDir) {
        deleteDir(existDir, false);
    }

    /**
     * 根据已存在的文件夹路径迭代删除文件夹.
     * <p/>
     * 注：当且仅当文件夹为空时才允许删除.
     *
     * @param existDirPath
     *            已存在的文件夹路径
     * @author xl
     * @since 1.0.0
     */
    public static void deleteDir(String existDirPath) {
        deleteDir(existDirPath, false);
    }

    /**
     * 根据已存在的文件夹迭代删除文件夹.
     * <p/>
     * 注：当且仅当文件夹为空时才允许删除.
     *
     * @param existDir
     *            已存在的文件夹
     * @param force
     *            是否强制删除. <code>true</code> - 不管文件夹是否为空，都会迭代删除;
     *            <code>false</code> - 文件夹不为空，则不执行删除.文件夹为空，则执行迭代删除
     * @author xl
     * @since 1.0.0
     */
    public static void deleteDir(File existDir, boolean force) {
        if (existDir == null) {
            return;
        }
        if (existDir.isDirectory()) {
            deleteFile(existDir, force);
        } else {
            return;
        }
    }

    /**
     * 根据已存在的文件夹路径迭代删除文件夹.
     * <p/>
     * 注：当且仅当文件夹为空时才允许删除.
     *
     * @param existDirPath
     *            已存在的文件夹路径
     * @param force
     *            是否强制删除. <code>true</code> - 不管文件夹是否为空，都会迭代删除;
     *            <code>false</code> - 文件夹不为空，则不执行删除.文件夹为空，则执行迭代删除
     * @author xl
     * @since 1.0.0
     */
    public static void deleteDir(String existDirPath, boolean force) {
        deleteDir(new File(existDirPath), force);
    }

    /**
     * 缓冲包装输入流.
     * 
     * @param is
     *            输入流.如果已包装，则直接返回
     * @return 包装后的输入流
     * @author xl
     * @since 1.0.0
     */
    public static InputStream buffered(InputStream is) {
        int size;
        try {
            size = is.available() % DEFAULT_BUFFER_SIZE;
            size = size <= 0 ? DEFAULT_BUFFER_SIZE : size;
        } catch (IOException e) {
            size = DEFAULT_BUFFER_SIZE;
        }
        return buffered(is, size);
    }

    /**
     * 缓冲包装输入流.
     * 
     * @param is
     *            输入流。如果已包装，则直接返回。
     * @param size
     *            缓冲区大小（>0）
     * @return 包装后的输入流
     * @author xl
     * @since 1.0.0
     */
    public static InputStream buffered(InputStream is, int size) {
        if (is instanceof BufferedInputStream) {
            return is;
        }
        return new BufferedInputStream(is, size);
    }

    /**
     * 缓冲包装输出流.
     * 
     * @param os
     *            输出流.如果已包装，则直接返回
     * @return 包装后的输出流
     * @author xl
     * @since 1.0.0
     */
    public static OutputStream buffered(OutputStream os) {
        return buffered(os, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 缓冲包装输出流.
     * 
     * @param os
     *            输出流.如果已包装，则直接返回.
     * @param size
     *            缓冲区大小（>0）
     * @return 包装后的输出流
     * @author xl
     * @since 1.0.0
     */
    public static OutputStream buffered(OutputStream os, int size) {
        if (os instanceof BufferedOutputStream) {
            return os;
        }
        return new BufferedOutputStream(os, size);
    }

    /**
     * 读取文件到内存中.
     * 
     * @param fromPath
     *            输入文件路径
     * @return 读取的文件字节数组
     * @throws IOException
     *             读取文件异常
     * @throws NullPointerException
     *             传入文件路径为null
     * @author xl
     * @since 1.0.0
     */
    public static byte[] readPath2Bytes(String fromPath) throws IOException {
        if (fromPath == null) {
            throw new NullPointerException("parameter cannot null");
        }
        byte[] fileBytes = null;
        FileInputStream fis = null;
        FileChannel smallFC = null;
        RandomAccessFile acessFile = null;
        FileChannel bigFC = null;
        ByteBuffer buf = null;
        MappedByteBuffer mapBuf = null;
        try {
            fis = new FileInputStream(fromPath);
            smallFC = fis.getChannel();
            long fileSize = smallFC.size();
            if (fileSize < 52428800) {// 如果文件尺寸小于500m,则直接读取
                buf = ByteBuffer.allocate((int) fileSize);
                buf.clear();
                smallFC.read(buf);
                buf.flip();
                fileBytes = buf.array();
            } else if (fileSize >= 52428800) {// 如果文件尺寸大于500m，则文件映射读取
                acessFile = new RandomAccessFile(fromPath, "rw");
                bigFC = acessFile.getChannel();
                long dataLen = bigFC.size();
                if (dataLen > Integer.MAX_VALUE) {
                    throw new RuntimeException("this file size is [" + dataLen + "] bytes, but must less than ["
                            + Integer.MAX_VALUE + "] bytes");
                }
                fileBytes = new byte[(int) dataLen];
                mapBuf = bigFC.map(FileChannel.MapMode.READ_ONLY, 0, dataLen);
                while (mapBuf.hasRemaining()) {
                    mapBuf.get(fileBytes);
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (smallFC != null) {
                smallFC.close();
            }
            if (bigFC != null) {
                bigFC.close();
            }
            if (acessFile != null) {
                acessFile.close();
            }
            if (buf != null) {
                buf.clear();
                buf = null;
            }
            if (mapBuf != null) {
                mapBuf.clear();
                mapBuf = null;
            }
        }
        return fileBytes;
    }

    /**
     * 读取文件到输入流.
     *
     * @param fromFile
     *            输入文件对象
     * @return 读取的文件输入流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static InputStream readFile2Stream(File fromFile) throws IOException {
        return buffered(new FileInputStream(fromFile), DEFAULT_BUFFER_SIZE);
    }

    /**
     * 读取文件到输入流.
     *
     * @param fromPath
     *            输入文件路径
     * @return 读取的文件输入流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static InputStream readFile2Stream(String fromPath) throws IOException {
        return buffered(new FileInputStream(fromPath), DEFAULT_BUFFER_SIZE);
    }

    /**
     * 读取文件到内存中.
     * 
     * @param fromFile
     *            输入文件对象
     * @return 读取的文件字节数组
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static byte[] readFile2Bytes(File fromFile) throws IOException {
        return readPath2Bytes(fromFile.getCanonicalPath());
    }

    /**
     * 读取数据流转换成对应的字节数组，并关闭输入流.
     * 
     * @param is
     *            输入的数据流
     * @return 读取的数据流对应的字节数组
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static byte[] readStream2Bytes(InputStream is) throws IOException {
        return readStream2Bytes(is, true);
    }

    /**
     * 读取数据流转换成对应的字节数组，并关闭输入流.
     * 
     * @param is
     *            输入的数据流
     * @param bufferSize
     *            设置读/写的缓冲区大小
     * @return 读取的数据流对应的字节数组
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static byte[] readStream2Bytes(InputStream is, int bufferSize) throws IOException {
        return readStream2Bytes(is, bufferSize, true);
    }

    /**
     * 读取数据流转换成对应的字节数组，并关闭输入流.
     * 
     * @param is
     *            输入的数据流
     * @param closeInStream
     *            是否关闭输入流
     * @return 读取的数据流对应的字节数组
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static byte[] readStream2Bytes(InputStream is, boolean closeInStream) throws IOException {
        int size = is.available() % DEFAULT_BUFFER_SIZE;
        return readStream2Bytes(is, size <= 0 ? DEFAULT_BUFFER_SIZE : size, closeInStream);
    }

    /**
     * 读取数据流转换成对应的字节数组，并关闭输入流.
     * 
     * @param is
     *            输入的数据流
     * @param bufferSize
     *            设置读/写的缓冲区大小
     * @param closeInStream
     *            是否关闭输入流
     * @return 读取的数据流对应的字节数组
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static byte[] readStream2Bytes(InputStream is, int bufferSize, boolean closeInStream) throws IOException {
        byte[] buf = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            buf = new byte[bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : bufferSize];
            int len = -1;
            while ((len = is.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            byte[] targetBytes = baos.toByteArray();
            return targetBytes;
        } finally {
            if (baos != null) {
                baos.close();
            }
            if (is != null && closeInStream) {
                is.close();
            }
        }
    }

    /**
     * 读取Base64编码后的二进制数据字符串到内存中.
     * 
     * @param base64Str
     *            输入的Base64编码后的二进制数据字符串
     * @return 读取的base64解码后的字节数组
     * @author xl
     * @since 1.0.0
     */
    public static byte[] readBase64Str2Bytes(String base64Str) {
        return DatatypeConverter.parseBase64Binary(base64Str);
    }

    /**
     * 读取字节数组转换成对应的数据流.
     * 
     * @param fromFileBytes
     *            输入的文件字节数组
     * @return 读取的数据流
     * @author xl
     * @since 1.0.0
     */
    public static InputStream readBytes2Stream(byte[] fromFileBytes) {
        return new ByteArrayInputStream(fromFileBytes);
    }

    /**
     * 读取Base64编码后的二进制数据字符串转换成对应的数据流.
     * 
     * @param base64Str
     *            输入的Base64编码后的二进制数据字符串
     * @return 读取的base64解码后的数据流
     * @author xl
     * @since 1.0.0
     */
    public static InputStream readBase64Str2Stream(String base64Str) {
        return readBytes2Stream(DatatypeConverter.parseBase64Binary(base64Str));
    }

    /**
     * 写指定字节的文件字节数组到指定输出流中.
     * 
     * @param fromFileBytes
     *            要写的文件字节数组
     * @param toStream
     *            写到的文件输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeBytes2Stream(byte[] fromFileBytes, OutputStream toStream) throws IOException {
        writeBytes2Stream(fromFileBytes, toStream, false);
    }

    /**
     * 写指定字节的文件字节数组到指定输出流中.
     * 
     * @param fromFileBytes
     *            要写的文件字节数组
     * @param toStream
     *            写到的文件输出流
     * @param closeOutStream
     *            是否关闭输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeBytes2Stream(byte[] fromFileBytes, OutputStream toStream, boolean closeOutStream)
            throws IOException {
        OutputStream bufOs = buffered(toStream);
        bufOs.write(fromFileBytes);
        bufOs.flush();
        if (closeOutStream) {
            toStream.close();
        }
    }

    /**
     * 写指定数据字节的数据到指定文件中<br/>
     * 
     * @param fromBytes
     *            要写的数据字节数组
     * @param toDestPath
     *            写到的文件路径
     * @return 写到的文件路径
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static String writeBytes2Path(byte[] fromBytes, String toDestPath) throws IOException {
        FileOutputStream fos = null;
        FileChannel fc = null;
        try {
            fos = new FileOutputStream(createFile(toDestPath));
            fc = fos.getChannel();
            fc.write(ByteBuffer.wrap(fromBytes));
            fos.flush();
        } finally {
            if (fc != null) {
                fc.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return toDestPath;
    }

    /**
     * 写指定字节的数据到指定文件中.
     * 
     * @param fromBytes
     *            要写的数据文件字节数组
     * @param toDestFile
     *            写到的文件对象
     * @return 写到的文件对象
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static File writeBytes2File(byte[] fromBytes, File toDestFile) throws IOException {
        return new File(writeBytes2Path(fromBytes, createFile(toDestFile).getCanonicalPath()));
    }

    /**
     * 写指定字节的输入流到指定文件中,并关闭输入流.
     * 
     * @param fromStream
     *            输入流
     * @param toDestFile
     *            写到的文件路径
     * @return 写到的文件对象
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static File writeStream2File(InputStream fromStream, File toDestFile) throws IOException {
        return writeStream2File(fromStream, toDestFile, true);
    }

    /**
     * 写指定字节的输入流到指定文件中.
     * 
     * @param fromStream
     *            输入流
     * @param toDestFile
     *            写到的文件路径
     * @param closeInStream
     *            是否关闭输入流
     * @return 写到的文件对象
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static File writeStream2File(InputStream fromStream, File toDestFile, boolean closeInStream)
            throws IOException {
        FileOutputStream fos = null;
        FileChannel fc = null;
        ReadableByteChannel readableByteChannel = null;
        try {
            fos = new FileOutputStream(createFile(toDestFile));
            fc = fos.getChannel();
            readableByteChannel = Channels.newChannel(fromStream);
            long offset = 0;
            long quantum = DEFAULT_BUFFER_SIZE;
            long count = fc.transferFrom(readableByteChannel, offset, quantum);
            while (count > 0) {
                offset += count;
                count = fc.transferFrom(readableByteChannel, offset, quantum);
            }
            fos.flush();
        } finally {
            if (fc != null) {
                fc.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (readableByteChannel != null && closeInStream) {
                readableByteChannel.close();
            }
            if (fromStream != null && closeInStream) {
                fromStream.close();
            }
        }
        return toDestFile;
    }

    /**
     * 写指定字节的输入流到指定路径中，并关闭输入流.
     * 
     * @param fromStream
     *            输入流
     * @param toDestPath
     *            写到的文件路径
     * @return 写到的文件路径
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static String writeStream2Path(InputStream fromStream, String toDestPath) throws IOException {
        return writeStream2File(fromStream, createFile(new File(toDestPath))).getCanonicalPath();
    }

    /**
     * 写指定字节的输入流到指定路径中，并关闭输入流.
     * 
     * @param fromStream
     *            输入流
     * @param toDestPath
     *            写到的文件路径
     * @param closeInStream
     *            是否关闭输入流
     * @return 写到的文件路径
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static String writeStream2Path(InputStream fromStream, String toDestPath, boolean closeInStream)
            throws IOException {
        return writeStream2File(fromStream, createFile(new File(toDestPath)), closeInStream).getCanonicalPath();
    }

    /**
     * 写指定Base64解码后的数据字符串到指定文件中.
     * 
     * @param base64Str
     *            输入的base64编码的数据字符串
     * @param toDestFile
     *            解码后写到的文件对象
     * @return 解码后写到的文件对象
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static File writeBase64Str2File(String base64Str, File toDestFile) throws IOException {
        return writeBytes2File(DatatypeConverter.parseBase64Binary(base64Str), createFile(toDestFile));
    }

    /**
     * 写指定Base64解码后的数据字符串到指定路径中.
     * 
     * @param base64Str
     *            输入的base64编码的数据字符串
     * @param toDestFile
     *            解码后写到的文件路径
     * @return 解码后写到的文件路径
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static String writeBase64Str2Path(String base64Str, String toDestPath) throws IOException {
        return writeBytes2Path(DatatypeConverter.parseBase64Binary(base64Str), createFile(toDestPath));
    }

    /**
     * 写指定字节的输入流到指定的输出流中，并关闭输入流.
     * 
     * @param fromStream
     *            输入流
     * @param toStream
     *            输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeStream2Stream(InputStream fromStream, OutputStream toStream) throws IOException {
        writeStream2Stream(fromStream, DEFAULT_BUFFER_SIZE, toStream, true, false);
    }

    /**
     * 写指定字节的输入流到指定的输出流中，并关闭输入流.
     * 
     * @param fromStream
     *            输入流
     * @param bufferSize
     *            设置读/写的缓冲区大小
     * @param toStream
     *            输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeStream2Stream(InputStream fromStream, int bufferSize, OutputStream toStream)
            throws IOException {
        writeStream2Stream(fromStream, bufferSize, toStream, true, false);
    }

    /**
     * 写指定字节的输入流到指定的输出流中.
     * 
     * @param fromStream
     *            输入流
     * @param bufferSize
     *            设置读/写的缓冲区大小
     * @param toStream
     *            输出流
     * @param closeInStream
     *            是否关闭输入流
     * @param closeOutStream
     *            是否关闭输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeStream2Stream(InputStream fromStream, int bufferSize, OutputStream toStream,
            boolean closeInStream, boolean closeOutStream) throws IOException {
        ReadableByteChannel inputChannel = null;
        WritableByteChannel outputChannel = null;
        ByteBuffer buffer = null;
        try {
            inputChannel = Channels.newChannel(fromStream);
            outputChannel = Channels.newChannel(toStream);
            buffer = ByteBuffer.allocateDirect(bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : bufferSize);
            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                outputChannel.write(buffer);
                buffer.clear();
            }
            toStream.flush();
        } finally {
            if (outputChannel != null && closeOutStream) {
                outputChannel.close();
            }
            if (toStream != null && closeOutStream) {
                toStream.close();
            }
            if (inputChannel != null && closeInStream) {
                inputChannel.close();
            }
            if (fromStream != null && closeInStream) {
                fromStream.close();
            }
            if (buffer != null) {
                buffer.clear();
                buffer = null;
            }
        }
    }

    /**
     * 写指定字节的输入流到指定的输出流中.
     * 
     * @param fromStream
     *            输入流
     * @param toStream
     *            输出流
     * @param closeInStream
     *            是否关闭输入流
     * @param closeOutStream
     *            是否关闭输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeStream2Stream(InputStream fromStream, OutputStream toStream, boolean closeInStream,
            boolean closeOutStream) throws IOException {
        writeStream2Stream(fromStream, DEFAULT_BUFFER_SIZE, toStream, closeInStream, closeOutStream);
    }

    /**
     * 写指定文件对象到指定的输出流中.
     * 
     * @param fromFile
     *            要写的文件对象
     * @param toStream
     *            写到的数据流中
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeFile2Stream(File fromFile, OutputStream toStream) throws IOException {
        writeFile2Stream(fromFile, toStream, false);
    }

    /**
     * 写指定文件对象到指定的输出流中.
     * 
     * @param fromFile
     *            要写的文件对象
     * @param toStream
     *            写到的数据流中
     * @param closeOutStream
     *            是否关闭输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writeFile2Stream(File fromFile, OutputStream toStream, boolean closeOutStream)
            throws IOException {
        InputStream fis = null;
        try {
            fis = new FileInputStream(fromFile);
            writeStream2Stream(fis, toStream);
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (toStream != null && closeOutStream) {
                toStream.close();
            }
        }
    }

    /**
     * 写指定文件路径到指定的输出流中.
     * 
     * @param fromPath
     *            要写的文件路径
     * @param toStream
     *            写到的数据流中
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writePath2Stream(String fromPath, OutputStream toStream) throws IOException {
        writePath2Stream(fromPath, toStream, false);
    }

    /**
     * 写指定文件路径到指定的输出流中.
     * 
     * @param fromPath
     *            要写的文件路径
     * @param toStream
     *            写到的数据流中
     * @param closeOutStream
     *            是否关闭输出流
     * @throws IOException
     *             读取文件异常
     * @author xl
     * @since 1.0.0
     */
    public static void writePath2Stream(String fromPath, OutputStream toStream, boolean closeOutStream)
            throws IOException {
        writeFile2Stream(new File(fromPath), toStream, closeOutStream);
    }

    /**
     * 获取文件MD5值.
     *
     * @param fromPath
     *            输入文件路径
     * @return 该路径下的文件MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static byte[] getFileMd5(String fromPath) {
        if (fromPath == null) {
            return null;
        }
        return getFileMd5(new File(fromPath));
    }

    /**
     * 获取文件MD5值.
     *
     * @param fromFile
     *            输入文件对象
     * @return 该路径下的文件MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static byte[] getFileMd5(File fromFile) {
        if (fromFile == null) {
            return null;
        }
        FileChannel fc = null;
        FileInputStream fis = null;
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(fromFile);
            fc = fis.getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            messagedigest.update(byteBuffer);
            return messagedigest.digest();
        } catch (Throwable e) {
            System.err.println("计算文件MD5异常，原因：" + ExceptionFormater.format(e));
            return null;
        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                    System.err.println("关闭文件读取通道异常，原因：" + ExceptionFormater.format(e));
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.err.println("关闭文件输入流异常，原因：" + ExceptionFormater.format(e));
                }
            }
        }
    }

    /**
     * 获取文件MD5值.
     *
     * @param fromStream
     *            输入文件数据流
     * @return 该数据流的MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static byte[] getFileMd5(InputStream fromStream) {
        if (fromStream == null) {
            return null;
        }
        ReadableByteChannel inputChannel = null;
        ByteBuffer buffer = null;
        try {
            inputChannel = Channels.newChannel(fromStream);
            buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(buffer);
            return messagedigest.digest();
        } catch (Throwable e) {
            System.err.println("计算文件MD5异常，原因：" + ExceptionFormater.format(e));
            return null;
        } finally {
            if (inputChannel != null) {
                try {
                    inputChannel.close();
                } catch (IOException e) {
                    System.err.println("关闭文件读取通道异常，原因：" + ExceptionFormater.format(e));
                }
            }
            if (buffer != null) {
                buffer.clear();
                buffer = null;
            }
        }
    }

    /**
     * 获取文件MD5值.
     *
     * @param fromBytes
     *            输入文件二进制数据
     * @return 该二进制数据的MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static byte[] getFileMd5(byte[] fromBytes) {
        if (fromBytes == null) {
            return null;
        }
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(fromBytes);
            return messagedigest.digest();
        } catch (Throwable e) {
            System.err.println("计算文件MD5异常，原因：" + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 获取文件MD5值,并返回以16进制编码的字符串.
     *
     * @param fromPath
     *            输入文件路径
     * @return 16进制编码的字符串表示的文件MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static String getFileMd5Hex(String fromPath) {
        try {
            return DatatypeConverter.printHexBinary(getFileMd5(fromPath))
                    .toLowerCase();
        } catch (Throwable e) {
            System.err.println("计算文件MD5异常，原因：" + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 获取文件MD5值,并返回以16进制编码的字符串.
     *
     * @param fromFile
     *            输入文件对象
     * @return 16进制编码的字符串表示的文件MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static String getFileMd5Hex(File fromFile) {
        try {
            return DatatypeConverter.printHexBinary(getFileMd5(fromFile))
                    .toLowerCase();
        } catch (Throwable e) {
            System.err.println("计算文件MD5异常，原因：" + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 获取文件MD5值,并返回以16进制编码的字符串.
     *
     * @param fromStream
     *            输入文件数据流
     * @return 16进制编码的字符串表示的文件数据流MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static String getFileMd5Hex(InputStream fromStream) {
        try {
            return DatatypeConverter.printHexBinary(getFileMd5(fromStream))
                    .toLowerCase();
        } catch (Throwable e) {
            System.err.println("计算文件MD5异常，原因：" + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 获取文件MD5值,并返回以16进制编码的字符串.
     *
     * @param fromBytes
     *            输入文件二进制数据
     * @return 16进制编码的字符串表示的文件二进制数据MD5值,失败返回null
     * @author xl
     * @since 1.0.0
     */
    public static String getFileMd5Hex(byte[] fromBytes) {
        try {
            return DatatypeConverter.printHexBinary(getFileMd5(fromBytes))
                    .toLowerCase();
        } catch (Throwable e) {
            System.err.println("计算文件MD5异常，原因：" + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 
     * 从指定url读取数据，如果连接成功但是未读取到数据得到的是一个空的byte[]而不是null
     *
     * @param urlString
     *            url
     * @return 读取结果
     * @author xuLiang
     * @since 1.2.0
     */
    public static byte[] getDataWithUrl(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return FileHandler.readStream2Bytes(conn.getInputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO错误");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 
     * 向指定url发送数据，如果连接成功但是未读取到数据得到的是一个空的byte[]而不是null ,默认contentType为json
     * 
     * @param urlString
     *            url
     * @return 读取结果
     * @author xuLiang
     * @since 1.2.0
     */
    public static byte[] postDataWithUrl(String urlString, String param, String contentType) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            if (contentType == null || contentType.length() == 0) {
                conn.setRequestProperty("Content-Type", "application/json");
            } else {
                conn.setRequestProperty("Content-Type", contentType);
            }
            conn.connect();
            if (param != null) {
                FileHandler.writeBytes2Stream(param.getBytes(), conn.getOutputStream(), true);
            }
            return FileHandler.readStream2Bytes(conn.getInputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO错误");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
