package jp.co.ntt.atrs.domain.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

/**
 * 画像変換に関するユーティリティクラス。
 */
@Component
public class ImageFileBase64Encoder {

    /**
     * 画像データのBase64変換を行う。
     * @param inputStream 画像データ
     * @param fileExtension ファイル拡張子
     * @return 変換後の文字列
     * @throws IOException
     */
    public String encodeBase64(InputStream inputStream,
            String fileExtension) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        image.flush();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Base64.Encoder encoder = Base64.getEncoder();
        try (OutputStream encodedOs = encoder.wrap(baos)) {
            ImageIO.write(image, fileExtension, encodedOs);
        }
        return new String(baos.toByteArray());
    }
}