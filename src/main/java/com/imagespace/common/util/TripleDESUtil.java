package com.imagespace.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DESede/ECB/PKCS5Padding的含义：
 * - 第一段是加密算法的名称，如DESede实际上是3-DES。这一段还可以放其它的对称加密算法，如Blowfish等。
 * - 第二段是分组加密的模式，除了CBC和ECB之外，还可以是NONE/CFB/QFB等。最常用的就是CBC和ECB了。
 *   DES采用分组加密的方式，将明文按8字节（64位）分组分别加密。如果每个组独立处理，则是ECB。
 *   CBC的处理方式是先用初始向量IV对第一组加密，再用第一组的密文作为密钥对第二组加密，然后依次完成整个加密操作。
 *   如果明文中有两个分组的内容相同，ECB会得到完全一样的密文，但CBC则不会。
 * - 第三段是指最后一个分组的填充方式。大部分情况下，明文并非刚好64位的倍数。
 *   对于最后一个分组，如果长度小于64位，则需要用数据填充至64位。PKCS5Padding是常用的填充方式，如果没有指定，默认的方式就是它。
 * 补充一点，虽然DES的有效密钥长度是56位，但要求密钥长度是64位（8字节）。3DES则要求24字节。
 *
 * 3DES加密解密
 * @author gusaishuai
 * @since 2018/6/1
 */
@Slf4j
public class TripleDESUtil {

    private static final String DEFAULT_ENCODE = "utf-8";

    public static String encrypt(String message, String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(DEFAULT_ENCODE), "DESede");
            //encrypt
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(message.getBytes(DEFAULT_ENCODE));
            return new String(new Base64().encode(encryptedData), DEFAULT_ENCODE);
        } catch (Exception e) {
            log.warn("TripleDES encrypt error", e);
            return message;
        }
    }

    public static String decrypt(String message, String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(DEFAULT_ENCODE), "DESede");
            //decrypt
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptPlainText = cipher.doFinal(new Base64().decode(message.getBytes(DEFAULT_ENCODE)));
            return new String(decryptPlainText);
        } catch (Exception e) {
            log.warn("TripleDES decrypt error", e);
            return message;
        }
    }

}
