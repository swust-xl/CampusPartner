package org.campus.partner.util.string;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 随机字符制作器.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public class RandomStringMaker {
    private RandomStringMaker() {}

    /**
     * 允许生成随机字符的最大长度.
     *
     * @since 1.0.0
     */
    public static final int MAX_LENGTH = 99999;

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 1个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get1FixedStr() {
        return getRandomStr(1);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 2个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get2FixedStr() {
        return getRandomStr(2);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 3个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get3FixedStr() {
        return getRandomStr(3);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 4个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get4FixedStr() {
        return getRandomStr(4);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 5个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get5FixedStr() {
        return getRandomStr(5);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 6个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get6FixedStr() {
        return getRandomStr(6);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 7个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get7FixedStr() {
        return getRandomStr(7);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 8个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get8FixedStr() {
        return getRandomStr(8);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 9个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get9FixedStr() {
        return getRandomStr(9);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 10个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get10FixedStr() {
        return getRandomStr(10);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 12个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get12FixedStr() {
        return getRandomStr(12);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 14个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get14FixedStr() {
        return getRandomStr(14);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 16个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get16FixedStr() {
        return getRandomStr(16);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 18个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get18FixedStr() {
        return getRandomStr(18);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 20个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get20FixedStr() {
        return getRandomStr(20);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 22个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get22FixedStr() {
        return getRandomStr(22);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 24个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get24FixedStr() {
        return getRandomStr(24);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 26个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get26FixedStr() {
        return getRandomStr(26);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 28个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get28FixedStr() {
        return getRandomStr(28);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 30个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get30FixedStr() {
        return getRandomStr(30);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 32个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get32FixedStr() {
        return getRandomStr(32);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 64个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get64FixedStr() {
        return getRandomStr(64);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 128个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get128FixedStr() {
        return getRandomStr(128);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 256个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get256FixedStr() {
        return getRandomStr(256);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 512个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get512FixedStr() {
        return getRandomStr(512);
    }

    /**
     * 数字和字母(大小写)组合的随机字符串.
     *
     * @return 1024个字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get1024FixedStr() {
        return getRandomStr(1024);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 1个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get1FixedNumberStr() {
        return getNumberRandomStr(1);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 2个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get2FixedNumberStr() {
        return getNumberRandomStr(2);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 3个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get3FixedNumberStr() {
        return getNumberRandomStr(3);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 4个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get4FixedNumberStr() {
        return getNumberRandomStr(4);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 5个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get5FixedNumberStr() {
        return getNumberRandomStr(5);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 6个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get6FixedNumberStr() {
        return getNumberRandomStr(6);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 7个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get7FixedNumberStr() {
        return getNumberRandomStr(7);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 8个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get8FixedNumberStr() {
        return getNumberRandomStr(8);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 9个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get9FixedNumberStr() {
        return getNumberRandomStr(9);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 10个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get10FixedNumberStr() {
        return getNumberRandomStr(10);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 12个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get12FixedNumberStr() {
        return getNumberRandomStr(12);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 14个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get14FixedNumberStr() {
        return getNumberRandomStr(14);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 16个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get16FixedNumberStr() {
        return getNumberRandomStr(16);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 18个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get18FixedNumberStr() {
        return getNumberRandomStr(18);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 20个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get20FixedNumberStr() {
        return getNumberRandomStr(20);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 22个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get22FixedNumberStr() {
        return getNumberRandomStr(22);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 24个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get24FixedNumberStr() {
        return getNumberRandomStr(24);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 26个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get26FixedNumberStr() {
        return getNumberRandomStr(26);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 28个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get28FixedNumberStr() {
        return getNumberRandomStr(28);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 30个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get30FixedNumberStr() {
        return getNumberRandomStr(30);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 32个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get32FixedNumberStr() {
        return getNumberRandomStr(32);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 64个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get64FixedNumberStr() {
        return getNumberRandomStr(64);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 128个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get128FixedNumberStr() {
        return getNumberRandomStr(128);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 256个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get256FixedNumberStr() {
        return getNumberRandomStr(256);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 512个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get512FixedNumberStr() {
        return getNumberRandomStr(512);
    }

    /**
     * 数字组合的随机数字符串.
     *
     * @return 1024个数字字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get1024FixedNumberStr() {
        return getNumberRandomStr(1024);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 1个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get1FixedLetterStr() {
        return getLetterRandomStr(1);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 2个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get2FixedLetterStr() {
        return getLetterRandomStr(2);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 3个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get3FixedLetterStr() {
        return getLetterRandomStr(3);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 4个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get4FixedLetterStr() {
        return getLetterRandomStr(4);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 5个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get5FixedLetterStr() {
        return getLetterRandomStr(5);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 6个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get6FixedLetterStr() {
        return getLetterRandomStr(6);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 7个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get7FixedLetterStr() {
        return getLetterRandomStr(7);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 8个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get8FixedLetterStr() {
        return getLetterRandomStr(8);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 9个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get9FixedLetterStr() {
        return getLetterRandomStr(9);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 10个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get10FixedLetterStr() {
        return getLetterRandomStr(10);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 12个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get12FixedLetterStr() {
        return getLetterRandomStr(12);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 14个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get14FixedLetterStr() {
        return getLetterRandomStr(14);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 16个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get16FixedLetterStr() {
        return getLetterRandomStr(16);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 18个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get18FixedLetterStr() {
        return getLetterRandomStr(18);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 20个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get20FixedLetterStr() {
        return getLetterRandomStr(20);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 22个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get22FixedLetterStr() {
        return getLetterRandomStr(22);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 24个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get24FixedLetterStr() {
        return getLetterRandomStr(24);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 26个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get26FixedLetterStr() {
        return getLetterRandomStr(26);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 28个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get28FixedLetterStr() {
        return getLetterRandomStr(28);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 30个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get30FixedLetterStr() {
        return getLetterRandomStr(30);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 32个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get32FixedLetterStr() {
        return getLetterRandomStr(32);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 64个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get64FixedLetterStr() {
        return getLetterRandomStr(64);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 128个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get128FixedLetterStr() {
        return getLetterRandomStr(128);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 256个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get256FixedLetterStr() {
        return getLetterRandomStr(256);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 512个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get512FixedLetterStr() {
        return getLetterRandomStr(512);
    }

    /**
     * 字母组合(包含大小写)的随机字符串.
     *
     * @return 1024个字母组合(包含大小写)字符的随机字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String get1024FixedLetterStr() {
        return getLetterRandomStr(1024);
    }

    /**
     * 生成数字和字母的随机位数的组合.
     * 
     * @param length
     *            随机字母数字组合的字符串长度.
     * @return 数字和字母的随机组合字符串.
     */
    public static String getRandomStr(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException(length + " not illegal! it must greater than 0!");
        }
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException(length + " not illegal! it must less than " + MAX_LENGTH);
        }
        // lower case: [97,122]
        // upper case: [65,90]
        // number: [48,57]
        Random random = getRandom();
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < length; i++) {
            int chooseCase = random.nextInt(3);
            switch (chooseCase) {
            case 0:// number
                sb.append(random.nextInt(10));
                break;
            case 1:// lower case
                sb.append((char) (random.nextInt(26) + 97));
                break;
            case 2:// upper case
                sb.append((char) (random.nextInt(26) + 65));
                break;
            default:
                sb.append(random.nextInt(10));
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 生成数字的随机位数的组合.
     * 
     * @param length
     *            随机数字组合的字符串长度.
     * @return 数字的随机组合字符串.
     */
    public static String getNumberRandomStr(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException(length + " not illegal! it must greater than 0!");
        }
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException(length + " not illegal! it must less than " + MAX_LENGTH);
        }
        // number: [48,57]
        Random random = getRandom();
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < length; i++) {
            int zero2nine = random.nextInt(10);
            sb.append(zero2nine);
        }
        return sb.toString();
    }

    /**
     * 生成字母(包含大小写)的随机位数的组合.
     * 
     * @param length
     *            随机字母(包含大小写)组合的字符串长度.
     * @return 字母(包含大小写)的随机组合字符串.
     */
    public static String getLetterRandomStr(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException(length + " not illegal! it must greater than 0!");
        }
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException(length + " not illegal! it must less than " + MAX_LENGTH);
        }
        // lower case: [97,122]
        // upper case: [65,90]
        Random random = getRandom();
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < length; i++) {
            int chooseCase = random.nextInt(2);
            switch (chooseCase) {
            case 0:// lower case
                sb.append((char) (random.nextInt(26) + 97));
                break;
            case 1:// upper case
                sb.append((char) (random.nextInt(26) + 65));
                break;
            default:
                sb.append((char) (random.nextInt(26) + 97));
                break;
            }
        }
        return sb.toString();
    }

    private static Random getRandom() {
        Random random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (Throwable e) {
            random = new Random();
        }
        return random;
    }

    /**
     * 以相同字符生成指定长度的字符串.
     *
     * @param baseChar
     *            待生成目标字符串的基础字符.
     * @param length
     *            需要生成的字符长度.
     * @return 长度为{@code length}的具有相同{@code baseChar}的字符串.
     * @author xl
     * @since 1.0.0
     */
    public static String getFixedStr(char baseChar, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException(length + " not illegal! it must greater than 0!");
        }
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException(length + " not illegal! it must less than " + MAX_LENGTH);
        }
        StringBuilder sb = new StringBuilder(baseChar);
        for (int i = 0; i < length; i++) {
            sb.append(baseChar);
        }
        return sb.toString();
    }
}
