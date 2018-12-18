package com.imagespace.common.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

/**
 * 返回流数据，例如：图片流、下载流等
 * @author gusaishuai
 * @since 2018/12/16
 */
@Setter
@Getter
public class MediaCallResult extends CallResult {

    private static final long serialVersionUID = 1914269339510678931L;

    private byte[] stream;
    /**
     * @see MediaType
     * jpg：MediaType.IMAGE_JPEG_VALUE
     * file：MediaType.APPLICATION_OCTET_STREAM_VALUE
     */
    private String mediaType;
    /**
     * 附件下载名称（如果页面直接显示，则置为空）
     */
    private String fileName;

    public MediaCallResult(byte[] stream, String mediaType, String fileName) {
        super();
        this.stream = stream;
        this.mediaType = mediaType;
        this.fileName = fileName;
    }

}
